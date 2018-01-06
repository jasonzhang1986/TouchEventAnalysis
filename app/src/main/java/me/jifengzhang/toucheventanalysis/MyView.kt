package me.jifengzhang.toucheventanalysis

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.Button

/**
 * Created by jifengzhang on 18/1/6.
 */

class MyView : Button {
    val TAG:String = "Button"
    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        Log.i(TAG,"dispatchTouchEvent action = " + Utils.getToutchEventAction(event))
        val ret = super.dispatchTouchEvent(event)
        Log.i(TAG, String.format("dispatchTouchEvent action = %s  ret = %s", Utils.getToutchEventAction(event), ret))
        return ret
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.i(TAG, "onTouchEvent action = " + Utils.getToutchEventAction(event))
        val ret = super.onTouchEvent(event)
        Log.i(TAG, String.format("onTouchEvent action = %s  ret = %s", Utils.getToutchEventAction(event), ret))
        return ret
    }

    override fun performClick(): Boolean {
        Log.i(TAG, "performClick")
        return super.performClick()
    }
}
