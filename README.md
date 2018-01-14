### 本篇主要是分析 Android 中 Touch 事件从产生到被消费掉的流程
* UI 的展示是基于 Activity 的，那 Activity 是如何把 Touch 事件分发给展示出来的 View，View 又是如何消费了此次 Touch 事件的？[Link](touchAnalysis_framework.md)
* 我们知道事件的产生是手指按压了屏幕，那 Android 系统是如何得知屏幕被 touch 了，又是如何将事件传给 Activity 的？[Link](touchAnalysis_native.md)
* 了解了 Android 处理 Touch 事件的原理和流程，我们来分析下滑动冲突应该如何处理？[Link](scroll_confict.md)
