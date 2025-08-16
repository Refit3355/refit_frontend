package com.refit.app.util.health

import kotlinx.coroutines.delay

/**
 * Health Connect API 호출 시 rate limit 에 걸릴 수 있으므로
 * 지수 백오프를 적용하여 재시도
 */
suspend fun <T> retryBackoff(
    maxRetries: Int = 3,           // 최대 재시도 횟수
    baseDelayMs: Long = 400L,      // 최초 대기 시간
    factor: Double = 2.0,          // 지수 배수
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
