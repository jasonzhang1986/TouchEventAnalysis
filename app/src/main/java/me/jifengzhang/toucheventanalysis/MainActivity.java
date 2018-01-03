package me.jifengzhang.toucheventanalysis;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean ret = super.dispatchTouchEvent(ev);
        Log.i("MainActivity", Utils.formatString("dispatchTouchEvent action = %s, ret = %s" , Utils.getToutchEventAction(ev), ret));
        return ret;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean ret = super.onTouchEvent(event);
        Log.i("MainActivity", Utils.formatString("onTouchEvent action = %s, ret = %s" , Utils.getToutchEventAction(event), ret));
        return ret;
    }
}
