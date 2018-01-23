### InputDispater
同样从 InputDispaterThread 执行过程中的 threadLoop 为起点开始分析。

#### threadLoop
```c
bool InputDispatcherThread::threadLoop() {
    mDispatcher->dispatchOnce();
    return true;
}
```
>threadLoop 返回 True 代表的是会不断地循环调用 loopOnce()，反之如果返回值为 False 时则会退出循环。

#### dispatchOnce
```c
void InputDispatcher::dispatchOnce() {

     ...

    // Run a dispatch loop if there are no pending commands.
    // The dispatch loop might enqueue commands to run afterwards.
    if (!haveCommandsLocked()) {
        dispatchOnceInnerLocked(&nextWakeupTime);
    }

    ...

    // Wait for callback or timeout or wake.  (make sure we round up, not down)
    nsecs_t currentTime = now();
    int timeoutMillis = toMillisecondTimeoutDelay(currentTime, nextWakeupTime);
    mLooper->pollOnce(timeoutMillis);
}
```
线程执行Looper->pollOnce，进入 epoll_wait 等待状态，当发生以下任一情况则退出等待状态：
  1. callback：通过回调方法来唤醒
  2. timeout：到达 nextWakeupTime 时间，超时唤醒
  3. wake: 主动调用 Looper 的 wake() 方法

#### dispatchOnceInnerLocked
```c
void InputDispatcher::dispatchOnceInnerLocked(nsecs_t* nextWakeupTime) {

    ...

    // Now we have an event to dispatch.
    // All events are eventually dequeued and processed this way, even if we intend to drop them.
    ALOG_ASSERT(mPendingEvent != NULL);
    bool done = false;

    switch (mPendingEvent->type) {
      ...
    case EventEntry::TYPE_KEY: {
        KeyEntry* typedEntry = static_cast<KeyEntry*>(mPendingEvent);

        ...

        //分发key事件
        done = dispatchKeyLocked(currentTime, typedEntry, &dropReason, nextWakeupTime);
        break;
    }

    case EventEntry::TYPE_MOTION: {
        MotionEntry* typedEntry = static_cast<MotionEntry*>(mPendingEvent);

        ...
        //分发Motion时间
        done = dispatchMotionLocked(currentTime, typedEntry,
                &dropReason, nextWakeupTime);
        break;
    }

    default:
        ALOG_ASSERT(false);
        break;
    }

    if (done) {
        if (dropReason != DROP_REASON_NOT_DROPPED) {    
            dropInboundEventLocked(mPendingEvent, dropReason);
        }
        mLastDropReason = dropReason;

        releasePendingEventLocked();
        * nextWakeupTime = LONG_LONG_MIN;  // force next poll to wake up immediately
    }
}

```
我们还是以 Touch 事件为例，这里是执行 dispatchMotionLocked
#### dispatchMotionLocked
```c
bool InputDispatcher::dispatchMotionLocked(
        nsecs_t currentTime, MotionEntry* entry, DropReason* dropReason, nsecs_t* nextWakeupTime) {

    ...

    // Dispatch the motion.
    ...
    dispatchEventLocked(currentTime, entry, inputTargets);
    return true;
}
```
其中省略了部分不重要的代码，主要功能是调用处理 Event 事件的方法 dispatchEventLocked
#### dispatchEventLocked
```c
void InputDispatcher::dispatchEventLocked(nsecs_t currentTime,
        EventEntry* eventEntry, const Vector<InputTarget>& inputTargets) {

    ...

    for (size_t i = 0; i < inputTargets.size(); i++) {
        const InputTarget& inputTarget = inputTargets.itemAt(i);

        ssize_t connectionIndex = getConnectionIndexLocked(inputTarget.inputChannel);
        if (connectionIndex >= 0) {
            sp<Connection> connection = mConnectionsByFd.valueAt(connectionIndex);
            prepareDispatchCycleLocked(currentTime, connection, eventEntry, &inputTarget);
        }
    }
}
```
主要功能是找到对应的 connection ， 接下来的调用链是
```
-> prepareDispatchCycleLocked
   -> enqueueDispatchEntriesLocked
      -> startDispatchCycleLocked
```
#### startDispatchCycleLocked
```c
void InputDispatcher::startDispatchCycleLocked(nsecs_t currentTime,
        const sp<Connection>& connection) {

    while (connection->status == Connection::STATUS_NORMAL
            && !connection->outboundQueue.isEmpty()) {
        DispatchEntry* dispatchEntry = connection->outboundQueue.head;
        dispatchEntry->deliveryTime = currentTime;

        // Publish the event.
        status_t status;
        EventEntry* eventEntry = dispatchEntry->eventEntry;
        switch (eventEntry->type) {
        case EventEntry::TYPE_KEY: {
            KeyEntry* keyEntry = static_cast<KeyEntry*>(eventEntry);

            // Publish the key event.
            status = connection->inputPublisher.publishKeyEvent(dispatchEntry->seq,
                    keyEntry->deviceId, keyEntry->source,
                    dispatchEntry->resolvedAction, dispatchEntry->resolvedFlags,
                    keyEntry->keyCode, keyEntry->scanCode,
                    keyEntry->metaState, keyEntry->repeatCount, keyEntry->downTime,
                    keyEntry->eventTime);
            break;
        }

        case EventEntry::TYPE_MOTION: {
            MotionEntry* motionEntry = static_cast<MotionEntry*>(eventEntry);

            PointerCoords scaledCoords[MAX_POINTERS];
            const PointerCoords* usingCoords = motionEntry->pointerCoords;

            // 设置 touch 事件的坐标
            float xOffset, yOffset, scaleFactor;

            ...

            // Publish the motion event.
            status = connection->inputPublisher.publishMotionEvent(dispatchEntry->seq,
                    motionEntry->deviceId, motionEntry->source,
                    dispatchEntry->resolvedAction, motionEntry->actionButton,
                    dispatchEntry->resolvedFlags, motionEntry->edgeFlags,
                    motionEntry->metaState, motionEntry->buttonState,
                    xOffset, yOffset, motionEntry->xPrecision, motionEntry->yPrecision,
                    motionEntry->downTime, motionEntry->eventTime,
                    motionEntry->pointerCount, motionEntry->pointerProperties,
                    usingCoords);
            break;
        }

        default:
            ALOG_ASSERT(false);
            return;
        }

       ...

    }
}
```
很明显的看出是分不同的事件类型发布 Event

