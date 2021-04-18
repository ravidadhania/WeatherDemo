package com.weatherdemo.custompreference

import android.content.Context
import android.content.SharedPreferences

class CustomPreference {

    companion object {

        private fun getSharedPreferences(context: Context): SharedPreferences? {
            return context.getSharedPreferences("App", Context.MODE_PRIVATE)
        }

        fun setPreference(context: Context?, key: String?, `val`: String?) {
            val settings = getSharedPreferences(context!!)
            val editor = settings?.edit()
            editor!!.putString(key, `val`)
            editor.apply()
        }

        fun getPreference(context: Context?, key: String?): String? {
            val prefs = getSharedPreferences(context!!)
            return prefs!!.getString(key, "")
        }

        fun removeAllPreference(context: Context?) {
            val settings = getSharedPreferences(context!!)
            val editor = settings!!.edit()
            editor.clear()
            editor.apply()
        }
    }
}