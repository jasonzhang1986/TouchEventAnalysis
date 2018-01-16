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
                return "Down["+action+"]";
            case MotionEvent.ACTION_MOVE:
                return "Move["+action+"]";
            case MotionEvent.ACTION_UP:
                return "Up["+action+"]";
            case MotionEvent.ACTION_CANCEL:
                return "Cancel["+action+"]";
            default:
                return "None["+action+"]";
        }
    }

    public static String formatString(String text, Object... args) {
        return String.format(text, args);
    }
}
