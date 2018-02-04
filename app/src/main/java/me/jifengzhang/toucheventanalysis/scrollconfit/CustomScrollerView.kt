package me.jifengzhang.toucheventanalysis.scrollconfit

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ScrollView
import me.jifengzhang.toucheventanalysis.R

/**
 * Created by jifengzhang on 18/1/27.
 */
class CustomScrollerView(context: Context?, attrs: AttributeSet?) : ScrollView(context, attrs) {
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        Log.i("ScrollerView","scrollY = $scrollY, height = $height ," + getChildAt(0).height)
        if (scrollY + height - paddingTop-paddingBottom == getChildAt(0).height) {
            Log.i("ScrollerView","onInterceptTouchEvent false" )
            return false
        }
        else {
            return true
        }
//        val ret:Boolean =  super.onInterceptTouchEvent(ev)
//        Log.i("ScrollView","onInterceptTouchEvent " + ret)
//        return  ret
    }

    fun headerIsShown(): Boolean {
        val header: View = findViewById<View>(R.id.head)
        var size = IntArray(2)
        header.getLocationOnScreen(size)
        Log.i("ScrollView", "header ["+size[0]+", " + size[1]+"]")
        return size[1]+header.height > 0
    }
}