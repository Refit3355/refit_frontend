package com.refit.app.data.health.reader

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.refit.app.util.health.retryBackoff
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.Period
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * 걸음 수, 총 칼로리 데이터 리더
 */
suspend fun readStepsAndTotalCalories(
    client: HealthConnectClient,
    start: LocalDateTime,
    end: LocalDateTime
): Pair<Map<LocalDate, Long>, Map<LocalDate, Double>> {
    val dailySteps = mutableMapOf<LocalDate, Long>()
    val dailyCalories = mutableMapOf<LocalDate, Double>()

    val grouped = retryBackoff {
        client.aggregateGroupByPeriod(
            AggregateGroupByPeriodRequest(
                metrics = setOf(
                    StepsRecord.COUNT_TOTAL,
                    TotalCaloriesBurnedRecord.ENERGY_TOTAL
                ),
                timeRangeFilter = TimeRangeFilter.between(start, end),
                timeRangeSlicer = Period.ofDays(1)
            )
        )
    }

    grouped.forEach { bucket ->
        val zone = ZoneId.systemDefault()
        val d = bucket.startTime.atZone(zone).toLocalDate()
        dailySteps[d] = bucket.result[StepsRecord.COUNT_TOTAL] ?: 0L
        dailyCalories[d] =
            bucket.result[TotalCaloriesBurnedRecord.ENERGY_TOTAL]?.inKilocalories ?: 0.0  // ✅ 수정
    }

    delay(100)
    return dailySteps to dailyCalories
}
