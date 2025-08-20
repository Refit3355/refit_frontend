package com.refit.app.ui.viewmodel.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refit.app.data.auth.api.AuthApi
import com.refit.app.data.auth.model.ConcernRequest
import com.refit.app.data.auth.model.HairInfoDto
import com.refit.app.data.auth.model.HealthInfoDto
import com.refit.app.data.auth.model.SignupAllRequest
import com.refit.app.data.auth.model.SignupRequest
import com.refit.app.data.auth.model.SkinInfoDto
import com.refit.app.network.RetrofitInstance
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SignupViewModel : ViewModel() {

    // 화면 상태 (프로젝트 내 정의된 타입 사용)
    var uiState by mutableStateOf(SignupUiState())
        private set

    // 네트워크 진행/에러 상태
    var loading by mutableStateOf(false)
        private set
    var errorMsg by mutableStateOf<String?>(null)
        private set

    // ---- step1 바인딩 ----
    fun onEmail(v: String)            { uiState = uiState.copy(email = v.trim()) }
    fun onPassword(v: String)         { uiState = uiState.copy(password = v) }
    fun onPasswordConfirm(v: String)  { uiState = uiState.copy(passwordConfirm = v) }
    fun onNick(v: String)             { uiState = uiState.copy(nickname = v) }
    fun onMemberName(v: String)       { uiState = uiState.copy(memberName = v) }
    fun onPhone(v: String)            { uiState = uiState.copy(phoneNumber = v.filter { it.isDigit() }.take(11)) }
    fun onBirthday(d: LocalDate?)     { uiState = uiState.copy(birthday = d) }
    fun onZipcode(v: String)          { uiState = uiState.copy(zipcode = v.take(5)) } // 프로젝트 정책 유지
    fun onRoad(v: String)             { uiState = uiState.copy(roadAddress = v) }
    fun onDetail(v: String)           { uiState = uiState.copy(detailAddress = v) }

    // ---- step2 전용 핸들러들 ----
    private val EXCLUSIVE = "해당없음"

    fun setSkinType(v: String?) {
        uiState = uiState.copy(skinType = v)
    }

    private fun toggleWithExclusive(current: Set<String>, item: String): Set<String> {
        return if (item == EXCLUSIVE) {
            setOf(EXCLUSIVE)                     // '해당없음' 단독 선택
        } else {
            val base = current - EXCLUSIVE       // '해당없음'이 켜져있으면 해제
            if (item in base) base - item else base + item
        }
    }

    fun toggleSkinConcern(item: String) {
        uiState = uiState.copy(
            skinConcerns = toggleWithExclusive(uiState.skinConcerns, item)
        )
    }

    fun toggleScalpConcern(item: String) {
        uiState = uiState.copy(
            scalpConcerns = toggleWithExclusive(uiState.scalpConcerns, item)
        )
    }

    fun toggleHealthConcern(item: String) {
        uiState = uiState.copy(
            healthConcerns = toggleWithExclusive(uiState.healthConcerns, item)
        )
    }

    // ---- 유효성 ----
    private val pwRegex = Regex("^(?=.{8,64}$)(?:(?=.*[A-Za-z])(?=.*\\d)|(?=.*[A-Za-z])(?=.*[^\\w\\s])|(?=.*\\d)(?=.*[^\\w\\s])).*$")

    val isValid: Boolean
        get() = uiState.email.isNotBlank() &&
                uiState.nickname.isNotBlank() &&
                uiState.memberName.isNotBlank() &&
                uiState.password.matches(pwRegex) &&
                uiState.passwordConfirm == uiState.password &&
                uiState.phoneNumber.matches(Regex("^010\\d{8}$")) &&
                uiState.birthday != null &&
                uiState.zipcode.isNotBlank() &&
                uiState.roadAddress.isNotBlank() &&
                uiState.detailAddress.isNotBlank()

    val isStep2Valid: Boolean
        get() = uiState.skinType != null

    // ---- 서버 DTO 매핑 ----
    private fun toSignupAllRequest(): SignupAllRequest {
        val bday = uiState.birthday?.format(DateTimeFormatter.ISO_DATE) // yyyy-MM-dd

        val signup = SignupRequest(
            email = uiState.email,
            nickName = uiState.nickname,          // 서버 DTO 필드명: nickName (N 대문자)
            memberName = uiState.memberName,
            password = uiState.password,
            zipcode = uiState.zipcode,
            roadAddress = uiState.roadAddress,
            detailAddress = uiState.detailAddress,
            birthday = bday ?: "",                // @NotNull이므로 null이면 서버에서 400
            phoneNumber = uiState.phoneNumber
        )

        val health = HealthInfoDto(
            eyeHealth = if ("눈건강" in uiState.healthConcerns) 1 else 0,
            fatigue = if ("만성피로" in uiState.healthConcerns) 1 else 0,
            sleepStress = if ("수면/스트레스" in uiState.healthConcerns) 1 else 0,
            immuneCare = if ("면역력" in uiState.healthConcerns) 1 else 0,
            muscleHealth = if ("근력" in uiState.healthConcerns) 1 else 0,
            gutHealth = if ("장건강" in uiState.healthConcerns) 1 else 0,
            bloodCirculation = if ("혈액순환" in uiState.healthConcerns) 1 else 0
        )

        val hair = HairInfoDto(
            hairLoss = if ("탈모" in uiState.scalpConcerns) 1 else 0,
            damagedHair = if ("손상모" in uiState.scalpConcerns) 1 else 0,
            scalpTrouble = if ("두피트러블" in uiState.scalpConcerns) 1 else 0,
            dandruff = if ("비듬/각질" in uiState.scalpConcerns) 1 else 0
        )

        val skin = SkinInfoDto(
            atopic = if ("아토피" in uiState.skinConcerns) 1 else 0,
            acne = if ("여드름/민감성" in uiState.skinConcerns) 1 else 0,
            whitening = if ("미백/잡티" in uiState.skinConcerns) 1 else 0,
            sebum = if ("피지/블랙헤드" in uiState.skinConcerns) 1 else 0,
            innerDryness = if ("속건조" in uiState.skinConcerns) 1 else 0,
            wrinkles = if ("주름/탄력" in uiState.skinConcerns) 1 else 0,
            enlargedPores = if ("모공" in uiState.skinConcerns) 1 else 0,
            redness = if ("홍조" in uiState.skinConcerns) 1 else 0,
            keratin = if ("각질" in uiState.skinConcerns) 1 else 0
        )

        return SignupAllRequest(
            signup = signup,
            concerns = ConcernRequest(health = health, hair = hair, skin = skin)
        )
    }

    // ---- 가입 호출 (Step2의 "가입하기") ----
    fun submitSignup(
        onSuccess: (Long) -> Unit,
        onError: (String) -> Unit
    ) {
        if (!isValid) {
            onError("입력값을 확인해 주세요.")
            return
        }
        if (!isStep2Valid) {
            onError("피부 타입을 선택해 주세요.")
            return
        }

        val api = RetrofitInstance.create(AuthApi::class.java)
        val req = toSignupAllRequest()

        viewModelScope.launch {
            loading = true
            errorMsg = null
            try {
                val res = api.join(req) // UtilResponse<SignupResponse>
                val id = res.data?.id ?: error("가입 ID 없음")
                onSuccess(id)
            } catch (t: Throwable) {
                val msg = t.message ?: "네트워크 오류"
                errorMsg = msg
                onError(msg)
            } finally {
                loading = false
            }
        }
    }
}