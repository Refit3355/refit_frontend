package com.refit.app.data.home.modelAndView

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.refit.app.data.health.HealthRepo
import com.refit.app.data.product.model.Product
import com.refit.app.data.product.repository.RecommendationRepository
import com.refit.app.network.weather.WeatherRetrofit
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import java.util.*

class HomeViewModel : ViewModel() {

    data class UiState(
        val steps: Long? = null,
        val sleepMinutes: Long? = null,
        val temperature: Double? = null,
        val weatherCode: Int? = null,
        val stepProducts: List<Product> = emptyList(),
        val sleepProducts: List<Product> = emptyList(),
        val weatherProducts: List<Product> = emptyList(),
        val rhythmProducts: List<Product> = emptyList()
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    private val recommendationRepo = RecommendationRepository()

    private var pollingJob: Job? = null

    fun loadData(ctx: Context) {
        loadHealth(ctx)     // 최초 1회 로드
        loadWeather(ctx)
        loadProducts()
    }

    // === Health Connect 데이터 읽기 ===
    private fun loadHealth(ctx: Context) {
        viewModelScope.launch {
            val client = HealthRepo.client(ctx)
            val granted = client.permissionController.getGrantedPermissions()
            if (granted.containsAll(HealthRepo.readPerms)) {
                val rows = HealthRepo.readDailyAll(ctx, days = 1)
                if (rows.isNotEmpty()) {
                    val today = rows.last()
                    _uiState.value = _uiState.value.copy(
                        steps = today.steps,
                        sleepMinutes = today.sleepMinutes
                    )
                }
            } else {
                _uiState.value = _uiState.value.copy(
                    steps = null,
                    sleepMinutes = null
                )
            }
        }
    }

    // === Health 데이터 Polling ===
    fun startHealthPolling(ctx: Context, intervalMs: Long = 60_000L) {
        // 이미 실행중이면 새로 안만듦
        if (pollingJob?.isActive == true) return

        pollingJob = viewModelScope.launch {
            while (isActive) {
                loadHealth(ctx)
                delay(intervalMs)
            }
        }
    }

    fun stopHealthPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }

    // === 날씨 데이터 로드 ===
    private fun loadWeather(ctx: Context) {
        viewModelScope.launch {
            val loc = getCurrentLocation(ctx)
            if (loc != null) {
                try {
                    val resp = WeatherRetrofit.api.getWeather(
                        latitude = loc.latitude,
                        longitude = loc.longitude
                    )
                    _uiState.value = _uiState.value.copy(
                        temperature = resp.current_weather?.temperature,
                        weatherCode = resp.current_weather?.weathercode
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    _uiState.value = _uiState.value.copy(
                        temperature = null,
                        weatherCode = null
                    )
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun getCurrentLocation(context: Context): Location? {
        val client = LocationServices.getFusedLocationProviderClient(context)
        return try {
            client.lastLocation.await()
        } catch (e: Exception) {
            null
        }
    }

    // === 수면시간 포맷 ===
    fun formatSleep(minutes: Long): String {
        val h = minutes / 60
        val m = minutes % 60
        return if (minutes == 0L) "--" else String.format(Locale.getDefault(), "%d시간 %d분", h, m)
    }

    // === 추천상품 로드 ===
    private fun loadProducts() {
        viewModelScope.launch {
            val stepProducts = recommendationRepo.fetchRecommendations(0, 10).getOrElse { emptyList() }
            val sleepProducts = recommendationRepo.fetchRecommendations(1, 10).getOrElse { emptyList() }
            val weatherProducts = recommendationRepo.fetchRecommendations(2, 10).getOrElse { emptyList() }
            val rhythmProducts = recommendationRepo.fetchRecommendations(3, 10).getOrElse { emptyList() }
            _uiState.value = _uiState.value.copy(
                stepProducts = stepProducts,
                sleepProducts = sleepProducts,
                weatherProducts = weatherProducts,
                rhythmProducts = rhythmProducts
            )
        }
    }
}
