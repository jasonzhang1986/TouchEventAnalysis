>回顾：在上一篇文章中，Native 层的 InputManager.initialize 创建了 InputReaderThread 和 InputDispatherThread 两个线程，并在 start 方法中分别启动了两个线程

### InputReader
今天从 InputReaderThread 的执行过程中的 threadLoop 为起点开始分析。
###InputReaderThread.threadLoop
[InputReader.cpp]
```c
bool InputReaderThread::threadLoop() {
    mReader->loopOnce();
    return true;
}
```
>threadLoop 返回 True 代表的是会不断地循环调用 loopOnce()，反之如果返回值为 False 时则会退出循环。

这里返回的是 True 那么整个过程就是不断循环的调用 InputReader 的 loopOnce() 方法。

#### InputReader.loopOnce
```c
void InputReader::loopOnce() {
    ...
    bool inputDevicesChanged = false;

    ...

    //从EventHub读取事件
    size_t count = mEventHub->getEvents(timeoutMillis, mEventBuffer, EVENT_BUFFER_SIZE);

    if (count) {
        processEventsLocked(mEventBuffer, count);
    }

    ...

    // Send out a message that the describes the changed input devices.
    if (inputDevicesChanged) {//输入设备发生改变
        mPolicy->notifyInputDevicesChanged(inputDevices);
    }

    //发送事件到 InputDispatcher
    mQueuedListener->flush();
}
```
EventHub 的 getEvent 是获取和转换 input_event，一起来进入代码一看究竟

在分析 getEvent 是如何获取 intput_event 之前，先来了解下 EventHub 是如何监听 /dev/input 下的设备的

#### scanDevicesLocked
```c
void EventHub::scanDevicesLocked() {
    //此处DEVICE_PATH="/dev/input"
    status_t res = scanDirLocked(DEVICE_PATH);
    ...
}
```
#### scanDirLocked
```c
status_t EventHub::scanDirLocked(const char *dirname)
{
    char devname[PATH_MAX];
    char * filename;
    DIR * dir;
    struct dirent * de;
    dir = opendir(dirname);
    if(dir == NULL)
        return -1;
    strcpy(devname, dirname);
    filename = devname + strlen(devname);
    * filename++ = '/';
    //读取/dev/input/目录下所有的设备节点
    while((de = readdir(dir))) {
        if(de->d_name[0] == '.' &&
           (de->d_name[1] == '\0' ||
            (de->d_name[1] == '.' && de->d_name[2] == '\0')))
            continue;
        strcpy(filename, de->d_name);
        //打开相应的设备节点
        openDeviceLocked(devname);
    }
    closedir(dir);
    return 0;
}
```
#### openDeviceLocked
```c
status_t EventHub::openDeviceLocked(const char *devicePath) {
    //打开设备文件
    int fd = open(devicePath, O_RDWR | O_CLOEXEC);
    //获取设备名
    if(ioctl(fd, EVIOCGNAME(sizeof(buffer) - 1), &buffer) < 1){
    } else {
        buffer[sizeof(buffer) - 1] = '\0';
        identifier.name.setTo(buffer);
    }

    ...

    Device* device = new Device(fd, deviceId, String8(devicePath), identifier);

    ...

    //注册epoll
    struct epoll_event eventItem;
    memset(&eventItem, 0, sizeof(eventItem));
    eventItem.events = EPOLLIN;
    if (mUsingEpollWakeup) {
        eventItem.events |= EPOLLWAKEUP;
    }
    eventItem.data.u32 = deviceId;
    if (epoll_ctl(mEpollFd, EPOLL_CTL_ADD, fd, &eventItem)) {
        delete device; //添加失败则删除该设备
        return -1;
    }

    ...

    addDeviceLocked(device);
}
```
#### addDeviceLocked
```c
void EventHub::addDeviceLocked(Device* device) {
    mDevices.add(device->id, device); //添加到mDevices队列
    device->next = mOpeningDevices;
    mOpeningDevices = device;
}
```
由上面几个方法可以看出 EventHub 扫描和添加设备到 mDevices 队列的大概过程，接下来继续 loopOnce 中的 getEvents 方法的分析
#### EventHub.getEvents
[EventHub.cpp]
```c
size_t EventHub::getEvents(int timeoutMillis, RawEvent* buffer, size_t bufferSize) {
    ...

    struct input_event readBuffer[bufferSize];
    RawEvent* event = buffer;
    size_t capacity = bufferSize;
    bool awoken = false;
    //大循环，在遇到错误或者 timeout 或者 awoken = true 时被break
    for (;;) {
        ...

        // Grab the next input event.
        bool deviceChanged = false;
        while (mPendingEventIndex < mPendingEventCount) {

          省略一堆代码 ...

        }

        // readNotify() will modify the list of devices so this must be done after
        // processing all other events to ensure that we read all remaining events
        // before closing the devices.
        if (mPendingINotify && mPendingEventIndex >= mPendingEventCount) {
            mPendingINotify = false;
            readNotifyLocked();
            deviceChanged = true;
        }

        // Report added or removed devices immediately.
        if (deviceChanged) {
            continue;
        }

        // Return now if we have collected any events or if we were explicitly awoken.
        if (event != buffer || awoken) {
            break;
        }

        mPendingEventIndex = 0;

        mLock.unlock(); // release lock before poll, must be before release_wake_lock
        release_wake_lock(WAKE_LOCK_ID);

        //epoll_wait 等待事件触发，当超过 timeout 还没有事件触发时就超时
        //如果有事件触发，事件集合在 mPendingEventItems 中
        //返回值是监听到的事件个数
        int pollResult = epoll_wait(mEpollFd, mPendingEventItems, EPOLL_MAX_EVENTS, timeoutMillis);

        acquire_wake_lock(PARTIAL_WAKE_LOCK, WAKE_LOCK_ID);
        mLock.lock(); // reacquire lock after poll, must be after acquire_wake_lock

        if (pollResult == 0) { // epoll_wait 返回值等于 0 表示超时
            // Timed out.
            mPendingEventCount = 0;
            break;
        }

        if (pollResult < 0) { // epoll_wait 返回值小于 0 表示出错了
            mPendingEventCount = 0;

            // Sleep after errors to avoid locking up the system.
            // Hopefully the error is transient.
            if (errno != EINTR) {
                ALOGW("poll failed (errno=%d)\n", errno);
                usleep(100000);
            }
        } else {
            //监听到事件，epoll_wait 的返回值 pollResult 是事件个数
            mPendingEventCount = size_t(pollResult);
        }
    } // end of for(; ;)
    // All done, return the number of events we read.
    return event - buffer; //返回所读取的事件个数
}
```
可以看出 getEvents 的代码大框架是通过 for 循环中通过 epoll_wait 监听设备，收到事件后，通过内部循环 while 来处理每个事件

