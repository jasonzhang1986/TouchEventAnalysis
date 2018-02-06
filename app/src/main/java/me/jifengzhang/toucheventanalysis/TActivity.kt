package me.jifengzhang.toucheventanalysis

import android.app.Activity
import android.os.Bundle
import android.widget.AbsListView
import android.widget.Button
import android.widget.ListView

/**
 * Created by jifengzhang on 18/1/6.
 */

class TActivity : Activity() {
    internal var btn: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val listView = ListView(this)
        listView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {

            }

            override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {

            }
        })
    }
}
