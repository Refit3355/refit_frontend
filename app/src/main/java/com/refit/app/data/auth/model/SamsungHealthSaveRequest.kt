package com.refit.app.data.auth.model

data class SamsungHealthSaveRequest(
    val steps: Long?,
    val totalKcal: Double?,
    val bloodGlucoseMgdl: Double?,
    val systolicMmhg: Double?,
    val diastolicMmhg: Double?,
    val intakeKcal: Double?,
    val sleepMinutes: Long?
)
