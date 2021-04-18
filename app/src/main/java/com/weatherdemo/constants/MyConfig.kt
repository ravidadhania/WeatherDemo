package com.weatherdemo.constants

class MyConfig {

    object Endpoints {

        //http://api.openweathermap.org/data/2.5/weather?lat=0&lon=0&appid=fae7190d7e6433ec3a452 85ffcf55c86
        //http://api.openweathermap.org/data/2.5/forecast?lat=0&lon=0&appid=fae7190d7e6433ec3a452 85ffcf55c86&units=metric
        const val BASE_URL = "http://api.openweathermap.org/data/2.5/"

        const val TODAY_FORECAST = "weather"
        const val FIVE_DAY_FORECAST = "forecast"
    }

    object REQUEST_CODE {

        const val REQUEST_CODE_PERMISSION = 101
        const val LOCATION_REQUEST_CODE = 102
        const val REQUEST_CHECK_SETTINGS = 103
    }


    object DateFormat {

        const val yyyy_MM_dd_T_HH_mm_ss = "yyyy-MM-dd'T'HH:mm:ss"
        const val yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss"
        const val yyyy_MM_dd = "yyyy-MM-dd"
        const val hh_mm_aa = "hh:mm aa" //11:25 PM
        const val EEEE = "EEE" //Thursday

    }

    object APIParam {

        const val lat = "lat"
        const val lon = "lon"
        const val appid = "appid"
        const val units = "units"

    }

    object PreferenceKeys {

        const val unit = "unit"

    }

    object Keys {

        const val latitude = "latitude"
        const val longitude = "longitude"
        const val city = "city"

    }

    object Tag {

        const val CityDetail = "CityDetail"

    }

}
