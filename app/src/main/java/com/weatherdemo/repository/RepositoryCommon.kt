package com.weatherdemo.repository

import com.weatherdemo.base.BaseRepository
import com.weatherdemo.callbacks.APIResponseCallback
import com.weatherdemo.model.request.*
import com.weatherdemo.model.response.*

class RepositoryCommon : BaseRepository() {

    fun getTodayForeCast(
        lat: Double,
        lon: Double,
        appId: String,
        units:String,
        callback: APIResponseCallback<TodaysForecastResponseModel>
    ) {
        sendApiCall(appRestService.getTodayForeCast(lat,lon,appId,units), callback)
    }

    fun getFiveDaysForeCast(
        lat: Double,
        lon: Double,
        appId: String,
        units:String,
        callback: APIResponseCallback<FiveDayForeCastResponseModel>
    ) {
        sendApiCall(appRestService.getFiveDaysForeCast(lat,lon,appId,units), callback)
    }
}