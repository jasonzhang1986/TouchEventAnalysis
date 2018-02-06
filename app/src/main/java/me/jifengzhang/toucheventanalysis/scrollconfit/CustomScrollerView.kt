package me.jifengzhang.toucheventanalysis.scrollconfit

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AbsListView
import android.widget.ListView
import android.widget.ScrollView
import me.jifengzhang.toucheventanalysis.R

/**
 * Created by jifengzhang on 18/1/27.
 */
class CustomScrollerView(context: Context?, attrs: AttributeSet?) : ScrollView(context, attrs) {
    var lastX:Float = 0f
    var lastY:Float = 0f
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        super.onInterceptTouchEvent(ev)
        Log.i("ScrollerView","scrollY = $scrollY, height = $height ," + getChildAt(0).height)
        var intercepted =  false
        when(ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = ev.x
                lastY = ev.y
            }
            MotionEvent.ACTION_MOVE -> {
                if (lastY - ev.y > 0){
                    intercepted = headerIsShown()
                } else {
                    val list: ListView = findViewById(R.id.list)
                    intercepted = list.firstVisiblePosition==0
                }
                lastX = ev.x
                lastY = ev.y
            }
            MotionEvent.ACTION_UP -> intercepted = false
        }
        Log.i("ScrollerView","intercepted = $intercepted")
        return intercepted
    }


    private fun headerIsShown(): Boolean {
        val header: View = findViewById<View>(R.id.head)
        var size = IntArray(2)
        header.getLocationOnScreen(size)
        Log.i("ScrollView", "header ["+size[0]+", " + size[1]+"]")
        Log.i("ScrollView","bottom = " + (size[1]+header.height))
        var isShown = (size[1]+header.height)>=0
        Log.i("ScrollView", "isShown = $isShown")
        return isShown
    }

}