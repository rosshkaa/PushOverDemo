package com.pushoverdemo.user.pushoverdemo.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder

class DBHandler(context: Context) : SQLiteOpenHelper(context, DBName, null, DBVersion) {

    companion object {
        val DBName = "message_history"
        val DBVersion = 1
        val tableName = "message_history"
        val _id = "id"
        val userKey = "userkey"
        val message = "message"
        val timestamp = "timestamp"
    }
    var sqlObj: SQLiteDatabase = this.writableDatabase

    override fun onCreate(p0: SQLiteDatabase?) {
        val sql1: String = "CREATE TABLE IF NOT EXISTS $tableName ( $_id  INTEGER PRIMARY KEY, $userKey TEXT, $message TEXT, $timestamp TEXT);"
        p0!!.execSQL(sql1);
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0!!.execSQL("Drop table IF EXISTS $tableName")
        onCreate(p0)
    }

    fun addMessage(values: ContentValues) = sqlObj.insert(tableName, "", values)

    fun removeMessage (id: Int) = sqlObj.delete(tableName, "id=?", arrayOf(id.toString()))

    fun updateMessage(values: ContentValues, id: Int) = sqlObj.update(tableName, values, "id=?", arrayOf(id.toString()))

    fun listMessages(key : String) : ArrayList<PushoverMessage> {
        val arraylist = ArrayList<PushoverMessage>()
        val sqlQB = SQLiteQueryBuilder()
        sqlQB.tables = tableName
        val cols = arrayOf(_id, userKey, message, timestamp)
        val selectArgs = arrayOf(key)
        val cursor = sqlQB.query(sqlObj, cols,"$userKey like ?", selectArgs,null,null, userKey)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(_id))
                val userKey = cursor.getString(cursor.getColumnIndex(userKey))
                val message = cursor.getString(cursor.getColumnIndex(message))
                val timestamp = cursor.getString(cursor.getColumnIndex(timestamp))

                arraylist.add(PushoverMessage(id, userKey, message, timestamp))

            } while (cursor.moveToNext())
        }
        return arraylist
    }


}