package com.weatherdemo.base

import androidx.lifecycle.ViewModel
import com.weatherdemo.utils.LogUtils

open class BaseViewModel<T : BaseRepository?>(var repository: T): ViewModel() {

    override fun onCleared() {
        LogUtils.d(null, "on cleared called and repository is $repository")
//        repository?.onCleared()
        super.onCleared()
    }
}