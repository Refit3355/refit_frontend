package com.refit.app.data.auth.modelAndView

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refit.app.data.auth.api.AuthApi
import com.refit.app.data.auth.model.*
import com.refit.app.network.RetrofitInstance
import com.refit.app.network.UserPrefs
import kotlinx.coroutines.launch

class HealthEditViewModel : ViewModel() {

    var skinType by mutableStateOf<String?>(null)
        private set
    var skinConcerns by mutableStateOf(setOf<String>())
        private set
    var scalpConcerns by mutableStateOf(setOf<String>())
        private set
    var healthConcerns by mutableStateOf(setOf<String>())
        private set

    var loading by mutableStateOf(false)
        private set
    var errorMsg by mutableStateOf<String?>(null)
        private set

    fun updateSkinType(v: String?) { skinType = v }

    private val NONE = "해당없음"

    /** 공통: ‘해당없음’ 독점 처리 */
    private fun toggleWithExclusive(current: Set<String>, item: String): Set<String> {
        return if (item == NONE) setOf(NONE)
        else {
            val base = current - NONE
            if (item in base) base - item else base + item
        }
    }

    /** 공통: 비어있으면 ‘해당없음’으로 대체 */
    private fun normalizeEmptyToNone(set: Set<String>): Set<String> =
        if (set.isEmpty()) setOf(NONE) else set

    fun toggleSkin(item: String)  {
        skinConcerns  = normalizeEmptyToNone(toggleWithExclusive(skinConcerns, item))
    }
    fun toggleScalp(item: String) {
        scalpConcerns = normalizeEmptyToNone(toggleWithExclusive(scalpConcerns, item))
    }
    fun toggleHealth(item: String){
        healthConcerns = normalizeEmptyToNone(toggleWithExclusive(healthConcerns, item))
    }

    private fun skinTypeName(code: Int?): String? = when (code) {
        0 -> "건성"; 1 -> "중성"; 2 -> "지성"; 3 -> "복합성"; 4 -> "수부지"; else -> null
    }
    private fun skinTypeCode(name: String?): Int = when (name) {
        "건성" -> 0; "중성" -> 1; "지성" -> 2; "복합성" -> 3; "수부지" -> 4; else -> 5
    }

    /** 로컬 캐시에서 프리필: 비어있으면 ‘해당없음’으로 세팅 */
    fun prefillFromPrefs() {
        // health
        UserPrefs.getHealth()?.let { h ->
            val set = buildSet {
                if (h.eyeHealth == 1) add("눈건강")
                if (h.fatigue == 1) add("만성피로")
                if (h.sleepStress == 1) add("수면/스트레스")
                if (h.immuneCare == 1) add("면역력")
                if (h.muscleHealth == 1) add("근력")
                if (h.gutHealth == 1) add("장건강")
                if (h.bloodCirculation == 1) add("혈액순환")
            }
            healthConcerns = normalizeEmptyToNone(set)
        } ?: run {
            healthConcerns = setOf(NONE)
        }

        // hair
        UserPrefs.getHair()?.let { hr ->
            val set = buildSet {
                if (hr.hairLoss == 1) add("탈모")
                if (hr.damagedHair == 1) add("손상모")
                if (hr.scalpTrouble == 1) add("두피트러블")
                if (hr.dandruff == 1) add("비듬/각질")
            }
            scalpConcerns = normalizeEmptyToNone(set)
        } ?: run {
            scalpConcerns = setOf(NONE)
        }

        // skin (+ skinType)
        UserPrefs.getSkin()?.let { sk ->
            val set = buildSet {
                if (sk.atopic == 1) add("아토피")
                if (sk.acne == 1) add("여드름/민감성")
                if (sk.whitening == 1) add("미백/잡티")
                if (sk.sebum == 1) add("피지/블랙헤드")
                if (sk.innerDryness == 1) add("속건조")
                if (sk.wrinkles == 1) add("주름/탄력")
                if (sk.enlargedPores == 1) add("모공")
                if (sk.redness == 1) add("홍조")
                if (sk.keratin == 1) add("각질")
            }
            skinConcerns = normalizeEmptyToNone(set)
            skinType = skinTypeName(sk.skinType)
        } ?: run {
            skinConcerns = setOf(NONE)
            skinType = null
        }
    }

    private fun toHealthDto(): HealthInfoDto = HealthInfoDto(
        eyeHealth        = if ("눈건강" in healthConcerns) 1 else 0,
        fatigue          = if ("만성피로" in healthConcerns) 1 else 0,
        sleepStress      = if ("수면/스트레스" in healthConcerns) 1 else 0,
        immuneCare       = if ("면역력" in healthConcerns) 1 else 0,
        muscleHealth     = if ("근력" in healthConcerns) 1 else 0,
        gutHealth        = if ("장건강" in healthConcerns) 1 else 0,
        bloodCirculation = if ("혈액순환" in healthConcerns) 1 else 0
    )

    private fun toSkinDto(): SkinInfoDto = SkinInfoDto(
        atopic        = if ("아토피" in skinConcerns) 1 else 0,
        acne          = if ("여드름/민감성" in skinConcerns) 1 else 0,
        whitening     = if ("미백/잡티" in skinConcerns) 1 else 0,
        sebum         = if ("피지/블랙헤드" in skinConcerns) 1 else 0,
        innerDryness  = if ("속건조" in skinConcerns) 1 else 0,
        wrinkles      = if ("주름/탄력" in skinConcerns) 1 else 0,
        enlargedPores = if ("모공" in skinConcerns) 1 else 0,
        redness       = if ("홍조" in skinConcerns) 1 else 0,
        keratin       = if ("각질" in skinConcerns) 1 else 0,
        skinType      = skinTypeCode(skinType)
    )

    private fun toHairDto(): HairInfoDto = HairInfoDto(
        hairLoss = if ("탈모" in scalpConcerns) 1 else 0,
        damagedHair = if ("손상모" in scalpConcerns) 1 else 0,
        scalpTrouble = if ("두피트러블" in scalpConcerns) 1 else 0,
        dandruff = if ("비듬/각질" in scalpConcerns) 1 else 0
    )

    /** 저장: 서버 반영 + 로컬 캐시 갱신 */
    fun save(onSaved: () -> Unit, onError: (String) -> Unit) {
        val api = RetrofitInstance.create(AuthApi::class.java)
        val req = ConcernSummaryDto(
            health = toHealthDto(),
            hair   = toHairDto(),
            skin   = toSkinDto()
        )

        viewModelScope.launch {
            loading = true
            try {
                val res = api.updateMyConcerns(req)
                if (!res.status.equals("SUCCESS", true))
                    throw IllegalStateException(res.message ?: "수정 실패")

                // 로컬 캐시 반영
                UserPrefs.setHealth(req.health)
                UserPrefs.setHair(req.hair)
                UserPrefs.setSkin(req.skin)

                onSaved()
            } catch (t: Throwable) {
                onError(t.message ?: "건강 정보 수정에 실패했어요.")
            } finally {
                loading = false
            }
        }
    }
}