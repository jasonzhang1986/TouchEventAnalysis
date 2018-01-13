package me.jifengzhang.toucheventanalysis

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.LinearLayout

/**
 * Created by jifengzhang on 18/1/1.
 */

class MyViewGroup : LinearLayout {
    val TAG:String = "LinearLayout"
    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    fun hashcodeStr():String {
        return "["+hashCode()+"]"
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        Log.i(TAG, hashcodeStr() + " dispatchTouchEvent action = " + Utils.getToutchEventAction(ev))
        val ret = super.dispatchTouchEvent(ev)
        Log.i(TAG, Utils.formatString("%s dispatchTouchEvent action = %s, ret = %s", hashcodeStr(), Utils.getToutchEventAction(ev), ret))
        return ret
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        Log.i(TAG, hashcodeStr() + " onInterceptTouchEvent action = " + Utils.getToutchEventAction(ev))
        val ret = true;//super.onInterceptTouchEvent(ev)
        Log.i(TAG, Utils.formatString("%s onInterceptTouchEvent action = %s, ret = %s", hashcodeStr(), Utils.getToutchEventAction(ev), ret))
        return ret
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.i(TAG,hashcodeStr() + " onTouchEvent action = " + Utils.getToutchEventAction(event))
        val ret = super.onTouchEvent(event)
        Log.i(TAG, Utils.formatString("%s onTouchEvent action = %s, ret = %s", hashcodeStr(), Utils.getToutchEventAction(event), ret))
        return ret
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

}
