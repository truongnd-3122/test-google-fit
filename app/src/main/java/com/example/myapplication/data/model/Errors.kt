package com.example.myapplication.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.example.myapplication.data.remote.response.BaseResponse
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Errors(
    val resource: String? = null,
    val id: String? = null,
    val attribute: String? = null,
//    @SerializedName("full_message") val fullMessage: String? = null
    @Json(name ="full_message") val fullMessage: String? = null
) : BaseResponse()
