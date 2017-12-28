## TouchEventAnalysis
本次的目的是通过跟踪一组用户的 Touch 事件，整个事件会经历物理产生、硬件层检测、驱动层处理、Framework 层分发， 应用层处理几个部分。
1. 事件的物理产生比较容易理解，就是我们的手指在屏幕上做出按压、拖动、抬起的动作，从而生成的 DOWN、 MOVE 、UP 的事件序列
2. 硬件层检测涉及到几个概念： 电容屏、 电容器、 ITO、鬼点

手机上的玻璃并不是一块普通的玻璃，在手机的屏幕上包括了触摸检测装置和触摸控制器。触摸检测测装置是用来检测用户的触摸位置，而触摸控制器则是将收集到的触摸信息转换成CPU能够读懂的触点坐标，最后由CPU对触摸的信息进行执行。

当手触摸屏幕表面的时候，手指和触摸面形成一个耦合电容，由于触摸面上接有高频信号，于是就会有一定量的电荷转移到了人体（电不死的放心），为了恢复这些电荷的损失，电荷就从屏幕的四个角补充回来，补充的电荷量和触摸的距离成正比，我们就可以由此推算出触摸点的位置。
