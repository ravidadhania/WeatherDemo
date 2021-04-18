package com.weatherdemo.dao

import androidx.room.*

import com.weatherdemo.entity.CityMaster

@Dao
interface CityMasterDao {

    //Insert in City Master Table
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCityMaster(city: CityMaster)

    //Get All City
    @Transaction
    @Query("Select * from CityMaster")
    fun getCityList(): List<CityMaster>

    //Delete City
    @Transaction
    @Query("DELETE FROM CityMaster WHERE IDNumber = :idNumber ")
    fun deleteCity(idNumber: String)

    //Delete City Master
    @Transaction
    @Query("DELETE FROM CityMaster")
    fun clearCityMaster()

}