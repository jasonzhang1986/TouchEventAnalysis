package me.jifengzhang.toucheventanalysis.scrollconfit

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.ListView

/**
 * Created by jifengzhang on 18/1/27.
 */
class CustomListView(context: Context?, attrs: AttributeSet?) : ListView(context, attrs) {
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        val ret:Boolean =  super.onInterceptTouchEvent(ev)
        Log.i("ListView","onInterceptTouchEvent " + ret)
        return ret
    }
}