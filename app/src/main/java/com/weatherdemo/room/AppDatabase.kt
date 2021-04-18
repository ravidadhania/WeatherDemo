package com.weatherdemo.room

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.weatherdemo.dao.CityMasterDao
import com.weatherdemo.entity.CityMaster

@Database(version = 1,entities = [CityMaster::class],exportSchema = true )
abstract class AppDatabase:RoomDatabase() {
    companion object{
        fun get(application: Application): AppDatabase {
            return Room.databaseBuilder(application, AppDatabase::class.java,"WeatherDemoDB")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build()
        }
    }

    abstract fun getCityMasterDao(): CityMasterDao
}