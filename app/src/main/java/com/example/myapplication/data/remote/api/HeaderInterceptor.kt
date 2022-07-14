package com.example.myapplication.data.remote.api


import com.example.myapplication.BuildConfig
import com.squareup.moshi.Moshi

import com.example.myapplication.data.constants.Constants
import com.example.myapplication.data.constants.Constants.TOKEN_EXPIRED
import com.example.myapplication.data.local.pref.AppPrefs
import com.example.myapplication.data.remote.ErrorState

import com.example.myapplication.data.remote.response.Resp
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull

import java.net.HttpURLConnection
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import javax.inject.Inject

class HeaderInterceptor @Inject constructor(val appPrefs: AppPrefs) : Interceptor {

    companion object {
        private const val API_REFRESH_TOKEN = "${BuildConfig.BASE_URL}${ApiPath.PATH_REFRESH_TOKEN}"
        private const val AUTH = "Authorization"
        private const val VALUE_AUTH = "Basic ZGV2LXMtZ2FhczpBYUAxMjM0NTY="
        private const val CONTENT_TYPE = "Content-Type"
        private const val VALUE_CONTENT_TYPE = "application/x-www-form-urlencoded"
        private const val X_REFRESH_TOKEN = "X-Refresh-Token"
        private const val X_ACCESS_TOKEN = "X-Access-Token"

    }

    private fun getAccessToken(): String = appPrefs.getAccessToken().toString()
    private fun getRefreshToken(): String = appPrefs.getRefreshToken().toString()
    private fun renewToken(accessTokenNew: String, refreshTokenNew: String){
        appPrefs.storeAccessToken(accessTokenNew)
        appPrefs.storeRefreshToken(refreshTokenNew)
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = recreateRequestWithNewAccessToken(chain)
        val response = chain.proceed(request)

        if (response.code == HttpURLConnection.HTTP_UNAUTHORIZED) {
            if (lock.tryLock()) {
                try {
                    val refreshToken = getRefreshToken()
                    val client = OkHttpClient()
                    val refreshRequest = Request.Builder()
                        .url(API_REFRESH_TOKEN)
                        .method("POST", RequestBody.create("text/plain".toMediaTypeOrNull(), ""))
                        .addHeader(AUTH, VALUE_AUTH)
                        .addHeader(CONTENT_TYPE, VALUE_CONTENT_TYPE)
                        .addHeader(X_REFRESH_TOKEN, refreshToken)
                        .build()

                    val refreshResponse = client.newCall(refreshRequest).execute()

                    if (refreshResponse.code == HttpURLConnection.HTTP_OK) {
                        val data = refreshResponse.body?.string()
                        val newAuth = Moshi.Builder().build().adapter(Resp::class.java).fromJson(data)

                        //store sprefs
                        renewToken(newAuth?.data?.accessToken.toString(), newAuth?.data?.refreshToken.toString())

                    }else if (refreshResponse.code == HttpURLConnection.HTTP_FORBIDDEN){
                        throw BaseException.toServerError(
                            serverErrorResponse = ErrorState(message = TOKEN_EXPIRED),
                            httpCode = Constants.REFRESH_TOKEN_EXPIRED,
                            response = null
                        )
                    }

                    val newRequest = recreateRequestWithNewAccessToken(chain)
                    return chain.proceed(newRequest)

                }catch (e: Exception){
                    return response
                }finally {
                    lock.unlock()
                }
            }
            else{
                lock.lock()
                val newRequest = recreateRequestWithNewAccessToken(chain)
                return chain.proceed(newRequest)
            }
        }
        else{
            return response
        }
    }

    private fun recreateRequestWithNewAccessToken(chain: Interceptor.Chain): Request {
        val token = getAccessToken()

        val request = chain.request()
        return request.newBuilder()
            .addHeader(AUTH, VALUE_AUTH)
            .addHeader(CONTENT_TYPE, VALUE_CONTENT_TYPE)
            .addHeader(X_ACCESS_TOKEN, token)
            .method(request.method, request.body)
            .build()
    }

    private val lock: Lock by lazy { ReentrantLock() }


//    override fun intercept(chain: Interceptor.Chain): Response {
//        val original = chain.request()
//        Log.d("Api call", original.url.toString())
//        val uri = original.url.toUri().toString()
//        val response = original.newBuilder()
//            .addHeader("Authorization", "Basic ZGV2LXMtZ2FhczpBYUAxMjM0NTY=")
//            .addHeader("Content-Type", "application/x-www-form-urlencoded")
////            .header("X-Access-Token", "")
////            .apply {
//////                if (uri.contains("refresh-token")){
//////                    addHeader("X-Refresh-Token",)
//////                }
////                if(getAccessToken()!!.isNotEmpty()){
////                    Log.d("zzz000", "token")
////                }
////            }
//            .method(original.method, original.body)
//            .build()
//        return chain.proceed(response)
//    }
}