package com.refit.app.data.health

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.*
import com.refit.app.data.health.model.DailyRow
import com.refit.app.util.health.Cache
import com.refit.app.util.health.HealthCache
import com.refit.app.data.health.reader.*
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * Health Connect 데이터 접근에 대한 퍼사드
 * 개별 reader 들을 조합하여 하루 단위 데이터 반환
 */
object HealthRepo {
    fun client(ctx: Context) = HealthConnectClient.getOrCreate(ctx)

    // 필요한 권한 정의
    val readPerms = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(SleepSessionRecord::class),
        HealthPermission.getReadPermission(BloodGlucoseRecord::class),
        HealthPermission.getReadPermission(BloodPressureRecord::class),
        HealthPermission.getReadPermission(NutritionRecord::class)
    )

    /**
     * 지정한 일수만큼의 건강 데이터를 하루 단위로 읽어옴
     */
    suspend fun readDailyAll(ctx: Context, days: Long = 30): List<DailyRow> {
        require(days >= 1) { "days must be >= 1" }

        // 캐시 사용
        HealthCache.cache?.let {
            if (it.days == days && (System.currentTimeMillis() - it.at) <= HealthCache.CACHE_TTL_MS) {
                return it.rows
            }
        }

        val c = client(ctx)
        val zone = ZoneId.systemDefault()

        // 날짜 범위 계산
        val endDate = LocalDate.now(zone)
        val startDate = endDate.minusDays(days)
        val dates = generateSequence(startDate) { it.plusDays(1) }
            .take(days.toInt())
            .toList()

        val startLdt = startDate.atStartOfDay()
        val endLdt = endDate.plusDays(1).atStartOfDay()
        val startI: Instant = startDate.atStartOfDay(zone).toInstant()
        val endI: Instant = endDate.plusDays(1).atStartOfDay(zone).toInstant()

        // 개별 리더 호출
        val (dailySteps, dailyTotalKcal) = readStepsAndTotalCalories(c, startLdt, endLdt)
        val dailyGlucose = readGlucose(c, startI, endI, zone)
        val (dailySys, dailyDia) = readPressure(c, startI, endI, zone)
        val dailyIntake = readNutrition(c, startI, endI, zone)
        val dailySleep = readSleep(c, startI, endI, zone)

        // 날짜 합치기
        val allDates = (dates.toSet()
                + dailySteps.keys + dailyTotalKcal.keys + dailyGlucose.keys
                + dailySys.keys + dailyIntake.keys + dailySleep.keys)
            .toSortedSet()

        val rows = allDates.map { d ->
            DailyRow(
                date = d,
                steps = dailySteps[d],
                totalKcal = dailyTotalKcal[d],
                bloodGlucoseMgdl = dailyGlucose[d],
                systolicMmhg = dailySys[d],
                diastolicMmhg = dailyDia[d],
                intakeKcal = dailyIntake[d],
                sleepMinutes = dailySleep[d]
            )
        }

        HealthCache.cache = Cache(System.currentTimeMillis(), days, rows)
        return rows
    }

}
