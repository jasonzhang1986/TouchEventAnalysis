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
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return super.dispatchTouchEvent(ev)
    }
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        var ret:Boolean = false
        Log.i("ListView", "firstVisiblePos = $firstVisiblePosition")
        Log.i("ListView","bottom = " + bottom)
        if(firstVisiblePosition>0) {
            ret = true
        } else if(firstVisiblePosition==0) {
//            var size = IntArray(2)
//            getChildAt(0).getLocationOnScreen(size)
//            Log.i("ListView","y = " + size[1])
//            if (size[1]<0) {
//                ret = true
//            }
        }
        requestDisallowInterceptTouchEvent(ret)
        Log.i("ListView","onInterceptTouchEvent " + ret)
//        requestDisallowInterceptTouchEvent(true)
        return ret
    }
}