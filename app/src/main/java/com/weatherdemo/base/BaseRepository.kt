package com.weatherdemo.base

import com.weatherdemo.callbacks.APIResponseCallback
import com.weatherdemo.network.APICallAndResponseHelper
import com.weatherdemo.network.AppRestService
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.Response

open class BaseRepository: KoinComponent {

    private var compositeDisposable = CompositeDisposable()
    internal val appRestService: AppRestService by inject()

    fun onCleared() {
        compositeDisposable.clear()
    }

    fun <T> sendApiCall(observable: Observable<Response<T>>, callback: APIResponseCallback<T>?) {
        APICallAndResponseHelper.call(observable, callback, compositeDisposable)
    }


}