**处理监听到事件序列的 while 循环**
```c
bool deviceChanged = false;
while (mPendingEventIndex < mPendingEventCount) {
    const struct epoll_event& eventItem = mPendingEventItems[mPendingEventIndex++];
    //如果是 EPOLL_ID_INOTIFY 事件，表示设备有变化
    //从 EventHub的构造方法中可以看到监听的是 IN_DELETE | IN_CREATE
    if (eventItem.data.u32 == EPOLL_ID_INOTIFY) {
        if (eventItem.events & EPOLLIN) {
            mPendingINotify = true;
        } else {
            ALOGW("Received unexpected epoll event 0x%08x for INotify.", eventItem.events);
        }
        continue;
    }
    //如果是 EPOLL_ID_WAKE 事件，说明有人往mWakeWritePipeFd上写东西
    //mWakeReadPipeFd 有东西可以读了
    if (eventItem.data.u32 == EPOLL_ID_WAKE) {
        if (eventItem.events & EPOLLIN) {
            ALOGV("awoken after wake()");
            awoken = true;
            char buffer[16];
            ssize_t nRead;
            do {
                //读取信息
                nRead = read(mWakeReadPipeFd, buffer, sizeof(buffer));
            } while ((nRead == -1 && errno == EINTR) || nRead == sizeof(buffer));
        } else {
            ALOGW("Received unexpected epoll event 0x%08x for wake read pipe.",
                    eventItem.events);
        }
        continue;

        ssize_t deviceIndex = mDevices.indexOfKey(eventItem.data.u32);
        //找到需要读取 input_event 的 device
        Device* device = mDevices.valueAt(deviceIndex);
        if (eventItem.events & EPOLLIN) {
            //读取 input_event
            int32_t readSize = read(device->fd, readBuffer,
                    sizeof(struct input_event) * capacity);

            ...

            size_t count = size_t(readSize) / sizeof(struct input_event);
            for (size_t i = 0; i < count; i++) {
                //获取 input_event 数据
                struct input_event& iev = readBuffer[i];

                //将input_event信息, 封装成RawEvent
                event->when = nsecs_t(iev.time.tv_sec) * 1000000000LL
                        + nsecs_t(iev.time.tv_usec) * 1000LL;
                event->deviceId = deviceId;
                event->type = iev.type;
                event->code = iev.code;
                event->value = iev.value;
                event += 1;
                capacity -= 1;
            }
            if (capacity == 0) {
                mPendingEventIndex -= 1;
                break;
            }
    }
}
```
通过 EventHub 的构造方法和 getEvents 方法可以很明显的看出 EventHub 采用 INotify + epoll 机制实现监听目录 /dev/input 下的设备节点，经过 EventHub将 input_event 结构体 + deviceId 转换成 RawEvent 结构体
#### RawEvent
[InputEventReader.h]
```c
struct input_event {
 struct timeval time; //事件发生的时间点
 __u16 type;
 __u16 code;
 __s32 value;
};
```
[EventHub.h]
```c
/*
 * A raw event as retrieved from the EventHub.
 */
struct RawEvent {
    nsecs_t when; //事件发生的事件点
    int32_t deviceId; //产生事件的设备id
    int32_t type; //事件类型
    int32_t code;
    int32_t value;
};
```
以上介绍的是 EventHub 扫描设备节点的变化并从设备节点获取事件的流程，当收到事件之后接下来的便是处理事件

####processEventsLocked
[InputReader.cpp]
