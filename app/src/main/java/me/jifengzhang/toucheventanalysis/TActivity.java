package me.jifengzhang.toucheventanalysis;

import android.app.Activity;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;

/**
 * Created by jifengzhang on 18/1/6.
 */

public class TActivity extends Activity {
    Button btn = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = new ListView(this);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }
}
