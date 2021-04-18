package com.weatherdemo.callbacks

import com.weatherdemo.network.RestResponse


interface IOnAPILoadingViewCallBacks {
    fun <T> responseStatusSuccess(it: RestResponse<T>)
    fun <T> responseStatusError(it: RestResponse<T>)
    fun responseStatusLoading()
}