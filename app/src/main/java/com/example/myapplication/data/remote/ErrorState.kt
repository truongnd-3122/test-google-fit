package com.example.myapplication.data.remote

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class ErrorState(
    var success: Boolean? = null,
    @Json(name = "error_code") var error_code: String?= null,
    @Json(name = "error_message") var message: String? = null,
    @Json(name = "errors") var errors: Message? = null,
//    var errorType: Int? = ErrorType.UNEXPECTED.mode,
    var code: Int? = null

): Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
data class ErrorList(
    var success: Boolean? = null,
    @Json(name = "error_code") var error_code: String? = null,
    @Json(name = "error_message") var message: String? = null,
    @Json(name = "errors") var errors: List<Message>? = null,
//    var errorType: Int? = ErrorType.SERVER.mode,
    var code: Int? = null
):Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
data class Message(
    var resource: String? = null,
    var id: String? = null,
    var attribute: String? = null,
    @Json(name ="full_message") var fullMessage: String? = null
):Parcelable