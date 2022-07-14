package com.example.myapplication

import android.app.Application
import androidx.multidex.MultiDex
import dagger.hilt.android.HiltAndroidApp
import com.example.myapplication.utils.PrefsHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import timber.log.Timber
import java.util.logging.Level
import java.util.logging.Logger

@HiltAndroidApp
class MainApplication : Application() {

    // use this to run coroutines that need a longer lifetime than the calling scope (like viewModelScope) might offer in our app
    val appScope by lazy {
        CoroutineScope(SupervisorJob())
    }

    override fun onCreate() {
        super.onCreate()

        MultiDex.install(this)


        PrefsHelper.init(applicationContext)

        if (enableLogging()) {
            // init timber
            Timber.plant(Timber.DebugTree())

            // init stetho
            Logger.getLogger(OkHttpClient::class.java.name).level = Level.FINE
        } else {
//            handleUncaughtException()
        }
    }
}

fun enableLogging() = BuildConfig.BUILD_TYPE != "release"
