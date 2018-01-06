package me.jifengzhang.toucheventanalysis

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Button

/**
 * Created by jifengzhang on 18/1/6.
 */
class Test1Activity : Activity() {
    val TAG:String = "Activity"
    var btn: Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test1)
        btn = findViewById(R.id.btn1)
        initData()
    }

    fun initData() {
        btn!!.setOnClickListener{ _ -> Log.i(TAG, "btn onClick") }
        btn!!.setOnTouchListener{ _, ev ->
            Log.i(TAG, "btn onTouch action = " + Utils.getToutchEventAction(ev))
            false
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        Log.i(TAG,"dispatchTouchEvent action = " + Utils.getToutchEventAction(ev))
        val ret: Boolean = super.dispatchTouchEvent(ev)
        Log.i(TAG, "dispatchTouchEvent action = " + Utils.getToutchEventAction(ev) + " ret = " + ret)
        return ret
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.i(TAG, "onTouchEvent action = " + Utils.getToutchEventAction(event))
        val ret:Boolean = super.onTouchEvent(event)
        Log.i(TAG, "onTouchEvent action = " + Utils.getToutchEventAction(event) + " ret = " + ret)
        return ret
    }

}