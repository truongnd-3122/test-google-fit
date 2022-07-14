package com.example.myapplication.data.model

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class UserInfoSignUp(
    val email: String = "",
    val password: String = "",
    val password_confirmation: String = ""
): Parcelable