#### inputPublisher.publishKeyEvent
[InputTransport.cpp]
```c
status_t InputPublisher::publishMotionEvent(
        uint32_t seq,
        int32_t deviceId,
        int32_t source,
        int32_t action,
        int32_t actionButton,
        int32_t flags,
        int32_t edgeFlags,
        int32_t metaState,
        int32_t buttonState,
        float xOffset,
        float yOffset,
        float xPrecision,
        float yPrecision,
        nsecs_t downTime,
        nsecs_t eventTime,
        uint32_t pointerCount,
        const PointerProperties* pointerProperties,
        const PointerCoords* pointerCoords) {

    ...

    InputMessage msg;
    msg.header.type = InputMessage::TYPE_MOTION;
    msg.body.motion.seq = seq;
    msg.body.motion.deviceId = deviceId;
    msg.body.motion.source = source;
    msg.body.motion.action = action;
    msg.body.motion.actionButton = actionButton;
    msg.body.motion.flags = flags;
    msg.body.motion.edgeFlags = edgeFlags;
    msg.body.motion.metaState = metaState;
    msg.body.motion.buttonState = buttonState;
    msg.body.motion.xOffset = xOffset;
    msg.body.motion.yOffset = yOffset;
    msg.body.motion.xPrecision = xPrecision;
    msg.body.motion.yPrecision = yPrecision;
    msg.body.motion.downTime = downTime;
    msg.body.motion.eventTime = eventTime;
    msg.body.motion.pointerCount = pointerCount;
    for (uint32_t i = 0; i < pointerCount; i++) {
        msg.body.motion.pointers[i].properties.copyFrom(pointerProperties[i]);
        msg.body.motion.pointers[i].coords.copyFrom(pointerCoords[i]);
    }
    //把事件转换为 Message 通过 InputChannel 发送出去
    return mChannel->sendMessage(&msg);
}
```
#### InputChannel.sendMessage
[InputTransport.cpp]
```c
status_t InputChannel::sendMessage(const InputMessage* msg) {
    size_t msgLength = msg->size();
    ssize_t nWrite;
    do {
        nWrite = ::send(mFd, msg, msgLength, MSG_DONTWAIT | MSG_NOSIGNAL);
    } while (nWrite == -1 && errno == EINTR);
    ...
}
```
向目标mFd写入消息，会唤醒处于 epoll_wait 状态的 UI 线程。其实是 UI 线程的 Looper，熟悉 Looper 的应该知道，Looper 的 pollInner 也是在 epoll_wait 等待事件，此时就中断了 Looper 的等待并开始处理消息，其中的代码挺多，这里简单列一下调用流程
```
->Looper::pollInner
  -> NativeInputEventReceiver::handleEvent [android_view_InputEventReceiver.cpp]
    -> NativeInputEventReceiver::consumeEvents [android_view_InputEventReceiver.cpp]
      -> InputConsumer::consume  [InputTransport.cpp]
        -> InputChannel::receiveMessage [InputTransport.cpp]
          -> InputEventReceiver.dispachInputEvent [InputEventReceiver.java]
```
到 InputEventReceiver 终于回到了 Java 层, 下一篇会从 InputEventReceiver 收到事件消息开始分析如何传递给 Activity 。

**[下一篇 中间层部分](touchAnalysis_ipc.md)**
