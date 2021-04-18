package com.weatherdemo.base

import android.app.Application
import com.weatherdemo.di.appModule
import com.weatherdemo.di.networkModule
import com.weatherdemo.room.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        sInstance = this
        AppDatabase.get(this@BaseApplication)

        startKoin {
            androidContext(this@BaseApplication)
            modules(appModule, networkModule)
        }
    }

    companion object {

        private lateinit var sInstance: BaseApplication

        fun getInstance() = sInstance
    }
}