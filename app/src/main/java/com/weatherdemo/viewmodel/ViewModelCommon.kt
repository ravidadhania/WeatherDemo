package com.weatherdemo.viewmodel

import androidx.lifecycle.MutableLiveData
import com.weatherdemo.base.BaseViewModel
import com.weatherdemo.callbacks.APIResponseCallback
import com.weatherdemo.model.response.*
import com.weatherdemo.network.RestResponse
import com.weatherdemo.repository.RepositoryCommon

class ViewModelCommon(repository: RepositoryCommon) :
    BaseViewModel<RepositoryCommon>(repository) {

    var eventGetTodayForeCast = MutableLiveData<RestResponse<TodaysForecastResponseModel>>()
    var eventGetFiveDaysForeCast = MutableLiveData<RestResponse<FiveDayForeCastResponseModel>>()

    fun apiGetTodayForeCast(lat: Double, lon: Double, appId: String, units:String) {

        eventGetTodayForeCast.value = RestResponse()

        repository.getTodayForeCast(lat,lon,appId,units,
            APIResponseCallback {
                eventGetTodayForeCast.value = it
            })
    }

    fun apiGetFiveDaysForeCast(lat: Double, lon: Double, appId: String, units:String) {

        eventGetFiveDaysForeCast.value = RestResponse()

        repository.getFiveDaysForeCast(lat,lon,appId,units,
            APIResponseCallback {
                eventGetFiveDaysForeCast.value = it
            })
    }
}