package com.refit.app.network

import android.content.Context
import android.content.SharedPreferences

object TokenManager {
    private const val SECURE_PREFS_NAME = "refit_secure_prefs"
    private const val KEY_ACCESS = "access_token"
    private const val KEY_REFRESH = "refresh_token"
    private lateinit var securePrefs: SharedPreferences

    fun init(context: Context) {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        securePrefs = EncryptedSharedPreferences.create(
            context,
            SECURE_PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
    fun getToken(): String? {
        if (!::prefs.isInitialized) return null
        val token = prefs.getString(TOKEN_KEY, null)
        return if (token == "null") null else token
    }
    fun saveToken(token: String?) { if (::prefs.isInitialized) prefs.edit().putString(TOKEN_KEY, token).apply() }
    fun clearToken() { if (::prefs.isInitialized) prefs.edit().remove(TOKEN_KEY).apply() }
}