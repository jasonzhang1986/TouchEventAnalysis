package me.jifengzhang.toucheventanalysis;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

/**
 * Created by jifengzhang on 18/1/6.
 */

public class TActivity extends Activity {
    Button btn = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
