## TouchEventAnalysis
本次的目的是通过跟踪一组用户的 Touch 事件，整个事件会经历物理产生、硬件层检测、驱动层处理、Framework 层分发， 应用层处理几个部分。
1. 事件的物理产生比较容易理解，就是我们的手指在屏幕上做出按压、拖动、抬起的动作，从而生成的 DOWN、 MOVE 、UP 的事件序列
2. 硬件层检测涉及到几个概念： 电容屏、 电容器、 ITO、鬼点

手机上的玻璃并不是一块普通的玻璃，在手机的屏幕上包括了触摸检测装置和触摸控制器。触摸检测测装置是用来检测用户的触摸位置，而触摸控制器则是将收集到的触摸信息转换成CPU能够读懂的触点坐标，最后由CPU对触摸的信息进行执行。

当手触摸屏幕表面的时候，手指和触摸面形成一个耦合电容，由于触摸面上接有高频信号，于是就会有一定量的电荷转移到了人体（电不死的放心），为了恢复这些电荷的损失，电荷就从屏幕的四个角补充回来，补充的电荷量和触摸的距离成正比，我们就可以由此推算出触摸点的位置。

电容器 是两金属板之间存在绝缘介质的一种电路元件。其单位为法拉，符号为F。电容器利用二个导体之间的电场来储存能量，二导体所带的电荷大小相等，但符号相反。





#### Touch 事件传递
1. MyViewGroup 继承 LinearLayout 默认情况下 onInterceptTouchEvent 返回 false，表示 ViewGroup 不拦截 Touch 事件，交还给 Activity ，从 log 看， Activity 默认也没有处理，直接交给 super 
```java
01-03 22:20:11.759 4637-4637/me.jifengzhang.toucheventanalysis I/MyViewGroup: onInterceptTouchEvent action = Down, ret = false
01-03 22:20:11.759 4637-4637/me.jifengzhang.toucheventanalysis I/MyViewGroup: onTouchEvent action = Down, ret = false
01-03 22:20:11.759 4637-4637/me.jifengzhang.toucheventanalysis I/MyViewGroup: dispatchTouchEvent action = Down, ret = false
01-03 22:20:11.760 4637-4637/me.jifengzhang.toucheventanalysis I/MainActivity: onTouchEvent action = Down, ret = false
01-03 22:20:11.760 4637-4637/me.jifengzhang.toucheventanalysis I/MainActivity: dispatchTouchEvent action = Down, ret = false
01-03 22:20:11.764 4637-4637/me.jifengzhang.toucheventanalysis I/MainActivity: onTouchEvent action = Up, ret = false
01-03 22:20:11.764 4637-4637/me.jifengzhang.toucheventanalysis I/MainActivity: dispatchTouchEvent action = Up, ret = false
```
