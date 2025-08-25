package com.refit.app.network

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.refit.app.data.auth.model.HairInfoDto
import com.refit.app.data.auth.model.HealthInfoDto
import com.refit.app.data.auth.model.SkinInfoDto

object UserPrefs {
    private const val PREFS_NAME = "refit_user_prefs"
    private const val KEY_MEMBER_ID = "member_id"
    private const val KEY_NICKNAME  = "nickname"
    private const val KEY_HEALTH    = "health_json"
    private const val KEY_HAIR      = "hair_json"
    private const val KEY_SKIN      = "skin_json"
    private const val KEY_PROFILE_URL = "profile_url"
    private const val DEFAULT_PROFILE_URL =
        "https://refit-s3.s3.ap-northeast-2.amazonaws.com/default_profile/default.png"

    private lateinit var prefs: SharedPreferences
    private val gson by lazy { Gson() }

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveUser(
        memberId: Long?,
        nickname: String?,
        health: HealthInfoDto?,
        hair: HairInfoDto?,
        skin: SkinInfoDto?
    ) {
        prefs.edit().apply {
            if (memberId != null) putLong(KEY_MEMBER_ID, memberId) else remove(KEY_MEMBER_ID)
            putString(KEY_NICKNAME, nickname ?: "")
            putString(KEY_HEALTH, health?.let { gson.toJson(it) } ?: "")
            putString(KEY_HAIR,   hair?.let { gson.toJson(it) }   ?: "")
            putString(KEY_SKIN,   skin?.let { gson.toJson(it) }   ?: "")
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

    fun getHair(): HairInfoDto? =
        if (!::prefs.isInitialized) null
        else prefs.getString(KEY_HAIR, null)
            ?.takeIf { it.isNotBlank() }
            ?.let { gson.fromJson(it, HairInfoDto::class.java) }

    fun getSkin(): SkinInfoDto? =
        if (!::prefs.isInitialized) null
        else prefs.getString(KEY_SKIN, null)
            ?.takeIf { it.isNotBlank() }
            ?.let { gson.fromJson(it, SkinInfoDto::class.java) }

    fun clear() {
        if (!::prefs.isInitialized) return
        prefs.edit().clear().apply()
    }

    fun setNickname(nickname: String?) {
        if (!::prefs.isInitialized) return
        prefs.edit().putString(KEY_NICKNAME, nickname ?: "").apply()
    }

    fun setHealth(health: HealthInfoDto?) {
        prefs.edit().putString(KEY_HEALTH, health?.let { gson.toJson(it) } ?: "").apply()
    }

    fun setHair(hair: HairInfoDto?) {
        prefs.edit().putString(KEY_HAIR, hair?.let { gson.toJson(it) } ?: "").apply()
    }

    fun setSkin(skin: SkinInfoDto?) {
        prefs.edit().putString(KEY_SKIN, skin?.let { gson.toJson(it) } ?: "").apply()
    }

    fun setProfileUrl(url: String?) {
        if (!::prefs.isInitialized) return
        prefs.edit().putString(KEY_PROFILE_URL, url ?: DEFAULT_PROFILE_URL).apply()
    }

    fun getProfileUrl(): String =
        if (!::prefs.isInitialized) DEFAULT_PROFILE_URL
        else prefs.getString(KEY_PROFILE_URL, null)
            ?.takeIf { it.isNotBlank() }
            ?: DEFAULT_PROFILE_URL

}