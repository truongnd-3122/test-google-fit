package com.example.myapplication.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object PrefsHelper {

    private lateinit var prefs: SharedPreferences

    private const val PREFS_NAME = "S_GaaS"

    const val ID_USER = "id_user"
    const val TOKEN = "token"
    const val DATA_USER = "data_user"

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun read(key: String, value: String): String? {
        return prefs.getString(key, value)
    }

    fun read(key: String, value: Long): Long? {
        return prefs.getLong(key, value)
    }

    fun write(key: String, value: String) {
        val prefsEditor: SharedPreferences.Editor = prefs.edit()
        with(prefsEditor) {
            putString(key, value)
            commit()
        }
    }

    fun write(key: String, value: Long) {
        val prefsEditor: SharedPreferences.Editor = prefs.edit()
        with(prefsEditor) {
            putLong(key, value)
            commit()
        }
    }

    fun remove(key: String) = prefs.edit { remove(key).commit() }

    fun clearAll() = prefs.edit { clear().commit() }

//    fun storeData(signUpResponse: SignUpResponse){
//        prefs.edit { putString(DATA_USER, UserGson.gson.toJson(signUpResponse)) }
//    }
//
//    fun getData(): SignUpResponse{
//        val data = prefs.getString(DATA_USER, "") ?: ""
//        return UserGson.gson.fromJson(data, SignUpResponse::class.java)
//    }
}