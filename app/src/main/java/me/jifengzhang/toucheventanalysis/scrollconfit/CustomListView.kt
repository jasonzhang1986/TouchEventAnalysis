package me.jifengzhang.toucheventanalysis.scrollconfit

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ListView

/**
 * Created by jifengzhang on 18/1/27.
 */
class CustomListView(context: Context?, attrs: AttributeSet?) : ListView(context, attrs) {
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return super.dispatchTouchEvent(ev)
    }
}