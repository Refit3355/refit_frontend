package com.refit.app.data.health.reader

import android.util.Log
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.refit.app.util.health.retryBackoff
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * 수면 데이터 리더
 */
suspend fun readSleep(
    client: HealthConnectClient,
    start: Instant,
    end: Instant,
    zone: ZoneId
): Map<LocalDate, Long> {
    val dailySleep = mutableMapOf<LocalDate, Long>()
    var pageCount = 0
    var page = retryBackoff {
        client.readRecords(
            ReadRecordsRequest(
                SleepSessionRecord::class,
                timeRangeFilter = TimeRangeFilter.between(start, end),
                ascendingOrder = false
            )
        )
    }
    while (true) {
        pageCount++
        if (page.records.isEmpty()) {
            Log.w("HealthRepo", "수면: 빈 페이지 (page=$pageCount) → 중단")
            break
        }
        page.records.forEach {
            val d = it.startTime.atZone(zone).toLocalDate()
            val minutes = Duration.between(it.startTime, it.endTime).toMinutes()
            dailySleep[d] = (dailySleep[d] ?: 0L) + minutes
        }
        val next = page.pageToken ?: break
        if (pageCount > 50) {
            Log.e("HealthRepo", "수면: 페이지 50 초과 → 강제 중단")
            break
        }
        delay(50)
        page = retryBackoff {
            client.readRecords(
                ReadRecordsRequest(
                    SleepSessionRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(start, end),
                    pageToken = next,
                    ascendingOrder = false
                )
            )
        }
    }
    return dailySleep
}
