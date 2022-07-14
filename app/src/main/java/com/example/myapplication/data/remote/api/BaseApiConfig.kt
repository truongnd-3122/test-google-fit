package com.example.myapplication.data.remote.api


import android.util.Log
import com.example.myapplication.data.remote.ErrorState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import toBaseException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

open class BaseApiConfig {

    protected suspend fun <T : Any> safeApiCallFlow(
        call: suspend () -> T
    ): Flow<ResultWrapper<T>> {
        return try {
            flowOf(ResultWrapper.success(call.invoke()))
        } catch (throwable: Throwable) {
            Log.d("zzz BaseApiConfig 1", throwable.toString())
            // return specific error
            val errorState = when (throwable) {
                is UnknownHostException, is ConnectException -> {
                    ErrorState()
                }
                is SocketTimeoutException -> {
                    ErrorState()
                }
                else -> {

                    val baseException = throwable.toBaseException()
                    Log.d("zzz BaseApiConfig 2", baseException.message.toString())
                    baseException.serverErrorResponse ?: ErrorState(message = baseException.message)

                }
            }

            Log.d("zzz", errorState.toString())
            flowOf(ResultWrapper.error(errorState))
        }
    }

}

