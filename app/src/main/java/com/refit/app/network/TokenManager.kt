package com.refit.app.network

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

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

    @Synchronized fun getAccessToken(): String? =
        securePrefs.getString(KEY_ACCESS, null).takeUnless { it.isNullOrBlank() || it == "null" }

    @Synchronized fun getRefreshToken(): String? =
        securePrefs.getString(KEY_REFRESH, null).takeUnless { it.isNullOrBlank() || it == "null" }

    @Synchronized fun saveTokens(access: String?, refresh: String?) {
        // refresh 가 null이면 기존 refresh 유지 (서버가 미회전 시)
        val currentRefresh = getRefreshToken()
        securePrefs.edit()
            .putString(KEY_ACCESS, access)
            .putString(KEY_REFRESH, refresh ?: currentRefresh)
            .apply()
    }

    @Synchronized fun saveAccess(access: String?) {
        securePrefs.edit().putString(KEY_ACCESS, access).apply()
    }

    @Synchronized fun saveRefresh(refresh: String?) {
        securePrefs.edit().putString(KEY_REFRESH, refresh).apply()
    }

    @Synchronized fun clearAll() {
        securePrefs.edit().remove(KEY_ACCESS).remove(KEY_REFRESH).apply()
    }

    // 역호환: 기존 코드 사용 시
    fun getToken(): String? = getAccessToken()
    fun saveToken(token: String?) = saveAccess(token)
    fun clearToken() = saveAccess(null)
}