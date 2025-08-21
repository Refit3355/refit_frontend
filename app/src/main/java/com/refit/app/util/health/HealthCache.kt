package com.refit.app.util.health

import com.refit.app.data.health.model.DailyRow

/**
 * - 동일한 days 파라미터로 요청 시 30초 이내에는 캐시 사용
 */
data class Cache(val at: Long, val days: Long, val rows: List<DailyRow>)

object HealthCache {
    @Volatile var cache: Cache? = null
    const val CACHE_TTL_MS = 30_000L
}
