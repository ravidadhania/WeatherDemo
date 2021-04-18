package com.weatherdemo.network

import com.weatherdemo.constants.MyConfig
import com.weatherdemo.model.request.*
import com.weatherdemo.model.response.*
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*


interface AppRestApiFast {

    @GET(MyConfig.Endpoints.TODAY_FORECAST)
    fun getTodayForeCast(
        @Query(MyConfig.APIParam.lat) lat: Double,
        @Query(MyConfig.APIParam.lon) lon: Double,
        @Query(MyConfig.APIParam.appid) appId: String,
        @Query(MyConfig.APIParam.units) units: String,
    ): Observable<Response<TodaysForecastResponseModel>>

    @GET(MyConfig.Endpoints.FIVE_DAY_FORECAST)
    fun getFiveDaysForeCast(
        @Query(MyConfig.APIParam.lat) lat: Double,
        @Query(MyConfig.APIParam.lon) lon: Double,
        @Query(MyConfig.APIParam.appid) appId: String,
        @Query(MyConfig.APIParam.units) units: String,
    ): Observable<Response<FiveDayForeCastResponseModel>>
}