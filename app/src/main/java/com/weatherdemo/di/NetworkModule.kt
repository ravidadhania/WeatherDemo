package com.weatherdemo.di

import android.annotation.SuppressLint
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.weatherdemo.constants.MyConfig
import com.weatherdemo.network.AppRestApiFast
import com.weatherdemo.network.AppRestService
import com.weatherdemo.network.AuthInterceptor
import com.weatherdemo.network.CustomLoggingInterceptor
import okhttp3.OkHttpClient
import org.koin.core.qualifier.StringQualifier
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


const val EXTENDED_TIMEOUT = "EXTENDED_TIMEOUT"
const val NORMAL_TIMEOUT = "NORMAL_TIMEOUT"


val networkModule = module {

    single { AppRestService(get()) }

    single { provideAppServiceFast(get(StringQualifier(NORMAL_TIMEOUT)), get()) }

    single(StringQualifier(EXTENDED_TIMEOUT)) { provideOkHttpClientExtendedTimeOut(get()) }

    single(StringQualifier(NORMAL_TIMEOUT)) { provideOkHttpClient(get()) }

    single { provideGson() }

    single { AuthInterceptor() }
}

private fun configureRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
    return Retrofit.Builder()
        .baseUrl(MyConfig.Endpoints.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(okHttpClient)
        .build()
}

@SuppressLint("SimpleDateFormat")
fun provideGson(): Gson {
    val builder = GsonBuilder()
    builder.disableHtmlEscaping()

    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    format.timeZone = TimeZone.getTimeZone("UTC")
    builder.setDateFormat(format.toLocalizedPattern())

    return builder.create()
}

fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
    val builder = OkHttpClient().newBuilder()
    builder.readTimeout(30, TimeUnit.SECONDS)
    builder.connectTimeout(30, TimeUnit.SECONDS)
    builder.writeTimeout(30, TimeUnit.SECONDS)
    builder.addInterceptor(authInterceptor)
 /*   if (BuildConfig.DEBUG) {

    }*/
    val logging = CustomLoggingInterceptor()
    logging.setLevel(CustomLoggingInterceptor.Level.BODY)
    builder.addInterceptor(logging)
    return builder.build()
}

fun provideOkHttpClientExtendedTimeOut(authInterceptor: AuthInterceptor): OkHttpClient {
    val builder = OkHttpClient().newBuilder()
    builder.readTimeout(5, TimeUnit.MINUTES)
    builder.connectTimeout(5, TimeUnit.MINUTES)
    builder.writeTimeout(5, TimeUnit.MINUTES)
    builder.addInterceptor(authInterceptor)
    /*if (BuildConfig.DEBUG) {

    }*/
    val logging = CustomLoggingInterceptor()
    logging.setLevel(CustomLoggingInterceptor.Level.BODY)
    builder.addInterceptor(logging)
    return builder.build()
}


fun provideAppServiceFast(okHttpClient: OkHttpClient, gson: Gson): AppRestApiFast {
    val retrofit = configureRetrofit(okHttpClient, gson)
    return retrofit.create(AppRestApiFast::class.java)
}

