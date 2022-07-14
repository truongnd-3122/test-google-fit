package com.example.myapplication.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Patterns
import com.example.myapplication.data.remote.response.DataResponse
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import java.util.regex.Pattern


fun checkNetWorkConnection(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
    return activeNetwork?.isConnectedOrConnecting == true
}

fun validateEmail(emailAddress: String): Boolean{
//    return Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()
    return Pattern.compile("\\A[^@\\s]+@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})", Pattern.CASE_INSENSITIVE).matcher(emailAddress).matches()
}

fun validatePassword(password: String): Boolean{
    return password.length in 8..128
}

fun validatePasswordShort(password: String):Boolean{
    return password.length < 8
}

fun validatePasswordLong(password: String):Boolean{
    return password.length > 128
}

fun validateRePassword(rePassword: String, password: String): Boolean{
    return rePassword == password
}
