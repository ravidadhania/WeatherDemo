package com.weatherdemo.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

//CityMasterTable
@Entity(tableName = "CityMaster")
class CityMaster (
    @PrimaryKey(autoGenerate = true) var IDNumber:Int,
    var City:String,
    var latitude:Double,
    var longitude:Double,
) {
    constructor(City: String,latitude: Double,longitude: Double) : this(0,City,latitude,longitude)
}




