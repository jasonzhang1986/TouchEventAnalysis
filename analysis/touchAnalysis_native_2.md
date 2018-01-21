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

    ...

    // Send out a message that the describes the changed input devices.
    if (inputDevicesChanged) {//输入设备发生改变
        mPolicy->notifyInputDevicesChanged(inputDevices);
    }

    //发送事件到 InputDispatcher
    mQueuedListener->flush();
}
```
