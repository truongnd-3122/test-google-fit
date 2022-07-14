package com.example.myapplication.ui.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.constants.Constants
import com.example.myapplication.data.remote.ErrorState
import com.example.myapplication.data.remote.api.ResultWrapper
import com.example.myapplication.data.remote.api.Status
import com.example.myapplication.data.repository.MovieRepository
import com.example.myapplication.utils.SingleLiveEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.plus
import timber.log.Timber
import toBaseException
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

open class BaseViewModel : ViewModel() {

    @Inject
    lateinit var repoToLogAdjustEvent: MovieRepository


    // loading flag
    val isLoading by lazy { MutableLiveData(false) }

    // error message
    val errorMessage by lazy { SingleLiveEvent<String>() }

    // optional flags
    val noInternetConnectionEvent by lazy { SingleLiveEvent<Unit>() }
    val connectTimeoutEvent by lazy { SingleLiveEvent<Unit>() }
    val forceUpdateAppEvent by lazy { SingleLiveEvent<Unit>() }
    val serverMaintainEvent by lazy { SingleLiveEvent<Unit>() }
    val forbiddenEvent by lazy { SingleLiveEvent<Unit>() }
    val unknownErrorEvent = SingleLiveEvent<Int?>()
    val httpNotFoundEvent by lazy { SingleLiveEvent<Unit>() }
    val updateTokenFailed by lazy { SingleLiveEvent<Unit>() }
    val badGatewayEvent by lazy { SingleLiveEvent<Unit>() }
    val httpNotImplement by lazy { SingleLiveEvent<Unit>() }

    // exception handler for coroutine
    private val exceptionHandler by lazy {
        CoroutineExceptionHandler { context, throwable ->
            onError(throwable)
        }
    }

    // viewModelScope with exception handler
    protected val viewModelScopeExceptionHandler by lazy { viewModelScope + exceptionHandler }

    open fun reLoadData() {}

    /**
     * handle throwable when load fail
     */
    open fun onError(throwable: Throwable) {
        hideLoading()
        when (throwable) {
            // case no internet connection
            is UnknownHostException -> {
                noInternetConnectionEvent.call()
            }
            is ConnectException -> {
                noInternetConnectionEvent.call()
            }
            // case request time out
            is SocketTimeoutException -> {
                connectTimeoutEvent.call()
            }
            else -> {
                // convert throwable to base exception to get error information
                val baseException = throwable.toBaseException()
                when (baseException.httpCode) {
                    Constants.FORCE_UPDATE_APP_CODE -> {
                        forceUpdateAppEvent.call()
                    }
                    HttpURLConnection.HTTP_FORBIDDEN -> {
                        forbiddenEvent.call()
                    }
                    HttpURLConnection.HTTP_UNAVAILABLE -> {
                        serverMaintainEvent.call()
                    }
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        httpNotFoundEvent.call()
                    }
                    HttpURLConnection.HTTP_UNAUTHORIZED -> {
                        updateTokenFailed.call()
                    }
                    else -> {
                        Timber.e(throwable)
                        unknownErrorEvent.value = baseException.httpCode
                    }
                }
            }
        }
    }

    open fun showError(e: Throwable) {
        errorMessage.value = e.message
    }

    fun showLoading() {
        isLoading.value = true
    }

    fun hideLoading() {
        isLoading.value = false
    }

    protected suspend fun <T : Any> Flow<ResultWrapper<T>>.handleResponse(
        loading: MutableLiveData<Boolean>? = null,
        onSuccess: (T) -> Unit,
        onError: ((ErrorState?) -> Unit)? = null,
        isHandleInFragment: Boolean = true
    ) {
        onCompletion {
            loading?.postValue(false) ?: showLoading()
        }.distinctUntilChanged().collect {
            when (it.status) {
                Status.SUCCESS -> {
                    if (it.data != null){
                        onSuccess.invoke(it.data)
//                        loading?.postValue(false)
                    }
                }

                Status.ERROR -> {
                    Timber.tag("handleResponse").d(it.throwable.toString())
                    if (it.throwable != null) {
                        onError?.invoke(it.throwable)
//                        if (isHandleInFragment) {
//                            onError?.invoke(it.throwable)
//                        } else {
//                            Log.d("zzz ERROR", it.throwable.errorType.toString())
//                            when (it.throwable.errorType) {
//                                ErrorType.NETWORK.mode -> {
//                                    noInternetConnectionEvent.call()
//                                }
//                                ErrorType.UNEXPECTED.mode -> {
//                                    unknownErrorEvent.call()
//                                }
//                                ErrorType.TIMEOUT.mode -> {
//                                    connectTimeoutEvent.call()
//                                }
//                                ErrorType.SERVER.mode-> {
//                                    when (it.throwable.code) {
//                                        Constants.FORCE_UPDATE_APP_CODE -> {
//                                            forceUpdateAppEvent.call()
//                                        }
//                                        HttpURLConnection.HTTP_FORBIDDEN -> {
//                                            forbiddenEvent.call()
//                                        }
//                                        HttpURLConnection.HTTP_UNAVAILABLE -> {
//                                            serverMaintainEvent.call()
//                                        }
//                                        HttpURLConnection.HTTP_NOT_FOUND -> {
//                                            httpNotFoundEvent.call()
//                                        }
//                                        HttpURLConnection.HTTP_UNAUTHORIZED -> {
//                                            updateTokenFailed.call()
//                                        }
//                                        HttpURLConnection.HTTP_BAD_GATEWAY -> {
//                                            badGatewayEvent.call()
//                                        }
//                                        HttpURLConnection.HTTP_NOT_IMPLEMENTED -> {
//                                            httpNotImplement.call()
//                                        }
//                                        else -> {
//                                            unknownErrorEvent.call()
//                                        }
//                                    }
//                                }
//                                else -> {
//                                    onError?.invoke(it.throwable)
//                                }
//                            }
//                        }
                    }
                }
            }
        }
    }

}

