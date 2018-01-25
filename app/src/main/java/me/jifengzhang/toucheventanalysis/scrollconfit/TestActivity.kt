package me.jifengzhang.toucheventanalysis.scrollconfit

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import me.jifengzhang.toucheventanalysis.R

/**
 * Created by zhangjifeng on 2018/1/25.
 */
class TestActivity : Activity() {
    private lateinit var _head: TextView
    private lateinit var _listView: ListView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        _head = findViewById(R.id.head)
        _listView = findViewById(R.id.list)
    }

    private class MyAdapter : BaseAdapter() {
        private var data: ArrayList<String> = ArrayList()
        fun setData(data: ArrayList<String>) {
            this.data.addAll(data)
        }

        override fun getItem(position: Int): Any {
            return position
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return data.size
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var holder: ViewHolder
            var retView: View
            if (convertView == null){
                retView = TextView(parent?.context)
                holder = ViewHolder()
                holder.content = retView
                retView.setTag(holder)
            } else {
                holder = convertView.tag as ViewHolder
                retView = convertView
            }
            holder.content.text = data[position]
            return retView
        }

        internal class ViewHolder {
            lateinit var content: TextView
        }

    }
}