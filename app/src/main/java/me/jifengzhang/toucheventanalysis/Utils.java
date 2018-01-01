package me.jifengzhang.toucheventanalysis;

import android.view.MotionEvent;

/**
 * Created by jifengzhang on 18/1/1.
 */

public class Utils {

    public static String getToutchEventAction(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                return "Down";
            case MotionEvent.ACTION_MOVE:
                return "Move";
            case MotionEvent.ACTION_UP:
                return "Up";
            default:
                return "None";
        }
    }
}
