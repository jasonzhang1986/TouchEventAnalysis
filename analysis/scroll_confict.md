>了解 Touch 事件的分发和处理流程之后，回到具体应用上，平时在开发中没少遇到处理滑动冲突的地方，比如 ViewPager + ListView， 又比如 ScrollerView + ListView，那具体应该怎么处理，今天就带大家一起撸一遍

今天的例子是 ScrollerView + ListView，ListView 是 ScrollerView 的子 View，通过判断 ScrollerView 什么时候拦截 TouchEvent 来处理滑动冲突

布局如下：
```xml
<?xml version="1.0" encoding="utf-8"?>
<me.jifengzhang.toucheventanalysis.scrollconfit.CustomScrollerView xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical" android:layout_width="match_parent"
    android:layout_height="match_parent">
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:focusable="true"
        android:focusableInTouchMode="true"
        android:orientation="vertical">
        <TextView
            android:id="@+id/head"
            android:layout_width="match_parent"
            android:layout_height="300dp"
            android:text="头部区域"
            android:textSize="30sp"
            android:background="@color/colorPrimaryDark"
            />
        <me.jifengzhang.toucheventanalysis.scrollconfit.CustomListView
            android:id="@+id/list"
            android:layout_width="match_parent"
            android:layout_height="800dp"
            />
    </LinearLayout>

</me.jifengzhang.toucheventanalysis.scrollconfit.CustomScrollerView>
```

```java
override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
    val ret:Boolean =  headerIsShown()
    Log.i("ScrollView","onInterceptTouchEvent " + ret)
    return  ret
}

fun headerIsShown(): Boolean {
    val header: View = findViewById<View>(R.id.head)
    var size = IntArray(2)
    header.getLocationOnScreen(size)
    Log.i("ScrollView", "header ["+size[0]+", " + size[1]+"]")
    return size[1]+header.height > 0
}
```

这里是通过判断顶部的 TextView 是否滑出了可视区域，如果没有滑出可视区域让 ScrollerView 拦截，否则 ScrollView 不拦截也就是交给了 ListView 去处理 TouchEvent
