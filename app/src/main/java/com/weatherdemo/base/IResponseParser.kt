package com.weatherdemo.base

import com.weatherdemo.callbacks.IOnAPILoadingViewCallBacks
import com.weatherdemo.network.RestResponse

open class IResponseParser<T>(private val loadingViewCallBack: IOnAPILoadingViewCallBacks?) {

    open fun onSuccess(it: RestResponse<T>) {
        loadingViewCallBack?.responseStatusSuccess(it)
    }

    open fun onError(it: RestResponse<T>) {
        loadingViewCallBack?.responseStatusError(it)
    }

    fun onLoading() {
        loadingViewCallBack?.responseStatusLoading()
    }
}