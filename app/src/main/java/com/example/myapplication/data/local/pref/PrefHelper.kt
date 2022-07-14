package com.example.myapplication.data.local.pref

interface PrefHelper {

    fun isFirstRun(): Boolean

    fun remove(key: String)

    fun clear()

    fun storeAccessToken(accessToken: String)

    fun getAccessToken(): String?

    fun storeRefreshToken(refreshToken: String)

    fun getRefreshToken(): String?

    fun storeEmail(email: String)

    fun getEmail(): String

}
