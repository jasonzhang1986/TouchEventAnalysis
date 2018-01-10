## TouchEventAnalysis
本次的目的是通过跟踪一组用户的 Touch 事件，整个事件会经历物理产生、硬件层检测、驱动层处理、Framework 层分发， 应用层处理几个部分。
1. 事件的物理产生比较容易理解，就是我们的手指在屏幕上做出按压、拖动、抬起的动作，从而生成的 DOWN、 MOVE 、UP 的事件序列
2. 硬件层检测涉及到几个概念： 电容屏、 电容器、 ITO、鬼点

手机上的玻璃并不是一块普通的玻璃，在手机的屏幕上包括了触摸检测装置和触摸控制器。触摸检测测装置是用来检测用户的触摸位置，而触摸控制器则是将收集到的触摸信息转换成CPU能够读懂的触点坐标，最后由CPU对触摸的信息进行执行。

当手触摸屏幕表面的时候，手指和触摸面形成一个耦合电容，由于触摸面上接有高频信号，于是就会有一定量的电荷转移到了人体（电不死的放心），为了恢复这些电荷的损失，电荷就从屏幕的四个角补充回来，补充的电荷量和触摸的距离成正比，我们就可以由此推算出触摸点的位置。

电容器 是两金属板之间存在绝缘介质的一种电路元件。其单位为法拉，符号为F。电容器利用二个导体之间的电场来储存能量，二导体所带的电荷大小相等，但符号相反。





#### Touch 事件传递
我们来梳理下 Touch 事件传递涉及的一些基础：
1. 一般情况下，每一个 Touch 事件，总是以 ACTION_DOWN 事件开始，中间穿插着一些 ACTION_MOVE 事件（取决于是否有手势的移动），然后以 ACTION_UP 事件结束，中间还会有 onTouch、onClick、LongClick 等事件。
2. 事件分发过程中，包括对 MotionEvent 事件的三种处理操作：
  * 分发操作：dispatchTouchEvent方法，后面两个方法都是在该方法中被调用的
  * 拦截操作：onInterceptTouchEvent 方法（ViewGroup）
  * 消费操作：onTouchEvent 方法和 OnTouchListener 的 onTouch 方法，其中 onTouch 的优先级高于 onTouchEvent，若 onTouch 返回 true，那么就不会调用 onTouchEvent 方法
3. dispatchTouchEvent 分发 Touch 事件是自顶向下，而 onTouchEvent 消费事件时自底向上，onTouchEvent 和 onIntercepteTouchEvent 都是在 dispatchTouchEvent 中被调用的。

