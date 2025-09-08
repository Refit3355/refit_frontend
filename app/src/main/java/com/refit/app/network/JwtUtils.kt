package com.refit.app.network

import android.util.Base64
import org.json.JSONObject

object JwtUtils {
    fun isAccessUsable(token: String?, expectedIssuer: String? = null, leewaySec: Long = 30): Boolean {
        if (token.isNullOrBlank()) return false
        return try {
            val parts = token.split(".")
            if (parts.size < 2) return false
            val payloadJson = String(
                Base64.decode(
                    parts[1],
                    Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP
                )
            )
            val obj = JSONObject(payloadJson)

            if (!expectedIssuer.isNullOrBlank() && obj.optString("iss", null) != expectedIssuer) return false
            if (obj.optString("typ", null) != "access") return false

            val exp = obj.optLong("exp", 0L)
            if (exp == 0L) return false

            val now = System.currentTimeMillis() / 1000
            (now + leewaySec) < exp
        } catch (_: Exception) {
            false
        }
    }
}