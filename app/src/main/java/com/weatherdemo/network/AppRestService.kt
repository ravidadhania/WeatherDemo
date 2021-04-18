package com.weatherdemo.network

import com.weatherdemo.model.response.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

class AppRestService(
    private val appRestApiFast: AppRestApiFast
) {

    fun getTodayForeCast(
        lat: Double,
        lon: Double,
        appId: String,
        units:String
    ): Observable<Response<TodaysForecastResponseModel>> {
        val apiCall = appRestApiFast.getTodayForeCast(lat,lon,appId,units)
        return apiCall.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
    }

    fun getFiveDaysForeCast(
        lat: Double,
        lon: Double,
        appId: String,
        units:String
    ): Observable<Response<FiveDayForeCastResponseModel>> {
        val apiCall = appRestApiFast.getFiveDaysForeCast(lat,lon,appId,units)
        return apiCall.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
    }
}
