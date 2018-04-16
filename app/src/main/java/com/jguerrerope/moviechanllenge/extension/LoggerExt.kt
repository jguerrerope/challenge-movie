package com.jguerrerope.moviechanllenge.extension

import android.util.Log

fun Any.logd(message: String) {
    Log.d(javaClass.name, message)
}

fun Any.logw(message: String) {
    Log.w(javaClass.name, message)
}

fun Any.loge(message: String) {
    Log.e(javaClass.name, message)
}

fun Any.loge(message: String,e: Throwable) {
    Log.e(javaClass.name, message,e)
}

fun Any.logv(message: String) {
    Log.v(javaClass.name, message)
}

fun Any.logi(message: String) {
    Log.i(javaClass.name, message)
}

fun Any.logwtf(message: String) {
    Log.wtf(javaClass.name, message)
}


