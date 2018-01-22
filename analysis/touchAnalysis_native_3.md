#### Looper.wake
[]
```c
void Looper::wake() {
412    uint64_t inc = 1;
413    ssize_t nWrite = TEMP_FAILURE_RETRY(write(mWakeEventFd, &inc, sizeof(uint64_t)));
414    if (nWrite != sizeof(uint64_t)) {
415        if (errno != EAGAIN) {
416            ALOGW("Could not write wake signal: %s", strerror(errno));
        }
    }
}
```
调用enqueueInboundEventLocked()方法来决定是否需要将数字1写入句柄mWakeEventFd来唤醒InputDispatcher线程. 
