package me.jifengzhang.toucheventanalysis

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Button
import me.jifengzhang.toucheventanalysis.scrollconfit.TestActivity


/**
 * Created by jifengzhang on 18/1/6.
 */
class MainActivity : Activity() {
    private val TAG:String = "Activity"
    private lateinit var btn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn = findViewById(R.id.btn1)
        initData()
    }

    fun initData() {
        btn.setOnClickListener{ _ ->
            Log.i(TAG, "btn onClick")
            startActivity(Intent(this, TestActivity::class.java))
        }

//        btn!!.setOnTouchListener{ _, ev ->
//            Log.i(TAG, "btn onTouch action = " + Utils.getToutchEventAction(ev))
//            false
//        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        Log.i(TAG,"[ dispatchTouchEvent action = " + Utils.getToutchEventAction(ev))
        val ret: Boolean = super.dispatchTouchEvent(ev)
        Log.i(TAG, "dispatchTouchEvent action = " + Utils.getToutchEventAction(ev) + " ret = " + ret + "]")
        return ret
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.i(TAG, "[ onTouchEvent action = " + Utils.getToutchEventAction(event))
        val ret:Boolean = super.onTouchEvent(event)
        Log.i(TAG, "onTouchEvent action = " + Utils.getToutchEventAction(event) + " ret = " + ret + "]")
        return ret
    }

}