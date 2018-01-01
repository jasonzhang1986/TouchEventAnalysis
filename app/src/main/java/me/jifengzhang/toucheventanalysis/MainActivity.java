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
        Log.i("MainActivity", "dispatchTouchEvent action = " + Utils.getToutchEventAction(ev));
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i("MainActivity", "onTouchEvent action = " + Utils.getToutchEventAction(event));
        return super.onTouchEvent(event);
    }
}
