package com.example.myapplication.data.remote.response

import androidx.annotation.Keep
import com.example.myapplication.data.remote.response.BaseResponse
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
@JsonClass(generateAdapter = true)
class GetMovieListResponse() : BaseResponse()
