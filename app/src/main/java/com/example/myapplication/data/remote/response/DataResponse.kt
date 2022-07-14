package com.example.myapplication.data.remote.response

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
@JsonClass(generateAdapter = true)
data class DataResponse(
    val id: Long? = 0,
    @Json(name ="access_token") val accessToken: String? = "",
    @Json(name ="refresh_token") val refreshToken: String? = "",
    @Json(name ="expires_at") val expiresAt: String? = ""
):Parcelable