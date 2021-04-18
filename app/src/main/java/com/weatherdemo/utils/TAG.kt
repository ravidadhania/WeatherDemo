package com.weatherdemo.utils

import android.util.Log

const val TAG = "App"
const val MESSAGE = "Message"

class LogUtils {

    companion object {

        fun d(tag: Any? = null, message: String?) = Log.d(getTag(tag), getMessage(message))

        fun v(tag: Any? = null, message: String?) = Log.v(getTag(tag), getMessage(message))

        fun i(tag: Any? = null, message: String?) = Log.i(getTag(tag), getMessage(message))

        fun e(exception: Throwable, tag: Any? = null) = Log.e(getTag(tag), "", exception)

        private fun getTag(tag: Any?): String = tag?.toString() ?: TAG

        private fun getMessage(message: String?) = message ?: MESSAGE
    }
}