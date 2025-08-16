package com.refit.app.health.model

import java.time.LocalDate

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
