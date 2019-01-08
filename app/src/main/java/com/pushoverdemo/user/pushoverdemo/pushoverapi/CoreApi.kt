package com.pushoverdemo.user.pushoverdemo.pushoverapi

import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query

interface CoreApi {

    companion object {
        const val USER_KEY = "uvw51e93nov37vw26brw14g1utyjqq"
        const val APP_TOKEN = "aj4q8y5muqipzuntnz4xs4wtha4orz"
        const val BASE_URL = "https://api.pushover.net/"
    }

    @POST("1/messages.json")
    fun pushMessage(
        @Query("token") token: String,
        @Query("user") user: String,
        @Query("message") message: String,
        @Query("timestamp") timestamp: Long
    ): Call<PushoverResponce>

}