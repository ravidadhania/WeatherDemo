package com.weatherdemo.di

import com.weatherdemo.base.BaseRepository
import com.weatherdemo.base.BaseViewModel
import com.weatherdemo.repository.RepositoryCommon
import com.weatherdemo.viewmodel.ViewModelCommon
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module


val appModule = module {
    viewModel { BaseViewModel<BaseRepository>(get()) }
    single { BaseRepository() }

    viewModel { ViewModelCommon(get()) }
    single { RepositoryCommon() }
}