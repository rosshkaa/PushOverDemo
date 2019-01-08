package com.pushoverdemo.user.pushoverdemo.pushoverapi

import com.google.gson.annotations.SerializedName

data class PushoverResponce (
    @SerializedName("status") val status: Int,
    @SerializedName("request") val request: String
)