接下来看一个最简单的例子，Activity 中只有一个 LinearLayout，Layout 中有一个 Button， 此时给 Button setOnClickListenr 和 setOnTouchListener, 我们来看执行结果
```java
I/Activity: dispatchTouchEvent action = Down
I/LinearLayout: dispatchTouchEvent action = Down
I/LinearLayout: onInterceptTouchEvent action = Down
I/LinearLayout: onInterceptTouchEvent action = Down, ret = false
I/Button: dispatchTouchEvent action = Down
I/Activity: btn onTouch action = Down
I/Button: onTouchEvent action = Down
I/Button: onTouchEvent action = Down  ret = true
I/Button: dispatchTouchEvent action = Down  ret = true
I/LinearLayout: dispatchTouchEvent action = Down, ret = true
I/Activity: dispatchTouchEvent action = Down ret = true
I/Activity: dispatchTouchEvent action = Up
I/LinearLayout: dispatchTouchEvent action = Up
I/LinearLayout: onInterceptTouchEvent action = Up
I/LinearLayout: onInterceptTouchEvent action = Up, ret = false
I/Button: dispatchTouchEvent action = Up
I/Activity: btn onTouch action = Up
I/Button: onTouchEvent action = Up
I/Button: onTouchEvent action = Up  ret = true
I/Button: dispatchTouchEvent action = Up  ret = true
I/LinearLayout: dispatchTouchEvent action = Up, ret = true
I/Activity: dispatchTouchEvent action = Up ret = true
I/Button: performClick
I/Activity: btn onClick
```
每个 Activity 有个 Window 对象(指的就是 PhoneWindow)，PhoneWindow 中有个 DecorView，这个 DecorView 就是 Activity 的 RootView，所有在 Activity 上触发的 TouchEvent，会先派发给 DecorView 的 dispatchTouchEvent，然后由 DecorView 来决定是否往子 view 派发事件。
```java
@Override
public boolean dispatchTouchEvent(MotionEvent ev) {
    final Window.Callback cb = mWindow.getCallback();
    return cb != null && !mWindow.isDestroyed() && mFeatureId < 0
            ? cb.dispatchTouchEvent(ev) : super.dispatchTouchEvent(ev);
}
```
这里的 cb 就是当前的 Activity，回看 Activity 的源码可以看到它实现了 Window.Callback 接口，同时在 Activity 的 attach 方法中，创建 PhoneWindow 后，调用了 mWindow.setCallback(this) 将 PhoneWindow 中的 callback 设置为当前的 Activity，所有这里的 cb.dispatchTouchEvent 就是 Activity 的 dispatchTouchEvent 方法，如果这三个条件成立则调用 Activity 的 dispatchTouchEvent 方法进行事件的分发，否则直接调用 super.dispatchTouchEvent 方法，也就是 FrameLayout 的 dispatchTouchEvent。这里如果继续跟下去，会发现即使调用 Activity 的 dispatchTouchEvent 最终也会d调用到 super.dispatchTouchEvent。

我们继续往下看 Activity 的 dispatchTouchEvent 方法：
```java
public boolean dispatchTouchEvent(MotionEvent ev) {
    if (ev.getAction() == MotionEvent.ACTION_DOWN) {
        onUserInteraction();
    }
    if (getWindow().superDispatchTouchEvent(ev)) {
        return true;
    }
    return onTouchEvent(ev);
}
```
第一个 if 分支我们暂时先不用看，接下来会调用 PhoneWindow 的 superDispatchTouchEvent，跟进去看看：
```java
@Override
public boolean superDispatchTouchEvent(MotionEvent event) {
    return mDecor.superDispatchTouchEvent(event);
}
```
调用的是 DecorView 的 superDispatchTouchEvent 方法，再跟进去：
```java
public boolean superDispatchTouchEvent(MotionEvent event) {
    return super.dispatchTouchEvent(event);
}
```
这里发现，最终还是调用 DecorView 的 super.dispatchTouchEvent。也就是说，无论怎样 DecorView 的 dispatchTouchEvent 最终都会调用到自己父类 FrameLayout 的 dispatchTouchEvent 方法，而我们在 FrameLayout 中找不到 dispatchTouchEvent 方法，所以会去执行其父类 ViewGroup 的
 dispatchTouchEvent 方法。如果该 dispatchTouchEvent 返回 true，说明后面有 view 消费掉了该事件，那就返回 true，不会再去执行自身的 onTouchEvent 方法，否则，说明没有 view 消费掉该事件，会一路回传到 Activity 中，然后调用自己的 onTouchEvent 方法，该方法的实现比较简单，如下：
 ```java
 public boolean onTouchEvent(MotionEvent event) {
    //当窗口需要关闭时，消费掉当前event
    if (mWindow.shouldCloseOnTouch(this, event)) {
        finish();
        return true;
    }

    return false;
}
 ```
