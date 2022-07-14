package com.example.myapplication.data.local.pref

import android.content.SharedPreferences
import androidx.core.content.edit
import com.squareup.moshi.Moshi
import javax.inject.Inject

class AppPrefs @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    val moshi: Moshi
) : PrefHelper {

    companion object {
        private const val FIRST_RUN = "FIRST_RUN"
        private const val ACCESS_TOKEN = "ACCESS_TOKEN"
        private const val REFRESH_TOKEN = "REFRESH_TOKEN"
        private const val USER_EMAIL = "USER_EMAIL"
    }

    override fun isFirstRun(): Boolean {
        val isFirstRun = sharedPreferences.getBoolean(FIRST_RUN, true)
        if (isFirstRun) {
            sharedPreferences.edit { putBoolean(FIRST_RUN, false) }
        }
        return isFirstRun
    }

    override fun remove(key: String) {
        sharedPreferences.edit {
            remove(key)
        }
    }

    override fun clear() {
        sharedPreferences.edit { clear() }
    }

    override fun storeAccessToken(token: String) {
        sharedPreferences.edit {
            putString(ACCESS_TOKEN, token)
            commit()
        }
    }

    override fun getAccessToken(): String? {
        return sharedPreferences.getString(ACCESS_TOKEN, "")
    }

    override fun storeRefreshToken(refreshToken: String) {
        sharedPreferences.edit {
            putString(REFRESH_TOKEN, refreshToken)
            commit()
        }
    }

    override fun getRefreshToken(): String? {
        return sharedPreferences.getString(REFRESH_TOKEN, "")
    }

    override fun storeEmail(email: String) {
        sharedPreferences.edit {
            putString(USER_EMAIL, email)
            commit()
        }
    }

    override fun getEmail(): String {
        return sharedPreferences.getString(USER_EMAIL, "").toString()
    }

}
