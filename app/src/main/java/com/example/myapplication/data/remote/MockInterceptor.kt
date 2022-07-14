package com.example.myapplication.data.remote

import android.content.res.AssetManager
import com.example.myapplication.BuildConfig
import com.example.myapplication.utils.getJsonStringFromFile
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.net.HttpURLConnection

class MockInterceptor(
    private val assets: AssetManager
) : Interceptor {

    companion object {
        const val DISCOVER_MOVIE = "discover/movie"
        const val MOCK_DISCOVER_MOVIE = "mock_discover_movie.json"
    }

    @Throws(IllegalAccessError::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (BuildConfig.DEBUG) {
            val uri = chain.request().url.toUri().toString()
            val responseString = when {
                uri.contains(DISCOVER_MOVIE) -> assets.getJsonStringFromFile(MOCK_DISCOVER_MOVIE)
                else -> null
            }

            return if (responseString.isNullOrBlank()) {
                chain.proceed(chain.request())
            } else {
                chain.proceed(chain.request())
                    .newBuilder()
                    .code(HttpURLConnection.HTTP_OK)
                    .protocol(Protocol.HTTP_2)
                    .message(responseString)
                    .body(
                        responseString.toByteArray()
                            .toResponseBody("application/json".toMediaTypeOrNull())
                    )
                    .addHeader("content-type", "application/json")
                    .build()
            }
        } else {
            //just to be on safe side.
            throw IllegalAccessError(
                "MockInterceptor is only meant for Testing Purposes and " +
                        "bound to be used only with DEBUG mode"
            )
        }
    }


}
