package com.refit.app.data.health.reader

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.refit.app.util.health.retryBackoff
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.Period
import java.time.LocalDateTime

/**
 * 걸음 수, 활동 칼로리 데이터 리더
 */
suspend fun readStepsAndActive(
    client: HealthConnectClient,
    start: LocalDateTime,
    end: LocalDateTime
): Pair<Map<LocalDate, Long>, Map<LocalDate, Double>> {
    val dailySteps = mutableMapOf<LocalDate, Long>()
    val dailyActive = mutableMapOf<LocalDate, Double>()

    val grouped = retryBackoff {
        client.aggregateGroupByPeriod(
            AggregateGroupByPeriodRequest(
                metrics = setOf(
                    StepsRecord.COUNT_TOTAL,
                    ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL
                ),
                timeRangeFilter = TimeRangeFilter.between(start, end),
                timeRangeSlicer = Period.ofDays(1)
            )
        )
    }

    grouped.forEach { bucket ->
        val d = bucket.startTime.toLocalDate()
        dailySteps[d] = bucket.result[StepsRecord.COUNT_TOTAL] ?: 0L
        dailyActive[d] =
            bucket.result[ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL]?.inKilocalories ?: 0.0
    }

    delay(100)
    return dailySteps to dailyActive
}
