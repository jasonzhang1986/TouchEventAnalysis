package me.jifengzhang.toucheventanalysis

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent

class MainActivity : Activity() {

    internal var myViewGroup: MyViewGroup? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        myViewGroup = findViewById(R.id.myviewgroup)
//        myViewGroup!!.setOnTouchListener { _, ev ->
//            Log.i("MainActivity", String.format("MyViewGroup onTouchListener action = %s, ret = true", Utils.getToutchEventAction(ev)))
//            true
//        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val ret = super.dispatchTouchEvent(ev)
        Log.i("MainActivity", String.format("dispatchTouchEvent [%d] action = %s, ret = %s", ev.hashCode(), Utils.getToutchEventAction(ev), ret))
        return ret
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val ret = super.onTouchEvent(event)
        Log.i("MainActivity", String.format("onTouchEvent [%d] action = %s, ret = %s", event.hashCode(), Utils.getToutchEventAction(event), ret))
        return ret
    }
}
