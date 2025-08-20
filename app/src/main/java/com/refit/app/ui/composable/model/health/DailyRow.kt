package com.refit.app.model.health

import java.time.LocalDate

/**
 * 하루 단위 건강 데이터 모델
 */
data class DailyRow(
    val date: LocalDate,          // 날짜
    val steps: Long? = null,      // 걸음 수
    val totalKcal: Double? = null,     // 총 소모 칼로리 (운동 + 기초대사 포함)
    val bloodGlucoseMgdl: Double? = null, // 혈당 (mg/dL)
    val systolicMmhg: Double? = null,     // 수축기 혈압
    val diastolicMmhg: Double? = null,    // 이완기 혈압
    val intakeKcal: Double? = null,       // 섭취 칼로리
    val sleepMinutes: Long? = null,       // 수면 시간 (분)
    val skinType: Int? = null             // 피부 타입 (옵션)
)
