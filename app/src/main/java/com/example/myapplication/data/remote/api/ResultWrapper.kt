package com.example.myapplication.data.remote.api

import com.example.myapplication.data.remote.ErrorState

data class ResultWrapper<out T : Any>(val status: Status, val data: T?,
                                      val throwable: ErrorState? = null) {
    companion object {
        fun <T : Any> success(data: T): ResultWrapper<T> = ResultWrapper(status = Status.SUCCESS,
                                                                         data = data)
        fun <T : Any> error(errorState: ErrorState? = null): ResultWrapper<T> =
                ResultWrapper(status = Status.ERROR, null, errorState)
    }
}

//data class ResultWrapper<out T : Any>(val status: Status, val data: T?,
//                                      val throwable: Throwable? = null) {
//    companion object {
//        fun <T : Any> success(data: T): ResultWrapper<T> = ResultWrapper(status = Status.SUCCESS,
//            data = data)
//        fun <T : Any> error(throwable: Throwable? = null): ResultWrapper<T> =
//            ResultWrapper(status = Status.ERROR, null, throwable)
//    }
//}