> 本篇主要分析从 InputEventReceiver 收到消息后，如何层层传递到 Activity 的

#### InputEventReceiver.dispatchInputEvent
```Java
private void dispatchInputEvent(int seq, InputEvent event) {
    mSeqMap.put(event.getSequenceNumber(), seq);
    onInputEvent(event);
}
```
而 InputEventReceiver 是个 abstract 的， WindowInputEventReceiver 继承自 InputEventReceiver， dispatchInputEvent 方法主要是调用 onInputEvent，那应该是来到 WindowInputEventReceiver 的 onInputEvent 方法

#### WindowInputEventReceiver
[ViewRootImpl.java]
```Java
final class WindowInputEventReceiver extends InputEventReceiver {
  public WindowInputEventReceiver(InputChannel inputChannel, Looper looper) {
      super(inputChannel, looper);
  }

  @Override
  public void onInputEvent(InputEvent event) {
      /// M: record current key event and motion event to dump input event info for
      /// ANR analysis. @{
      if (event instanceof KeyEvent) {
          mCurrentKeyEvent = (KeyEvent) event;
          mKeyEventStartTime = System.currentTimeMillis();
          mKeyEventStatus = INPUT_DISPATCH_STATE_STARTED;
      } else {
          mCurrentMotion = (MotionEvent) event;
          mMotionEventStartTime = System.currentTimeMillis();
          mMotionEventStatus = INPUT_DISPATCH_STATE_STARTED;
      }
      /// @}
      enqueueInputEvent(event, this, 0, true);
  }
}
```
当一个输入事件产生时（这里认为是触摸事件），会回调InputEventReceiver.onInputEvent()。从名字也可以看出，它是接收输入事件的。然后进一步调用 ViewRootImpl.enqueueInputEvent() 将输入事件加入单链表队列。

#### enqueueInputEvent
```Java
void enqueueInputEvent(InputEvent event,
        InputEventReceiver receiver, int flags, boolean processImmediately) {

    ...

    if (processImmediately) { //立即处理
        doProcessInputEvents();
    } else {
        scheduleProcessInputEvents();
    }
}
```
#### doProcessInputEvents
```Java
void doProcessInputEvents() {
    while (mPendingInputEventHead != null) {
        ...
        deliverInputEvent(q);
        //进一步派发事件处理
        ...
    }
}
```
在这个方法中有一个 while 循环。它会将事件队列循环处理，直到队列中没有数据为止。如何处理呢？我们看看 deliverInputEvent()

#### deliverInputEvent
```Java
private void deliverInputEvent(QueuedInputEvent q) {
    ...

    InputStage stage;
    if (q.shouldSendToSynthesizer()) {
        stage = mSyntheticInputStage;
    } else {
        stage = q.shouldSkipIme() ? mFirstPostImeInputStage : mFirstInputStage;
    }

    if (stage != null) {
        //派发事件到InputStage中处理
        stage.deliver(q);
    } else {
        finishInputEvent(q);
    }
}
```
简单说一下 InputStage, InputStage 是在 ViewRootImpl.setView 时创建的，InputStage 是一个单链表结构，next 指向构造时传递进来的 InputStage 对象，这里我们来看 ViewPostImeInputStage 是如何处理的。

#### ViewPostImeInputStage
```Java
final class ViewPostImeInputStage extends InputStage {

    public final void deliver(QueuedInputEvent q) {
        ...
        apply(q, onProcess(q));//onProcess()中处理处理事件
        ...
    }

    @Override
    protected int onProcess(QueuedInputEvent q) {
        ...
        return processPointerEvent(q); //处理点触摸事件
    }

    private int processPointerEvent(QueuedInputEvent q) {
        final MotionEvent event = (MotionEvent)q.mEvent;
        ...
        final View eventTarget =
            (event.isFromSource(InputDevice.SOURCE_MOUSE) && mCapturingView != null) ?
            mCapturingView :  
            mView;  //DecorView
        ...
        //eventTarget一般取值是mView，即DecorView
        boolean handled = eventTarget.dispatchPointerEvent(event);        
        ...
        return handled ? FINISH_HANDLED : FORWARD;
    }    
}
```
省略了部分不重要的代码，可以比较清晰的看到过程很简单。ViewPostImeInputStage.deliver 调用后，会进一步调用 onProcess 处理。对于点触摸事件会再进一步调用 processPointerEvent 处理，并且在这个方法中，通过 eventTarget.dispatchPointerEvent(event) 将触摸事件传递给了 DecorView 的 dispatchPointerEvent() 处理。

现在越来越清晰了。触摸事件几经辗转终于传递到了 View 上。赶紧接着看看触摸事件后面的旅途是怎样的？

### 奇怪的辗转
上面说到触摸事件传递到了 DecorView.dispatchPointerEvent 中，不多说跟进去看这个方法中发生了什么？ 发现 DecorView 中 没有 dispatchPointerEvent, 事实上，这里是执行的是 View 类的 dispatchPointerEvent
```Java
public final boolean dispatchPointerEvent(MotionEvent event) {
    if (event.isTouchEvent()) {
        return dispatchTouchEvent(event);
    } else {
        return dispatchGenericMotionEvent(event);
    }
}
```
这里 event 是 TouchEvent，转到 dispatchTouchEvent, 这就更熟悉了吧。好，回到 DecorView 的 dispatchTouchEvent

```Java
@Override
public boolean dispatchTouchEvent(MotionEvent ev) {
    final Window.Callback cb = mWindow.getCallback();
    return cb != null && !mWindow.isDestroyed() && mFeatureId < 0
            ? cb.dispatchTouchEvent(ev) : super.dispatchTouchEvent(ev);
}
```
这里 cb 是指 Window 的内部接口 Callback， 由于Activity 实现了Window.Callback接口， 所以接下来调用 Activity.dispatchTouchEvent
```Java
public boolean dispatchTouchEvent(MotionEvent ev) {
    ...
    传递给 Window 的 superDispatchTouchEvent
    if (getWindow().superDispatchTouchEvent(ev)) {        
        return true;
    }
    return onTouchEvent(ev);
}
```
这里的 Window 是 PhoneWindow，那 PhoneWindow 的 superDispatchTouchEvent 有发生了什么？跟进去
```Java
@Override
public boolean superDispatchTouchEvent(MotionEvent event) {
    boolean handled = mDecor.superDispatchTouchEvent(event);    
    return handled;
}
```
有没有发现，这里又回到了 DecorView 中，这次调用的是superDispatchTouchEvent
```java
public boolean superDispatchTouchEvent(MotionEvent event) {
    return super.dispatchTouchEvent(event);
}
```
来回绕啊绕，终于到了耳熟能详的触摸事件分发流程了，即从 ViewGroup 的 dispatchTouchEvent()

>那问题来了，为什么绕这一圈呢？ 为什么 InputStage 不直接把 Event 传给 Activity 处理？

我的理解可能是为了解耦吧，ViewRootImpl 其实是不知道 Activity 的存在的，但它持有 DecorView 的引用，所以需要经过 DecorView， DecorView 起到了中间桥梁的作用。

**[下一篇 frameworks](touchAnalysis_framework.md)**
