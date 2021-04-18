package com.weatherdemo.interfaces

import android.location.Location

interface CurrentLocationResult {
    fun gotCurrentLocation(location: Location?)
}