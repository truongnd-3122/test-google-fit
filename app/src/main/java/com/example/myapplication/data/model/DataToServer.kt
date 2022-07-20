package com.example.myapplication.data.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class DataToServer(
    val date: String = "",
    val temperature: DataTemperature,
    @Json(name = "blood_pressure")
    val bloodPressure: DataBloodPressure
): Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class DataTemperature(
    val temperature: Float = 0f,
    @Json(name = "temperature_unit")
    val temperatureUnit: String,
    @Json(name = "log_date")
    val logDate: String = ""
): Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class DataBloodPressure(
    val min: Int = 0,
    val max: Int = 0,
    @Json(name = "log_date")
    val logDate: String = ""
): Parcelable
