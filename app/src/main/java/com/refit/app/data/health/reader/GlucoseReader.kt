package com.refit.app.data.health.reader

import android.util.Log
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.BloodGlucoseRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.refit.app.util.health.retryBackoff
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * 혈당 데이터 리더
 */
suspend fun readGlucose(
    client: HealthConnectClient,
    start: Instant,
    end: Instant,
    zone: ZoneId
): Map<LocalDate, Double?> {
    val dailyGlucose = mutableMapOf<LocalDate, Double?>()
    var pageCount = 0
    var page = retryBackoff {
        client.readRecords(
            ReadRecordsRequest(
                BloodGlucoseRecord::class,
                timeRangeFilter = TimeRangeFilter.between(start, end),
                ascendingOrder = false
            )
        )
    }
    while (true) {
        pageCount++
        if (page.records.isEmpty()) {
            Log.w("HealthRepo", "혈당: 빈 페이지 (page=$pageCount) → 중단")
            break
        }
        page.records.forEach {
            val d = it.time.atZone(zone).toLocalDate()
            val mgdl: Double? = it.level.inMilligramsPerDeciliter
            if (mgdl != null) dailyGlucose.putIfAbsent(d, mgdl)
        }
        val next = page.pageToken ?: break
        if (pageCount > 50) {
            Log.e("HealthRepo", "혈당: 페이지 50 초과 → 강제 중단")
            break
        }
        delay(50)
        page = retryBackoff {
            client.readRecords(
                ReadRecordsRequest(
                    BloodGlucoseRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(start, end),
                    pageToken = next,
                    ascendingOrder = false
                )
            )
        }
    }
    return dailyGlucose
}
