package com.pushoverdemo.user.pushoverdemo

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.pushoverdemo.user.pushoverdemo.database.DBHandler
import com.pushoverdemo.user.pushoverdemo.pushoverapi.CoreApi
import com.pushoverdemo.user.pushoverdemo.pushoverapi.PushoverResponce
import kotlinx.android.synthetic.main.activity_add.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.app.TimePickerDialog
import java.util.*

class AddActivity : AppCompatActivity() {
    private lateinit var localDB : DBHandler
    private var timestamp : Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        setInputAdapters()
        button_perform.setOnClickListener(::performOnClickListener)
        time_cancel.setOnClickListener(::cancelOnClickListener)
        set_time.setOnClickListener(::setTimeOnClickListener)
        localDB = DBHandler(this)
    }

    private fun cancelOnClickListener(view : View){
        post_time.visibility = TextView.GONE
        post_time_label.visibility = TextView.GONE
        time_cancel.visibility= TextView.GONE
        post_time.setText("", TextView.BufferType.EDITABLE)
        timestamp = 0
    }

    private fun setTimeOnClickListener(view : View){
        post_time.visibility = TextView.VISIBLE
        post_time.isEnabled = false
        post_time_label.visibility = TextView.VISIBLE
        time_cancel.visibility = TextView.VISIBLE

        val calendar = Calendar.getInstance()
        val currHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currMinute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this,
            TimePickerDialog.OnTimeSetListener {
                    view, hourOfDay, minute ->
                    val date = Date()
                    val currStamp = Calendar.getInstance()
                    currStamp.time = date
                    currStamp.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    currStamp.set(Calendar.MINUTE, minute)

                    timestamp = currStamp.timeInMillis
                    post_time.setText("$hourOfDay:${if(minute > 9) minute else "0"+ minute}")
                    Toast.makeText(this, "${currStamp.timeInMillis}", Toast.LENGTH_LONG).show()
            },
            currHour,
            currMinute,
            true
        )
        timePickerDialog.show()
    }

    private fun setInputAdapters(){
        val tokenData = mutableListOf<String>("aj4q8y5muqipzuntnz4xs4wtha4orz")
        val tokenAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, tokenData)
        app_token.setAdapter(tokenAdapter)

        val userData = mutableListOf<String>("uvw51e93nov37vw26brw14g1utyjqq", "u3ba37zs37ftt72cab43qwp2xofidn")
        val userAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, userData)
        user_key.setAdapter(userAdapter)

        app_token.setOnClickListener {
            app_token.showDropDown()
        }

        user_key.setOnClickListener {
            user_key.showDropDown()
        }
        post_time.visibility = TextView.GONE
        post_time_label.visibility = TextView.GONE
        time_cancel.visibility= TextView.GONE
    }

    private fun performOnClickListener(button : View) {
        if (!checkInputValues(app_token.text, user_key.text, message_text.text)) return

         val apiService = Retrofit.Builder()
            .baseUrl(CoreApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CoreApi::class.java)

        val appToken = app_token.text.toString()
        val userKey = user_key.text.toString()
        val message = message_text.text.toString()
        apiService.pushMessage(
            appToken,
            userKey,
            message,
            timestamp
        ).enqueue(object : Callback<PushoverResponce> {
            override fun onFailure(call: Call<PushoverResponce>, t: Throwable) {
                Toast.makeText(this@AddActivity, getString(R.string.push_failed), Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<PushoverResponce>, response: Response<PushoverResponce>) {
                if (response.isSuccessful) {
                    val responseStatus = response.body()?.status
                    if(responseStatus == 1){
                        Toast.makeText(this@AddActivity, getString(R.string.push_succ), Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this@AddActivity, getString(R.string.push_failed), Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(this@AddActivity, getString(R.string.push_failed), Toast.LENGTH_SHORT).show()
                }
            }
        })
        val values = ContentValues()
        values.put(DBHandler.userKey, userKey)
        values.put(DBHandler.message, message)
        values.put(DBHandler.timestamp, timestamp)
        localDB.addMessage(values)
        finish()
    }

    private fun checkInputValues(appToken : Editable, userKey : Editable, message : Editable): Boolean {
        if(appToken.isEmpty()){
            Toast.makeText(this@AddActivity, getString(R.string.app_token_error), Toast.LENGTH_LONG).show()
            return false
        }
        if(userKey.isEmpty()){
            Toast.makeText(this@AddActivity, getString(R.string.user_key_error), Toast.LENGTH_LONG).show()
            return false
        }
        if(message.isEmpty()){
            Toast.makeText(this@AddActivity, getString(R.string.message_error), Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

}
