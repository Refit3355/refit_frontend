package com.refit.app.data.auth.modelAndView

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

    var uiState by mutableStateOf(SignupUiState())
        private set

    var loading by mutableStateOf(false)
        private set
    var errorMsg by mutableStateOf<String?>(null)
        private set

    //  Step1 바인딩 (입력 시 중복검사 결과 초기화 포함)
    fun onEmail(v: String) {
        uiState = uiState.copy(
            email = v.trim(),
            emailChecked = false,
            emailAvailable = false,
            emailMsg = null
        )
    }
    fun onPassword(v: String)        { uiState = uiState.copy(password = v) }
    fun onPasswordConfirm(v: String) { uiState = uiState.copy(passwordConfirm = v) }
    fun onNick(v: String) {
        uiState = uiState.copy(
            nickname = v.trim(),
            nickChecked = false,
            nickAvailable = false,
            nickMsg = null
        )
    }
    fun onMemberName(v: String)      { uiState = uiState.copy(memberName = v) }
    fun onPhone(v: String)           { uiState = uiState.copy(phoneNumber = v.filter { it.isDigit() }.take(11)) }
    fun onBirthday(d: LocalDate?)    { uiState = uiState.copy(birthday = d) }
    fun onZipcode(v: String)         { uiState = uiState.copy(zipcode = v.take(5)) }
    fun onRoad(v: String)            { uiState = uiState.copy(roadAddress = v) }
    fun onDetail(v: String)          { uiState = uiState.copy(detailAddress = v) }

    // Step2 전용
    private val EXCLUSIVE = "해당없음"
    fun setSkinType(v: String?)      { uiState = uiState.copy(skinType = v) }

    private fun toggleWithExclusive(current: Set<String>, item: String): Set<String> =
        if (item == EXCLUSIVE) setOf(EXCLUSIVE)
        else {
            val base = current - EXCLUSIVE
            if (item in base) base - item else base + item
        }

    fun toggleSkinConcern(item: String)  { uiState = uiState.copy(skinConcerns = toggleWithExclusive(uiState.skinConcerns, item)) }
    fun toggleScalpConcern(item: String) { uiState = uiState.copy(scalpConcerns = toggleWithExclusive(uiState.scalpConcerns, item)) }
    fun toggleHealthConcern(item: String){ uiState = uiState.copy(healthConcerns = toggleWithExclusive(uiState.healthConcerns, item)) }

    // 유효성
    private val pwRegex =
        Regex("^(?=.{8,64}$)(?:(?=.*[A-Za-z])(?=.*\\d)|(?=.*[A-Za-z])(?=.*[^\\w\\s])|(?=.*\\d)(?=.*[^\\w\\s])).*$")

    val isPasswordRuleOk: Boolean
        get() = pwRegex.matches(uiState.password)

    val isPasswordConfirmMatch: Boolean
        get() = uiState.password.isNotEmpty() &&
                uiState.passwordConfirm.isNotEmpty() &&
                uiState.passwordConfirm == uiState.password

    val isPhoneStartsWith010: Boolean
        get() = uiState.phoneNumber.startsWith("010")

    val isPhoneFormatOk: Boolean
        get() = uiState.phoneNumber.matches(Regex("^010\\d{8}$"))
    private fun isEmailFormatValid(): Boolean {
        val r = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
        return uiState.email.isNotBlank() && r.matches(uiState.email)
    }
    private fun isNicknameValid(): Boolean = uiState.nickname.length in 2..20

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

    // “다음” 활성 조건: 기본 유효성 + 중복검사 통과
    val isStep1Ready: Boolean
        get() = isValid &&
                uiState.emailChecked && uiState.emailAvailable &&
                uiState.nickChecked && uiState.nickAvailable

    val isStep2Valid: Boolean
        get() = uiState.skinType != null

    // 서버 DTO 매핑
    private fun toSignupAllRequest(): SignupAllRequest {
        val bday = uiState.birthday?.format(DateTimeFormatter.ISO_DATE) // yyyy-MM-dd
        val signup = SignupRequest(
            email = uiState.email,
            nickName = uiState.nickname,
            memberName = uiState.memberName,
            password = uiState.password,
            zipcode = uiState.zipcode,
            roadAddress = uiState.roadAddress,
            detailAddress = uiState.detailAddress,
            birthday = bday ?: "",
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

    // 가입 호출
    fun submitSignup(
        onSuccess: (Long) -> Unit,
        onError: (String) -> Unit
    ) {
        if (!isValid) { onError("입력값을 확인해 주세요."); return }
        if (!isStep2Valid) { onError("피부 타입을 선택해 주세요."); return }

        val api = RetrofitInstance.create(AuthApi::class.java)
        val req = toSignupAllRequest()

        viewModelScope.launch {
            loading = true
            errorMsg = null
            try {
                val res = api.join(req)
                val id = requireNotNull(res.data?.id) { "가입 ID 없음" }
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

    // 중복검사
    fun checkEmailDuplicate() {
        val email = uiState.email
        if (!isEmailFormatValid()) {
            uiState = uiState.copy(
                emailChecked = false,
                emailAvailable = false,
                emailMsg = "이메일 형식이 올바르지 않아요. 예) example@domain.com"
            )
            return
        }

        viewModelScope.launch {
            try {
                val api = RetrofitInstance.create(AuthApi::class.java)
                val res = api.checkEmail(email) // UtilResponse<Boolean>
                val ok = res.status.equals("SUCCESS", true)
                val available = res.data == true

                val message = when {
                    !ok -> res.message.orEmpty().ifBlank { "서버 응답이 올바르지 않아요. 잠시 후 다시 시도해 주세요." }
                    available -> "사용 가능한 이메일입니다."
                    else -> "이미 사용 중인 이메일이에요."
                }

                uiState = uiState.copy(
                    emailChecked = ok,
                    emailAvailable = ok && available,
                    emailMsg = message
                )
            } catch (t: Throwable) {
                uiState = uiState.copy(
                    emailChecked = false,
                    emailAvailable = false,
                    emailMsg = "네트워크 오류로 확인에 실패했어요. 연결을 확인하고 다시 시도해 주세요."
                )
            } finally {
                uiState = uiState.copy(emailCheckLoading = false)
            }
        }
    }

    fun checkNickDuplicate() {
        val nick = uiState.nickname
        if (!isNicknameValid()) {
            uiState = uiState.copy(
                nickChecked = false,
                nickAvailable = false,
                nickMsg = "닉네임은 2~20자여야 해요."
            )
            return
        }

        viewModelScope.launch {

            try {
                val api = RetrofitInstance.create(AuthApi::class.java)
                val res = api.checkNickname(nick)
                val ok = res.status.equals("SUCCESS", true)
                val available = res.data == true

                val message = when {
                    !ok -> res.message.orEmpty().ifBlank { "서버 응답이 올바르지 않아요. 잠시 후 다시 시도해 주세요." }
                    available -> "사용 가능한 닉네임입니다."
                    else -> "이미 사용 중인 닉네임이에요."
                }

                uiState = uiState.copy(
                    nickChecked = ok,
                    nickAvailable = ok && available,
                    nickMsg = message
                )
            } catch (t: Throwable) {
                uiState = uiState.copy(
                    nickChecked = false,
                    nickAvailable = false,
                    nickMsg = "네트워크 오류로 확인에 실패했어요. 연결을 확인하고 다시 시도해 주세요."
                )
            } finally {
                uiState = uiState.copy(nickCheckLoading = false)
            }
        }
    }

    fun canProceed(mode: FormMode): Boolean = when (mode) {
        FormMode.SIGNUP -> isStep1Ready
        FormMode.EDIT   -> {
            // 수정 모드에선 중복검사 flag 강제하지 않고 기본/비번검증 완화 버전 사용
            uiState.nickname.isNotBlank() &&
                    uiState.memberName.isNotBlank() &&
                    uiState.phoneNumber.matches(Regex("^010\\d{8}$")) &&
                    uiState.birthday != null &&
                    uiState.zipcode.isNotBlank() &&
                    uiState.roadAddress.isNotBlank() &&
                    uiState.detailAddress.isNotBlank() &&
                    isPasswordRuleOkOrEmpty &&
                    isPasswordConfirmMatchOrEmpty
        }
    }


    private var originalNickname: String? = null
    val isPasswordRuleOkOrEmpty: Boolean
        get() = uiState.password.isBlank() || isPasswordRuleOk

    val isPasswordConfirmMatchOrEmpty: Boolean
        get() = uiState.password.isBlank() && uiState.passwordConfirm.isBlank() ||
                (uiState.password.isNotBlank() && uiState.passwordConfirm == uiState.password)

    fun prefillFromMe() {
        viewModelScope.launch {
            try {
                val api = RetrofitInstance.create(AuthApi::class.java)
                val res = api.getMyBasic()               // UtilResponse<BasicInfoResponse>
                val d = requireNotNull(res.data)

                originalNickname = d.nickname

                uiState = uiState.copy(
                    email = d.email,                     // 이메일은 readOnly로 표시
                    nickname = d.nickname,
                    memberName = d.memberName,
                    zipcode = d.zipcode.orEmpty(),
                    roadAddress = d.roadAddress.orEmpty(),
                    detailAddress = d.detailAddress.orEmpty(),
                    birthday = runCatching { java.time.LocalDate.parse(d.birthday) }.getOrNull(),
                    phoneNumber = d.phoneNumber.orEmpty(),
                    // 수정 진입 시 중복검사 초기화 상태
                    emailChecked = true, emailAvailable = true,
                    nickChecked = true,  nickAvailable  = true,
                    emailMsg = null, nickMsg = null
                )
            } catch (t: Throwable) {
                errorMsg = t.message ?: "내 정보 불러오기 실패"
            }
        }
    }
    fun checkNickDuplicateForEdit() {
        val nick = uiState.nickname
        if (originalNickname != null && originalNickname == nick) {
            uiState = uiState.copy(
                nickChecked = true,
                nickAvailable = true,
                nickMsg = "현재 사용 중인 닉네임입니다."
            )
            return
        }
        // 기존 검사 재사용
        checkNickDuplicate()
    }

    // ====== 추가: 내 정보 업데이트 ======
//    fun updateMyInfo(
//        onSaved: () -> Unit,
//        onError: (String) -> Unit
//    ) {
//        // 이메일은 서버에서 토큰 기준이므로 보내지 않음
//        val req = com.refit.app.data.auth.model.UpdateBasicRequest(
//            meail = uiState.email.takeIf { it.isNotBlank()}
//            nickname = uiState.nickname.takeIf { it.isNotBlank() },
//            memberName = uiState.memberName.takeIf { it.isNotBlank() },
//            password = uiState.password.takeIf { it.isNotBlank() }, // 비어있으면 null → 미변경
//            zipcode = uiState.zipcode.takeIf { it.isNotBlank() },
//            roadAddress = uiState.roadAddress.takeIf { it.isNotBlank() },
//            detailAddress = uiState.detailAddress.takeIf { it.isNotBlank() },
//            birthday = uiState.birthday?.format(java.time.format.DateTimeFormatter.ISO_DATE),
//            phoneNumber = uiState.phoneNumber.takeIf { it.isNotBlank() }
//        )
//
//        // 기본 유효성: 수정 모드에서는 비번/확인 빈값 허용
//        val basicOk =
//            uiState.nickname.isNotBlank() &&
//                    uiState.memberName.isNotBlank() &&
//                    uiState.phoneNumber.matches(Regex("^010\\d{8}$")) &&
//                    uiState.birthday != null &&
//                    uiState.zipcode.isNotBlank() &&
//                    uiState.roadAddress.isNotBlank() &&
//                    uiState.detailAddress.isNotBlank() &&
//                    isPasswordRuleOkOrEmpty &&
//                    isPasswordConfirmMatchOrEmpty
//
//        if (!basicOk) {
//            onError("입력값을 확인해 주세요.")
//            return
//        }
//
//        viewModelScope.launch {
//            try {
//                val api = RetrofitInstance.create(AuthApi::class.java)
//                val res = api.updateMyBasic(req)        // UtilResponse<Boolean>
//                val ok = res.status.equals("SUCCESS", true) && (res.data == true)
//                if (ok) onSaved() else onError(res.message.ifBlank { "수정에 실패했어요." })
//            } catch (t: Throwable) {
//                onError(t.message ?: "네트워크 오류")
//            }
//        }
//
//
//    }
}