package com.refit.app.network.weather

import retrofit2.http.GET
import retrofit2.http.Query

data class WeatherResponse(
    val current_weather: CurrentWeather?,
    val daily: DailyWeather? = null
)

data class CurrentWeather(
    val temperature: Double,
    val windspeed: Double,
    val winddirection: Double,
    val weathercode: Int,
    val time: String
)

data class DailyWeather(
    val time: List<String>,
    val temperature_2m_max: List<Double>,
    val temperature_2m_min: List<Double>,
    val precipitation_sum: List<Double>,
    val snowfall_sum: List<Double>,
    val relative_humidity_2m_max: List<Double>,
    val relative_humidity_2m_min: List<Double>
)

interface OpenMeteoService {
    // 현재 날씨
    @GET("v1/forecast?current_weather=true&timezone=auto")
    suspend fun getWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ): WeatherResponse

    // 한달 날씨
    @GET("v1/forecast?current_weather=true&timezone=auto")
    suspend fun getWeatherWithDaily(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("daily") daily: String = "temperature_2m_max,temperature_2m_min,precipitation_sum,snowfall_sum,relative_humidity_2m_max,relative_humidity_2m_min"
    ): WeatherResponse

    // 7일 전부터 오늘까지 날씨
    @GET("v1/forecast?current_weather=true&timezone=auto")
    suspend fun getWeatherWithPast7Days(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("daily") daily: String = "temperature_2m_max,temperature_2m_min,precipitation_sum,snowfall_sum,relative_humidity_2m_max,relative_humidity_2m_min",
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): WeatherResponse
}
