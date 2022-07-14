package com.example.myapplication.utils

import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.example.myapplication.enableLogging
import java.io.IOException

// # Kotlin Extensions

//- [View](#view)
//- [Context](#context)
//- [Fragment](#fragment)
//- [Activity](#activity)
//- [ViewGroup](#viewgroup)
//- [TextView](#textview)
//- [String](#string)
//- [Other](#other)


// ## View

fun Exception.safeLog() {
    if (enableLogging()) printStackTrace()
}

/**
 * Extension method to provide simpler access to {@link View#getResources()#getString(int)}.
 */
fun View.getString(stringResId: Int): String? = try {
    resources.getString(stringResId)
} catch (e: Resources.NotFoundException) {
    null
}

/**
 * Extension method to show a keyboard for View.
 */
fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    this.requestFocus()
    imm.showSoftInput(this, 0)
}


/**
 * Try to hide the keyboard and returns whether it worked
 * https://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
 */
fun View.hideKeyboard(): Boolean {
    try {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        return inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    } catch (ignored: RuntimeException) {
    }
    return false
}

/**
 * get json string from file
 * @param fileName: file name in the asset folder.
 */
fun AssetManager.getJsonStringFromFile(fileName: String): String? {
    return try {
        val inputStream = open(fileName)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        String(buffer, Charsets.UTF_8)
    } catch (e: IOException) {
        null
    }
}
