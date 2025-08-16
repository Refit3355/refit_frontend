package com.refit.app.data.health.reader

import android.util.Log
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.NutritionRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.refit.app.util.health.retryBackoff
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * 영양 (섭취 칼로리) 데이터 리더
 */
suspend fun readNutrition(
    client: HealthConnectClient,
    start: Instant,
    end: Instant,
    zone: ZoneId
): Map<LocalDate, Double> {
    val dailyIntake = mutableMapOf<LocalDate, Double>()
    var pageCount = 0
    var page = retryBackoff {
        client.readRecords(
            ReadRecordsRequest(
                NutritionRecord::class,
                timeRangeFilter = TimeRangeFilter.between(start, end),
                ascendingOrder = false
            )
        )
    }
    while (true) {
        pageCount++
        if (page.records.isEmpty()) {
            Log.w("HealthRepo", "영양: 빈 페이지 (page=$pageCount) → 중단")
            break
        }
        page.records.forEach {
            val d = it.startTime.atZone(zone).toLocalDate()
            dailyIntake[d] = (dailyIntake[d] ?: 0.0) + (it.energy?.inKilocalories ?: 0.0)
        }
        val next = page.pageToken ?: break
        if (pageCount > 50) {
            Log.e("HealthRepo", "영양: 페이지 50 초과 → 강제 중단")
            break
        }
        delay(50)
        page = retryBackoff {
            client.readRecords(
                ReadRecordsRequest(
                    NutritionRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(start, end),
                    pageToken = next,
                    ascendingOrder = false
                )
            )
        }
    }
    return dailyIntake
}
