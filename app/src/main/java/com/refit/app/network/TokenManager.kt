package com.refit.app.network

import android.content.Context
import android.content.SharedPreferences

object TokenManager {
    private const val PREFS_NAME = "refit_prefs"
    private const val TOKEN_KEY = "auth_token"
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    fun getToken(): String? {
        if (!::prefs.isInitialized) return null
        val token = prefs.getString(TOKEN_KEY, null)
        return if (token == "null") null else token
    }
    fun saveToken(token: String?) { if (::prefs.isInitialized) prefs.edit().putString(TOKEN_KEY, token).apply() }
    fun clearToken() { if (::prefs.isInitialized) prefs.edit().remove(TOKEN_KEY).apply() }
    fun parseNicknameFromJwt(token: String): String? {
        return try {
            val payload = token.split(".").getOrNull(1) ?: return null
            val json = String(android.util.Base64.decode(payload, android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP))
            org.json.JSONObject(json).optString("nickname", null)
        } catch (e: Exception) { null }
    }

}