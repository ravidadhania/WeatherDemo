package com.weatherdemo.model.response

data class TodaysForecastResponseModel(
    val base: String = "",
    val clouds: Clouds = Clouds(),
    val cod: Int = 0,
    val coord: Coord = Coord(),
    val dt: Int = 0,
    val id: Int = 0,
    val main: Main = Main(),
    val name: String = "",
    val sys: Sys = Sys(),
    val timezone: Int = 0,
    val visibility: Int = 0,
    val weather: List<Weather> = listOf(),
    val wind: Wind = Wind()
) {
    data class Clouds(
        val all: Int = 0
    )

    data class Coord(
        val lat: Double = 0.0,
        val lon: Double = 0.0
    )

    data class Main(
        val feels_like: Double = 0.0,
        val humidity: Int = 0,
        val pressure: Int = 0,
        val temp: Double = 0.0,
        val temp_max: Double = 0.0,
        val temp_min: Double = 0.0
    )

    data class Sys(
        val country: String = "",
        val id: Int = 0,
        val sunrise: Int = 0,
        val sunset: Int = 0,
        val type: Int = 0
    )

    data class Weather(
        val description: String = "",
        val icon: String = "",
        val id: Int = 0,
        val main: String = ""
    )

    data class Wind(
        val deg: Int = 0,
        val speed: Double = 0.0
    )
}