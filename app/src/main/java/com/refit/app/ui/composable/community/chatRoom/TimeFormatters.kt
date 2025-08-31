package com.refit.app.ui.composable.community.chatRoom

import java.time.*
import java.time.format.DateTimeFormatter
import java.util.Locale

private val KST = ZoneId.of("Asia/Seoul")
private val TIME_FMT = DateTimeFormatter.ofPattern("a h:mm", Locale.KOREAN) // 예: 오전 10:25

/** createdAt이 OffsetDateTime인 경우 */
fun OffsetDateTime.koreanTime(): String =
    this.atZoneSameInstant(KST).toLocalTime().format(TIME_FMT)

/** createdAt이 LocalDateTime(String 오프셋 없음)인 경우 대비 */
fun LocalDateTime.koreanTime(): String =
    this.atZone(KST).toLocalTime().format(TIME_FMT)

/** createdAt이 String(예: 2025-08-25T08:44:40.911409)인 경우 대비 */
fun String.koreanTimeFromIsoLocal(): String = runCatching {
    LocalDateTime.parse(this, DateTimeFormatter.ISO_LOCAL_DATE_TIME).koreanTime()
}.getOrDefault("-")