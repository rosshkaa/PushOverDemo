package com.pushoverdemo.user.pushoverdemo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter
import android.widget.TextView
import com.pushoverdemo.user.pushoverdemo.database.DBHandler

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button_add.setOnClickListener {
            startActivity(Intent(this, AddActivity::class.java))
        }

    }

    override fun onResume() {
        super.onResume()
        val localDB = DBHandler(this)
        val messageList = localDB.listMessages("%")
        if(messageList.size > 0) {
            history_tag.visibility = TextView.GONE
            val data = ArrayList<String>()
            for (temp in messageList){
                data.add("User key: ${temp.userKey}\nText: ${temp.message}")
            }
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, data)
            history_view.adapter = adapter
        }
    }
}
