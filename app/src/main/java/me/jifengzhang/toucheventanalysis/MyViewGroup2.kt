package me.jifengzhang.toucheventanalysis

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.LinearLayout

/**
 * Created by jifengzhang on 18/1/1.
 */

class MyViewGroup2 : LinearLayout {
    val TAG:String = "ViewGroup2"
    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    fun hashcodeStr():String {
        return "["+hashCode()+"]"
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        Log.i(TAG, "|--------------------ViewGroup2 dispatchTouchEvent begin ----------------------------|")
        Log.i(TAG,  "  dispatchTouchEvent action = " + Utils.getToutchEventAction(ev))
        val ret = super.dispatchTouchEvent(ev)
        Log.i(TAG, Utils.formatString("  dispatchTouchEvent action = %s, ret = %s",  Utils.getToutchEventAction(ev), ret))
        Log.i(TAG, "|--------------------ViewGroup2 dispatchTouchEvent end----------------------------|")
        return ret
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        Log.i(TAG, "    onInterceptTouchEvent action = " + Utils.getToutchEventAction(ev))
//        val ret = super.onInterceptTouchEvent(ev)
        var ret = false
        if (ev.action == MotionEvent.ACTION_UP) {
            ret = true
        }
        Log.i(TAG, Utils.formatString("   onInterceptTouchEvent action = %s, ret = %s", Utils.getToutchEventAction(ev), ret))
        return ret
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.i(TAG,"      onTouchEvent action = " + Utils.getToutchEventAction(event))
        val ret = super.onTouchEvent(event)
        Log.i(TAG, Utils.formatString("    onTouchEvent action = %s, ret = %s", Utils.getToutchEventAction(event), ret))
        return ret
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

}
