package com.refit.app.util.home

import com.refit.app.R

fun getWeatherIcon(temp: Double?, weatherCode: Int?): Int {
    return when {
        // 눈
        weatherCode in listOf(71, 73, 75) -> R.drawable.jellbbo_snow

        // 비
        weatherCode in listOf(61, 63, 65) -> R.drawable.jellbbo_rainy

        // 습함
        weatherCode in listOf(51, 53, 55) -> R.drawable.jellbbo_humid

        // 더움
        temp != null && temp >= 28 -> R.drawable.jellbbo_hot

        // 추움
        temp != null && temp <= 5 -> R.drawable.jellbbo_cold

        // 맑음
        else -> R.drawable.jellbbo_sunny
    }
}
