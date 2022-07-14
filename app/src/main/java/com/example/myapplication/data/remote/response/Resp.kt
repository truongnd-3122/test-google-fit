package com.example.myapplication.data.remote.response

import com.example.myapplication.data.remote.response.DataResponse
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Resp(
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "data") val data: DataResponse? = null,
)