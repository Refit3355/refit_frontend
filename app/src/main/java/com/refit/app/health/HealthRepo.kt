package com.refit.app.health

import android.content.Context
import android.util.Log
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.*
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId

data class DailyRow(
    val date: LocalDate,
    val steps: Long? = null,
    val activeKcal: Double? = null,
    val bloodGlucoseMgdl: Double? = null,  // mg/dL
    val systolicMmhg: Double? = null,
    val diastolicMmhg: Double? = null,
    val intakeKcal: Double? = null,
    val sleepMinutes: Long? = null,
    val skinType: Int? = null
)

object HealthRepo {
    fun client(ctx: Context) = HealthConnectClient.getOrCreate(ctx)

    val readPerms = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),                 // 걸음
        HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),  // 활동칼로리
        HealthPermission.getReadPermission(SleepSessionRecord::class),          // 수면
        HealthPermission.getReadPermission(BloodGlucoseRecord::class),          // 혈당
        HealthPermission.getReadPermission(BloodPressureRecord::class),         // 혈압
        HealthPermission.getReadPermission(NutritionRecord::class)              // 영양
    )

    // ====== 간단 캐시: 같은 days로 30초 이내 재요청이면 재사용 ======
    private data class Cache(val at: Long, val days: Long, val rows: List<DailyRow>)
    @Volatile private var cache: Cache? = null
    private const val CACHE_TTL_MS = 30_000L

    // ====== rate limit 친화: 지수 백오프 재시도 헬퍼 ======
    private suspend fun <T> retryBackoff(
        maxRetries: Int = 3,
        baseDelayMs: Long = 400L,
        factor: Double = 2.0,
        block: suspend () -> T
    ): T {
        var attempt = 0
        var delayMs = baseDelayMs
        var lastErr: Throwable? = null
        while (attempt <= maxRetries) {
            try {
                return block()
            } catch (e: Throwable) {
                val msg = (e.message ?: "").lowercase()
                val rateLimited =
                    "rate" in msg && "limit" in msg || "quota" in msg || "request rejected" in msg
                if (!rateLimited || attempt == maxRetries) {
                    lastErr = e
                    break
                }
                delay(delayMs)
                delayMs = (delayMs * factor).toLong()
                attempt++
            }
        }
        throw lastErr ?: RuntimeException("Unknown error")
    }

    suspend fun readDailyAll(ctx: Context, days: Long = 30): List<DailyRow> {
        require(days >= 1) { "days must be >= 1" }

        // 캐시 히트 시 반환
        cache?.let {
            if (it.days == days && (System.currentTimeMillis() - it.at) <= CACHE_TTL_MS) {
                return it.rows
            }
        }

        val c = client(ctx)
        val zone = ZoneId.systemDefault()

        // 달력 N일(오늘 포함)
        val endDate = LocalDate.now(zone)
        val startDate = endDate.minusDays(days - 1)
        val dates = generateSequence(startDate) { it.plusDays(1) }
            .take(days.toInt())
            .toList()

        // LocalDateTime 범위(그룹 집계용)
        val startLdt = startDate.atStartOfDay()
        val endLdt = endDate.plusDays(1).atStartOfDay() // 배타

        // Instant 범위(readRecords용)
        val startI: Instant = startDate.atStartOfDay(zone).toInstant()
        val endI: Instant = endDate.plusDays(1).atStartOfDay(zone).toInstant()

        // =================== 걸음/활동칼로리 ===================
        val dailySteps = mutableMapOf<LocalDate, Long>()
        val dailyActive = mutableMapOf<LocalDate, Double>()
        val grouped = retryBackoff {
            c.aggregateGroupByPeriod(
                AggregateGroupByPeriodRequest(
                    metrics = setOf(
                        StepsRecord.COUNT_TOTAL,
                        ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL
                    ),
                    timeRangeFilter = TimeRangeFilter.between(startLdt, endLdt),
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

        // =================== 혈당 ===================
        val dailyGlucose = mutableMapOf<LocalDate, Double?>()
        run {
            var pageCount = 0
            var page = retryBackoff {
                c.readRecords(
                    ReadRecordsRequest(
                        BloodGlucoseRecord::class,
                        timeRangeFilter = TimeRangeFilter.between(startI, endI),
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
                    c.readRecords(
                        ReadRecordsRequest(
                            BloodGlucoseRecord::class,
                            timeRangeFilter = TimeRangeFilter.between(startI, endI),
                            pageToken = next,
                            ascendingOrder = false
                        )
                    )
                }
            }
        }

        delay(100)

        // =================== 혈압 ===================
        val dailySys = mutableMapOf<LocalDate, Double>()
        val dailyDia = mutableMapOf<LocalDate, Double>()
        run {
            var pageCount = 0
            var page = retryBackoff {
                c.readRecords(
                    ReadRecordsRequest(
                        BloodPressureRecord::class,
                        timeRangeFilter = TimeRangeFilter.between(startI, endI),
                        ascendingOrder = false
                    )
                )
            }
            while (true) {
                pageCount++
                if (page.records.isEmpty()) {
                    Log.w("HealthRepo", "혈압: 빈 페이지 (page=$pageCount) → 중단")
                    break
                }
                page.records.forEach {
                    val d = it.time.atZone(zone).toLocalDate()
                    dailySys.putIfAbsent(d, it.systolic.inMillimetersOfMercury)
                    dailyDia.putIfAbsent(d, it.diastolic.inMillimetersOfMercury)
                }
                val next = page.pageToken ?: break
                if (pageCount > 50) {
                    Log.e("HealthRepo", "혈압: 페이지 50 초과 → 강제 중단")
                    break
                }
                delay(50)
                page = retryBackoff {
                    c.readRecords(
                        ReadRecordsRequest(
                            BloodPressureRecord::class,
                            timeRangeFilter = TimeRangeFilter.between(startI, endI),
                            pageToken = next,
                            ascendingOrder = false
                        )
                    )
                }
            }
        }

        delay(100)

        // =================== 영양 ===================
        val dailyIntake = mutableMapOf<LocalDate, Double>()
        run {
            var pageCount = 0
            var page = retryBackoff {
                c.readRecords(
                    ReadRecordsRequest(
                        NutritionRecord::class,
                        timeRangeFilter = TimeRangeFilter.between(startI, endI),
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
                    c.readRecords(
                        ReadRecordsRequest(
                            NutritionRecord::class,
                            timeRangeFilter = TimeRangeFilter.between(startI, endI),
                            pageToken = next,
                            ascendingOrder = false
                        )
                    )
                }
            }
        }

        delay(100)

        // =================== 수면 ===================
        val dailySleep = mutableMapOf<LocalDate, Long>()
        run {
            var pageCount = 0
            var page = retryBackoff {
                c.readRecords(
                    ReadRecordsRequest(
                        SleepSessionRecord::class,
                        timeRangeFilter = TimeRangeFilter.between(startI, endI),
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
                    c.readRecords(
                        ReadRecordsRequest(
                            SleepSessionRecord::class,
                            timeRangeFilter = TimeRangeFilter.between(startI, endI),
                            pageToken = next,
                            ascendingOrder = false
                        )
                    )
                }
            }
        }

        // =================== 최종 병합 ===================
        val allDates = (dates.toSet()
                + dailySteps.keys + dailyActive.keys + dailyGlucose.keys
                + dailySys.keys + dailyIntake.keys + dailySleep.keys)
            .toSortedSet()

        val rows = allDates.map { d ->
            DailyRow(
                date = d,
                steps = dailySteps[d],
                activeKcal = dailyActive[d],
                bloodGlucoseMgdl = dailyGlucose[d],
                systolicMmhg = dailySys[d],
                diastolicMmhg = dailyDia[d],
                intakeKcal = dailyIntake[d],
                sleepMinutes = dailySleep[d]
            )
        }

        // 캐시 저장
        cache = Cache(System.currentTimeMillis(), days, rows)
        return rows
    }
}
