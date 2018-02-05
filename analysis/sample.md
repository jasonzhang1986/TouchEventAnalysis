### 分析一个例子

![view 结构](../image/view-jiegou.png)

UI 结构如上图所示，通过这个例子来分析如下几种情况
1. 默认情况，全部返回super，默认情况是不拦截不消费事件的
2. View 的 onTouchEvent() 消费 down 事件，其他默认
3. View 消费 Down，但 ViewGroup2 拦截 up 事件
4. ViewGroup2 的 onTouchEvent() 消费 down 事件，其他默认

#### 情况1 - 全部采用默认，也就是 return false
```java
I/Activity: [ dispatchTouchEvent action = Down[0]
I/ViewGroup: |--------------------ViewGroup dispatchTouchEvent begin ----------------------------|
I/ViewGroup:  dispatchTouchEvent action = Down[0]
I/ViewGroup:     onInterceptTouchEvent action = Down[0]
I/ViewGroup:     onInterceptTouchEvent action = Down[0], ret = false
I/ViewGroup2: |--------------------ViewGroup2 dispatchTouchEvent begin ----------------------------|
I/ViewGroup2:   dispatchTouchEvent action = Down[0]
I/ViewGroup2:     onInterceptTouchEvent action = Down[0]
I/ViewGroup2:     onInterceptTouchEvent action = Down[0], ret = false
I/MyTextView:        dispatchTouchEvent Down[0]
I/MyTextView:           onTouchEvent action = Down[0]
I/MyTextView:           onTouchEvent action = Down[0]  ret = false
I/MyTextView:        dispatchTouchEvent Down[0], ret = false
I/ViewGroup2:     onTouchEvent action = Down[0]
I/ViewGroup2:     onTouchEvent action = Down[0], ret = false
I/ViewGroup2:   dispatchTouchEvent action = Down[0], ret = false
I/ViewGroup2: |--------------------ViewGroup2 dispatchTouchEvent end----------------------------|
I/ViewGroup:    onTouchEvent action = Down[0]
I/ViewGroup:    onTouchEvent action = Down[0], ret = false
I/ViewGroup: dispatchTouchEvent action = Down[0], ret = false
I/ViewGroup: |--------------------ViewGroup dispatchTouchEvent end----------------------------|
I/Activity: [ onTouchEvent action = Down[0]
I/Activity: onTouchEvent action = Down[0] ret = false]
I/Activity: dispatchTouchEvent action = Down[0] ret = false]
I/Activity: [ dispatchTouchEvent action = Up[1]
I/Activity: [ onTouchEvent action = Up[1]
I/Activity: onTouchEvent action = Up[1] ret = false]
I/Activity: dispatchTouchEvent action = Up[1] ret = false]
```
#### 情况2 - view 消费 down
```java
I/Activity: [ dispatchTouchEvent action = Down[0]
 I/ViewGroup: |--------------------ViewGroup dispatchTouchEvent begin ----------------------------|
 I/ViewGroup:  dispatchTouchEvent action = Down[0]
 I/ViewGroup:     onInterceptTouchEvent action = Down[0]
 I/ViewGroup:     onInterceptTouchEvent action = Down[0], ret = false
 I/ViewGroup2: |--------------------ViewGroup2 dispatchTouchEvent begin ----------------------------|
 I/ViewGroup2:   dispatchTouchEvent action = Down[0]
 I/ViewGroup2:     onInterceptTouchEvent action = Down[0]
 I/ViewGroup2:     onInterceptTouchEvent action = Down[0], ret = false
 I/Button:             dispatchTouchEvent action = Down[0]
 I/Button:                onTouchEvent action = Down[0]
 I/Button:                onTouchEvent action = Down[0]  ret = true
 I/Button: dispatchTouchEvent action = Down[0]  ret = true
 I/ViewGroup2:   dispatchTouchEvent action = Down[0], ret = true
 I/ViewGroup2: |--------------------ViewGroup2 dispatchTouchEvent end----------------------------|
 I/ViewGroup:  dispatchTouchEvent action = Down[0], ret = true
 I/ViewGroup: |--------------------ViewGroup dispatchTouchEvent end----------------------------|
 I/Activity: dispatchTouchEvent action = Down[0] ret = true]
 I/Activity: [ dispatchTouchEvent action = Up[1]
 I/ViewGroup: |--------------------ViewGroup dispatchTouchEvent begin ----------------------------|
 I/ViewGroup:  dispatchTouchEvent action = Up[1]
 I/ViewGroup:     onInterceptTouchEvent action = Up[1]
 I/ViewGroup:     onInterceptTouchEvent action = Up[1], ret = false
 I/ViewGroup2: |--------------------ViewGroup2 dispatchTouchEvent begin ----------------------------|
 I/ViewGroup2:   dispatchTouchEvent action = Up[1]
 I/ViewGroup2:     onInterceptTouchEvent action = Up[1]
 I/ViewGroup2:    onInterceptTouchEvent action = Up[1], ret = false
 I/Button: dispatchTouchEvent action = Up[1]
 I/Button: onTouchEvent action = Up[1]
 I/Button: onTouchEvent action = Up[1]  ret = true
 I/Button: dispatchTouchEvent action = Up[1]  ret = true
 I/ViewGroup2:   dispatchTouchEvent action = Up[1], ret = true
 I/ViewGroup2: |--------------------ViewGroup2 dispatchTouchEvent end----------------------------|
 I/ViewGroup:  dispatchTouchEvent action = Up[1], ret = true
 I/ViewGroup: |--------------------ViewGroup dispatchTouchEvent end----------------------------|
 I/Activity: dispatchTouchEvent action = Up[1] ret = true]
 I/Button: performClick
```
#### 情况3 - View 消费 Down，但 ViewGroup2 拦截 up 事件
```java
I/Activity: [ dispatchTouchEvent action = Down[0]
I/ViewGroup: |--------------------ViewGroup dispatchTouchEvent begin ----------------------------|
I/ViewGroup:  dispatchTouchEvent action = Down[0]
I/ViewGroup:     onInterceptTouchEvent action = Down[0]
I/ViewGroup:     onInterceptTouchEvent action = Down[0], ret = false
I/ViewGroup2: |--------------------ViewGroup2 dispatchTouchEvent begin ----------------------------|
I/ViewGroup2:   dispatchTouchEvent action = Down[0]
I/ViewGroup2:     onInterceptTouchEvent action = Down[0]
I/ViewGroup2:    onInterceptTouchEvent action = Down[0], ret = false
I/Button: dispatchTouchEvent action = Down[0]
I/Button: onTouchEvent action = Down[0]
I/Button: onTouchEvent action = Down[0]  ret = true
I/Button: dispatchTouchEvent action = Down[0]  ret = true
I/ViewGroup2:   dispatchTouchEvent action = Down[0], ret = true
I/ViewGroup2: |--------------------ViewGroup2 dispatchTouchEvent end----------------------------|
I/ViewGroup:  dispatchTouchEvent action = Down[0], ret = true
I/ViewGroup: |--------------------ViewGroup dispatchTouchEvent end----------------------------|
I/Activity: dispatchTouchEvent action = Down[0] ret = true]
I/Activity: [ dispatchTouchEvent action = Up[1]
I/ViewGroup: |--------------------ViewGroup dispatchTouchEvent begin ----------------------------|
I/ViewGroup:  dispatchTouchEvent action = Up[1]
I/ViewGroup:     onInterceptTouchEvent action = Up[1]
I/ViewGroup:     onInterceptTouchEvent action = Up[1], ret = false
I/ViewGroup2: |--------------------ViewGroup2 dispatchTouchEvent begin ----------------------------|
I/ViewGroup2:   dispatchTouchEvent action = Up[1]
I/ViewGroup2:     onInterceptTouchEvent action = Up[1]
I/ViewGroup2:    onInterceptTouchEvent action = Up[1], ret = true
I/Button: dispatchTouchEvent action = Cancel[3]
I/Button: onTouchEvent action = Cancel[3]
I/Button: onTouchEvent action = Cancel[3]  ret = true
I/Button: dispatchTouchEvent action = Cancel[3]  ret = true
I/ViewGroup2:   dispatchTouchEvent action = Up[1], ret = true
I/ViewGroup2: |--------------------ViewGroup2 dispatchTouchEvent end----------------------------|
I/ViewGroup:  dispatchTouchEvent action = Up[1], ret = true
I/ViewGroup: |--------------------ViewGroup dispatchTouchEvent end----------------------------|
I/Activity: dispatchTouchEvent action = Up[1] ret = true]
```
#### ViewGroup2 的 onTouchEvent() 消费 down 事件，其他默认
```java
I/Activity: [ dispatchTouchEvent action = Down[0]
 I/ViewGroup: |--------------------ViewGroup dispatchTouchEvent begin ----------------------------|
 I/ViewGroup:  dispatchTouchEvent action = Down[0]
 I/ViewGroup:     onInterceptTouchEvent action = Down[0]
 I/ViewGroup:     onInterceptTouchEvent action = Down[0], ret = false
 I/ViewGroup2: |--------------------ViewGroup2 dispatchTouchEvent begin ----------------------------|
 I/ViewGroup2:   dispatchTouchEvent action = Down[0]
 I/ViewGroup2:     onInterceptTouchEvent action = Down[0]
 I/ViewGroup2:    onInterceptTouchEvent action = Down[0], ret = false
 I/MyTextView: [dispatchTouchEvent Down[0]
 I/MyTextView: [onTouchEvent action = Down[0]
 I/MyTextView: onTouchEvent action = Down[0]  ret = false]
 I/MyTextView: dispatchTouchEvent Down[0], ret = false]
 I/ViewGroup2:       onTouchEvent action = Down[0]
 I/ViewGroup2:     onTouchEvent action = Down[0], ret = true
 I/ViewGroup2:   dispatchTouchEvent action = Down[0], ret = true
 I/ViewGroup2: |--------------------ViewGroup2 dispatchTouchEvent end----------------------------|
 I/ViewGroup:  dispatchTouchEvent action = Down[0], ret = true
 I/ViewGroup: |--------------------ViewGroup dispatchTouchEvent end----------------------------|
 I/Activity: dispatchTouchEvent action = Down[0] ret = true]
 I/Activity: [ dispatchTouchEvent action = Up[1]
 I/ViewGroup: |--------------------ViewGroup dispatchTouchEvent begin ----------------------------|
 I/ViewGroup:  dispatchTouchEvent action = Up[1]
 I/ViewGroup:     onInterceptTouchEvent action = Up[1]
 I/ViewGroup:     onInterceptTouchEvent action = Up[1], ret = false
 I/ViewGroup2: |--------------------ViewGroup2 dispatchTouchEvent begin ----------------------------|
 I/ViewGroup2:   dispatchTouchEvent action = Up[1]
 I/ViewGroup2:       onTouchEvent action = Up[1]
 I/ViewGroup2:     onTouchEvent action = Up[1], ret = true
 I/ViewGroup2:   dispatchTouchEvent action = Up[1], ret = true
 I/ViewGroup2: |--------------------ViewGroup2 dispatchTouchEvent end----------------------------|
 I/ViewGroup:  dispatchTouchEvent action = Up[1], ret = true
 I/ViewGroup: |--------------------ViewGroup dispatchTouchEvent end----------------------------|
 I/Activity: dispatchTouchEvent action = Up[1] ret = true]
```
可以看到 TextView 没有消费 Down 事件，返回到 ViewGroup2 的 onTouchEvent 中，此时 ViewGroup2 的 onTouchEvent 返回 True，认为 ViewGroup2 消费了该事件，之后的 up 事件到 ViewGroup2 就截止


