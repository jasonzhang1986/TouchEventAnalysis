package me.jifengzhang.toucheventanalysis.scrollconfit

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView

/**
 * Created by jifengzhang on 18/1/27.
 */
class CustomScrollerView(context: Context?, attrs: AttributeSet?) : ScrollView(context, attrs) {
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return super.dispatchTouchEvent(ev)
    }
}