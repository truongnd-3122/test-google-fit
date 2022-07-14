package com.example.myapplication.data.remote.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Response<T>(
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "data") val data: T? = null,
)
