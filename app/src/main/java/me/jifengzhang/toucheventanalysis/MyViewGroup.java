package me.jifengzhang.toucheventanalysis;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by jifengzhang on 18/1/1.
 */

public class MyViewGroup extends LinearLayout {
    public MyViewGroup(Context context) {
        super(context);
    }

    public MyViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean ret = super.dispatchTouchEvent(ev);
        Log.i("MyViewGroup" , Utils.formatString("dispatchTouchEvent action = %s, ret = %s" ,Utils.getToutchEventAction(ev), ret));
        return ret;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean ret = super.onInterceptTouchEvent(ev);
        Log.i("MyViewGroup", Utils.formatString("onInterceptTouchEvent action = %s, ret = %s" , Utils.getToutchEventAction(ev), ret));
        return ret;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean ret = super.onTouchEvent(event);
        Log.i("MyViewGroup", Utils.formatString("onTouchEvent action = %s, ret = %s" , Utils.getToutchEventAction(event), ret));
        return ret;
    }
}