#### getEvent & sendEvent
点击模拟器launcher的电话图标
```
/dev/input/event1: 0003 0039 00000000
/dev/input/event1: 0003 0030 00000006
/dev/input/event1: 0003 003a 00000081
/dev/input/event1: 0003 0035 00000ce5
/dev/input/event1: 0003 0036 0000693c
/dev/input/event1: 0000 0000 00000000
/dev/input/event1: 0003 003a 00000000
/dev/input/event1: 0003 0039 ffffffff
/dev/input/event1: 0000 0000 00000000
```
对应的有意义的内容是
```
/dev/input/event1: EV_ABS       ABS_MT_TRACKING_ID   00000000
/dev/input/event1: EV_ABS       ABS_MT_TOUCH_MAJOR   00000009
/dev/input/event1: EV_ABS       ABS_MT_PRESSURE      00000081
/dev/input/event1: EV_ABS       ABS_MT_POSITION_X    00000ce5
/dev/input/event1: EV_ABS       ABS_MT_POSITION_Y    000068a3
/dev/input/event1: EV_SYN       SYN_REPORT           00000000
/dev/input/event1: EV_ABS       ABS_MT_PRESSURE      00000000
/dev/input/event1: EV_ABS       ABS_MT_TRACKING_ID   ffffffff
/dev/input/event1: EV_SYN       SYN_REPORT           00000000
```
那么可以模拟输入
```
sendevent /dev/input/event1 3 57 0
sendevent /dev/input/event1 3 48 6
sendevent /dev/input/event1 3 58 129
sendevent /dev/input/event1 3 53 3301
sendevent /dev/input/event1 3 54 26940
sendevent /dev/input/event1 0 0 0
sendevent /dev/input/event1 3 58 0
sendevent /dev/input/event1 3 57 -1
sendevent /dev/input/event1 0 0 0
```
需要注意的是，getevent 读取的是 16 进制的，sendevent 需要输入对应的 10 进制