接下来我们重点来分析一下 ViewGroup 的 dispatchTouchEvent
```java
public boolean dispatchTouchEvent(MotionEvent ev) {

    ...

    boolean handled = false;
    //根据隐私策略而来决定是否过滤本次触摸事件
    //当返回 true 表示继续分发事件；当返回 flase,表示该事件应该被过滤掉，不再进行任何分发
    if (onFilterTouchEventForSecurity(ev)) {
        final int action = ev.getAction();
        final int actionMasked = action & MotionEvent.ACTION_MASK;

        // Handle an initial down.
        if (actionMasked == MotionEvent.ACTION_DOWN) {
            // Throw away all previous state when starting a new touch gesture.
            // The framework may have dropped the up or cancel event for the previous gesture
            // due to an app switch, ANR, or some other state change.
            //发生 ACTION_DOWN 事件，则取消并清除之前所有的 TouchTarget
            cancelAndClearTouchTargets(ev);
            //重置触摸状态
            resetTouchState();
        }

        // Check for interception.
        final boolean intercepted;
        //发生 ACTION_DOWN 事件或者已经发生过 ACTION_DOWN，才会进入该区域
        //只要发生过 ACTION_DOWN 则 mFirstTouchTarget != null
        if (actionMasked == MotionEvent.ACTION_DOWN || mFirstTouchTarget != null) {     
            //可通过调用 requestDisallowInterceptTouchEvent，不让父 View 拦截事件       
            final boolean disallowIntercept = (mGroupFlags & FLAG_DISALLOW_INTERCEPT) != 0;
            //判断是否允许调用拦截器
            if (!disallowIntercept) {
                //调用拦截方法
                intercepted = onInterceptTouchEvent(ev);
                ev.setAction(action); // restore action in case it was changed
            } else {
                intercepted = false;
            }
        } else {
            // There are no touch targets and this action is not an initial down
            // so this view group continues to intercept touches.
            // 当没有触摸 targets，且不是 down事件时，开始持续拦截触摸。
            intercepted = true;
        }

        ...

        TouchTarget newTouchTarget = null;
        boolean alreadyDispatchedToNewTouchTarget = false;
         //不取消事件，同时不拦截事件, 并且是Down事件才进入该区域
        if (!canceled && !intercepted) {

            // If the event is targeting accessiiblity focus we give it to the
            // view that has accessibility focus and if it does not handle it
            // we clear the flag and dispatch the event to all children as usual.
            // We are looking up the accessibility focused host to avoid keeping
            // state since these events are very rare.
            View childWithAccessibilityFocus = ev.isTargetAccessibilityFocus()
                    ? findChildWithAccessibilityFocus() : null;

            if (actionMasked == MotionEvent.ACTION_DOWN
                    || (split && actionMasked == MotionEvent.ACTION_POINTER_DOWN)
                    || actionMasked == MotionEvent.ACTION_HOVER_MOVE) {
                final int actionIndex = ev.getActionIndex(); // always 0 for down
                final int idBitsToAssign = split ? 1 << ev.getPointerId(actionIndex)
                        : TouchTarget.ALL_POINTER_IDS;

                // Clean up earlier touch targets for this pointer id in case they
                // have become out of sync.
                removePointersFromTouchTargets(idBitsToAssign);

                final int childrenCount = mChildrenCount;
                if (newTouchTarget == null && childrenCount != 0) {
                    final float x = ev.getX(actionIndex);
                    final float y = ev.getY(actionIndex);
                    // Find a child that can receive the event.
                    // Scan children from front to back.
                    final ArrayList<View> preorderedList = buildTouchDispatchChildList();
                    final boolean customOrder = preorderedList == null
                            && isChildrenDrawingOrderEnabled();
                    final View[] children = mChildren;
                    for (int i = childrenCount - 1; i >= 0; i--) {
                        final int childIndex = getAndVerifyPreorderedIndex(
                                childrenCount, i, customOrder);
                        final View child = getAndVerifyPreorderedView(
                                preorderedList, children, childIndex);

                        // If there is a view that has accessibility focus we want it
                        // to get the event first and if not handled we will perform a
                        // normal dispatch. We may do a double iteration but this is
                        // safer given the timeframe.
                        if (childWithAccessibilityFocus != null) {
                            if (childWithAccessibilityFocus != child) {
                                continue;
                            }
                            childWithAccessibilityFocus = null;
                            i = childrenCount - 1;
                        }

                        if (!canViewReceivePointerEvents(child)
                                || !isTransformedTouchPointInView(x, y, child, null)) {
                            ev.setTargetAccessibilityFocus(false);
                            continue;
                        }

                        newTouchTarget = getTouchTarget(child);
                        if (newTouchTarget != null) {
                            // Child is already receiving touch within its bounds.
                            // Give it the new pointer in addition to the ones it is handling.
                            newTouchTarget.pointerIdBits |= idBitsToAssign;
                            break;
                        }

                        resetCancelNextUpFlag(child);
                        if (dispatchTransformedTouchEvent(ev, false, child, idBitsToAssign)) {
                            // Child wants to receive touch within its bounds.
                            mLastTouchDownTime = ev.getDownTime();
                            if (preorderedList != null) {
                                // childIndex points into presorted list, find original index
                                for (int j = 0; j < childrenCount; j++) {
                                    if (children[childIndex] == mChildren[j]) {
                                        mLastTouchDownIndex = j;
                                        break;
                                    }
                                }
                            } else {
                                mLastTouchDownIndex = childIndex;
                            }
                            mLastTouchDownX = ev.getX();
                            mLastTouchDownY = ev.getY();
                            newTouchTarget = addTouchTarget(child, idBitsToAssign);
                            alreadyDispatchedToNewTouchTarget = true;
                            break;
                        }

                        // The accessibility focus didn't handle the event, so clear
                        // the flag and do a normal dispatch to all children.
                        ev.setTargetAccessibilityFocus(false);
                    }
                    if (preorderedList != null) preorderedList.clear();
                }

                if (newTouchTarget == null && mFirstTouchTarget != null) {
                    // Did not find a child to receive the event.
                    // Assign the pointer to the least recently added target.
                    newTouchTarget = mFirstTouchTarget;
                    while (newTouchTarget.next != null) {
                        newTouchTarget = newTouchTarget.next;
                    }
                    newTouchTarget.pointerIdBits |= idBitsToAssign;
                }
            }
        }

        // Dispatch to touch targets.
        if (mFirstTouchTarget == null) {
            // No touch targets so treat this as an ordinary view.
            handled = dispatchTransformedTouchEvent(ev, canceled, null,
                    TouchTarget.ALL_POINTER_IDS);
        } else {
            // Dispatch to touch targets, excluding the new touch target if we already
            // dispatched to it.  Cancel touch targets if necessary.
            TouchTarget predecessor = null;
            TouchTarget target = mFirstTouchTarget;
            while (target != null) {
                final TouchTarget next = target.next;
                if (alreadyDispatchedToNewTouchTarget && target == newTouchTarget) {
                    handled = true;
                } else {
                    final boolean cancelChild = resetCancelNextUpFlag(target.child)
                            || intercepted;
                    if (dispatchTransformedTouchEvent(ev, cancelChild,
                            target.child, target.pointerIdBits)) {
                        handled = true;
                    }
                    if (cancelChild) {
                        if (predecessor == null) {
                            mFirstTouchTarget = next;
                        } else {
                            predecessor.next = next;
                        }
                        target.recycle();
                        target = next;
                        continue;
                    }
                }
                predecessor = target;
                target = next;
            }
        }

        // Update list of touch targets for pointer up or cancel, if needed.
        if (canceled
                || actionMasked == MotionEvent.ACTION_UP
                || actionMasked == MotionEvent.ACTION_HOVER_MOVE) {
            resetTouchState();
        } else if (split && actionMasked == MotionEvent.ACTION_POINTER_UP) {
            final int actionIndex = ev.getActionIndex();
            final int idBitsToRemove = 1 << ev.getPointerId(actionIndex);
            removePointersFromTouchTargets(idBitsToRemove);
        }
    }

    if (!handled && mInputEventConsistencyVerifier != null) {
        mInputEventConsistencyVerifier.onUnhandledEvent(ev, 1);
    }
    return handled;
}

```
