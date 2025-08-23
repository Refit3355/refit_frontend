package com.refit.app.network

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.refit.app.data.auth.model.HealthInfoDto

object UserPrefs {
    private const val PREFS_NAME = "refit_user_prefs"
    private const val KEY_MEMBER_ID = "member_id"
    private const val KEY_NICKNAME  = "nickname"
    private const val KEY_HEALTH    = "health_json"

    private lateinit var prefs: SharedPreferences
    private val gson by lazy { Gson() }

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveUser(memberId: Long?, nickname: String?, health: HealthInfoDto?) {
        prefs.edit().apply {
            if (memberId != null) putLong(KEY_MEMBER_ID, memberId) else remove(KEY_MEMBER_ID)
            putString(KEY_NICKNAME, nickname ?: "")
            putString(KEY_HEALTH, health?.let { gson.toJson(it) } ?: "")
        }.apply()
    }

    fun getMemberId(): Long? {
        if (!::prefs.isInitialized) return null
        val v = prefs.getLong(KEY_MEMBER_ID, -1L)
        return if (v > 0) v else null
    }
    fun getNickname(): String? =
        if (!::prefs.isInitialized) null
        else prefs.getString(KEY_NICKNAME, null)?.takeIf { it.isNotBlank() }

    fun getHealth(): HealthInfoDto? =
        if (!::prefs.isInitialized) null
        else prefs.getString(KEY_HEALTH, null)
            ?.takeIf { it.isNotBlank() }
            ?.let { gson.fromJson(it, HealthInfoDto::class.java) }

    fun clear() {
        if (!::prefs.isInitialized) return
        prefs.edit().clear().apply()
    }

    fun setNickname(nickname: String?) {
        if (!::prefs.isInitialized) return
        prefs.edit().putString(KEY_NICKNAME, nickname ?: "").apply()
    }

}