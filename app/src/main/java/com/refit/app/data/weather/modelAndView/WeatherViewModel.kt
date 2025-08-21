package com.refit.app.data.weather.modelAndView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refit.app.network.weather.WeatherRetrofit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class WeatherViewModel : ViewModel() {

    data class UiState(
        // 현재 날씨
        val temperature: Double? = null,
        val windspeed: Double? = null,
        val weatherCode: Int? = null,

        // 한달 치 데이터
        val dates: List<String> = emptyList(),
        val maxTemps: List<Double> = emptyList(),
        val minTemps: List<Double> = emptyList(),
        val precipitations: List<Double> = emptyList(),
        val snowfalls: List<Double> = emptyList(),
        val humidities: List<Double> = emptyList(),

        val loading: Boolean = false,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    init {
        loadWeather() // 기본 서울
    }

    fun loadWeather(latitude: Double = 37.5665, longitude: Double = 126.9780) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(loading = true)

                val formatter = DateTimeFormatter.ISO_LOCAL_DATE
                val today = LocalDate.now()
                val start = today.minusDays(7).format(formatter)
                val end = today.format(formatter)

                val resp = WeatherRetrofit.api.getWeatherWithPast7Days(
                    latitude = latitude,
                    longitude = longitude,
                    startDate = start,
                    endDate = end
                )

                _uiState.value = _uiState.value.copy(
                    temperature = resp.current_weather?.temperature,
                    windspeed = resp.current_weather?.windspeed,
                    weatherCode = resp.current_weather?.weathercode,

                    dates = resp.daily?.time ?: emptyList(),
                    maxTemps = resp.daily?.temperature_2m_max ?: emptyList(),
                    minTemps = resp.daily?.temperature_2m_min ?: emptyList(),
                    precipitations = resp.daily?.precipitation_sum ?: emptyList(),
                    snowfalls = resp.daily?.snowfall_sum ?: emptyList(),
                    humidities = resp.daily?.relative_humidity_2m_max ?: emptyList(),

                    loading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    loading = false
                )
            }
        }
    }
}
