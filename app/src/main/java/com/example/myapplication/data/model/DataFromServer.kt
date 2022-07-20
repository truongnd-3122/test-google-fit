package com.example.myapplication.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
@JsonClass(generateAdapter = true)
data class DataFromServer(
    val temperature: DataTemperature,
    @Json(name = "blood_pressure")
    val bloodPressure: DataBloodPressure
): Parcelable

