package com.example.myapplication.data.model

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class UserInfoSignIn(
    val email: String = "",
    val password: String = ""
) : Parcelable
