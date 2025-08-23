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
import com.refit.app.data.auth.model.UpdateBasicRequest
import com.refit.app.network.RetrofitInstance
import com.refit.app.network.UserPrefs
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

    // 원본 스냅샷(수정 비교용)
    private data class Original(
        val email: String = "",
        val nickname: String = "",
        val memberName: String = "",
        val phoneNumber: String = "",
        val birthday: String? = null,        // yyyy-MM-dd
        val zipcode: String = "",
        val roadAddress: String = "",
        val detailAddress: String = ""
    )
    private var original by mutableStateOf(Original())
    private var originalNickname: String? = null

    // ---------------------------
    // 바인딩
    // ---------------------------
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

    // ---------------------------
    // 검증
    // ---------------------------
    private val pwRegex =
        Regex("^(?=.{8,64}$)(?:(?=.*[A-Za-z])(?=.*\\d)|(?=.*[A-Za-z])(?=.*[^\\w\\s])|(?=.*\\d)(?=.*[^\\w\\s])).*$")

    val isPasswordRuleOk: Boolean
        get() = pwRegex.matches(uiState.password)

    val isPasswordConfirmMatch: Boolean
        get() = uiState.password.isNotEmpty() &&
                uiState.passwordConfirm.isNotEmpty() &&
                uiState.passwordConfirm == uiState.password

    val isPasswordRuleOkOrEmpty: Boolean
        get() = uiState.password.isBlank() || isPasswordRuleOk

    val isPasswordConfirmMatchOrEmpty: Boolean
        get() = uiState.password.isBlank() && uiState.passwordConfirm.isBlank() ||
                (uiState.password.isNotBlank() && uiState.passwordConfirm == uiState.password)

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
                isPhoneFormatOk &&
                uiState.birthday != null &&
                uiState.zipcode.isNotBlank() &&
                uiState.roadAddress.isNotBlank() &&
                uiState.detailAddress.isNotBlank()

    // “다음” 활성(SIGNUP): 기본 유효성 + 중복검사 둘 다 통과
    val isStep1Ready: Boolean
        get() = isValid &&
                uiState.emailChecked && uiState.emailAvailable &&
                uiState.nickChecked && uiState.nickAvailable

    val isStep2Valid: Boolean get() = uiState.skinType != null

    // EDIT용 추가 검증
    private fun nicknameChanged(): Boolean = uiState.nickname != original.nickname
    private fun isValidForEdit(): Boolean =
        uiState.nickname.isNotBlank() &&
                uiState.memberName.isNotBlank() &&
                isPhoneFormatOk &&
                uiState.birthday != null &&
                uiState.zipcode.isNotBlank() &&
                uiState.roadAddress.isNotBlank() &&
                uiState.detailAddress.isNotBlank() &&
                isPasswordRuleOkOrEmpty &&
                isPasswordConfirmMatchOrEmpty

    fun canProceed(mode: FormMode): Boolean = when (mode) {
        FormMode.SIGNUP -> isStep1Ready
        FormMode.EDIT -> {
            // 닉네임을 바꿨다면 중복검사 통과해야 활성화
            val nickOk = if (nicknameChanged()) (uiState.nickChecked && uiState.nickAvailable) else true
            isValidForEdit() && nickOk
        }
    }

    // ---------------------------
    // 회원가입 DTO 변환 & 호출
    // ---------------------------
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

    // ---------------------------
    // 중복검사
    // ---------------------------
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
                val res = api.checkEmail(email)
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

    // (EDIT 전용) 닉네임 중복검사: 기존 닉이면 자동 통과, 변경 시 서버 검사
    fun checkNickDuplicateForEdit() {
        val nick = uiState.nickname.trim()
        if (!isNicknameValid()) {
            uiState = uiState.copy(
                nickChecked = false,
                nickAvailable = false,
                nickMsg = "닉네임은 2~20자여야 해요."
            )
            return
        }
        if (!nicknameChanged()) {
            uiState = uiState.copy(
                nickChecked = true,
                nickAvailable = true,
                nickMsg = "현재 사용 중인 닉네임입니다."
            )
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(nickCheckLoading = true)
            try {
                val api = RetrofitInstance.create(AuthApi::class.java)
                val res = api.checkNickname(nick)
                val ok  = res.status.equals("SUCCESS", true) && res.data == true
                uiState = uiState.copy(
                    nickChecked = true,
                    nickAvailable = ok,
                    nickMsg = if (ok) "사용 가능한 닉네임입니다." else "이미 사용 중인 닉네임이에요."
                )
            } catch (_: Throwable) {
                uiState = uiState.copy(
                    nickChecked = false,
                    nickAvailable = false,
                    nickMsg = "중복검사 실패: 네트워크 확인"
                )
            } finally {
                uiState = uiState.copy(nickCheckLoading = false)
            }
        }
    }

    // ---------------------------
    // 프리필 (내 정보 조회)
    // ---------------------------
    fun prefillFromMe() {
        viewModelScope.launch {
            try {
                val api = RetrofitInstance.create(AuthApi::class.java)
                val res = api.getMyBasic()
                val d = requireNotNull(res.data)

                // 원본 스냅샷 저장
                original = Original(
                    email = d.email,
                    nickname = d.nickname,
                    memberName = d.memberName,
                    phoneNumber = d.phoneNumber.orEmpty(),
                    birthday = d.birthday,
                    zipcode = d.zipcode.orEmpty(),
                    roadAddress = d.roadAddress.orEmpty(),
                    detailAddress = d.detailAddress.orEmpty()
                )
                originalNickname = d.nickname

                // UI 상태 채움
                uiState = uiState.copy(
                    email = d.email,                     // 이메일은 readOnly
                    nickname = d.nickname,
                    memberName = d.memberName,
                    zipcode = d.zipcode.orEmpty(),
                    roadAddress = d.roadAddress.orEmpty(),
                    detailAddress = d.detailAddress.orEmpty(),
                    birthday = runCatching { LocalDate.parse(d.birthday) }.getOrNull(),
                    phoneNumber = d.phoneNumber.orEmpty(),
                    // 수정 진입 시 중복검사/메시지 초기화
                    emailChecked = true, emailAvailable = true,
                    nickChecked = true,  nickAvailable  = true,
                    emailMsg = null, nickMsg = null
                )
            } catch (t: Throwable) {
                errorMsg = t.message ?: "내 정보 불러오기 실패"
            }
        }
    }

    // ---------------------------
    // diff → UpdateBasicRequest
    // ---------------------------
    private fun diffForUpdate(): UpdateBasicRequest {
        val newBirthday = uiState.birthday?.format(DateTimeFormatter.ISO_DATE)
        return UpdateBasicRequest(
            nickname      = uiState.nickname.takeIf { it != original.nickname },
            name          = uiState.memberName.takeIf { it != original.memberName },
            password      = uiState.password.takeIf { it.isNotBlank() },   // 입력했을 때만
            zipcode       = uiState.zipcode.takeIf { it != original.zipcode },
            roadAddress   = uiState.roadAddress.takeIf { it != original.roadAddress },
            detailAddress = uiState.detailAddress.takeIf { it != original.detailAddress },
            birthday      = newBirthday?.takeIf { it != original.birthday },
            phone         = uiState.phoneNumber.takeIf { it != original.phoneNumber }
        )
    }

    // ---------------------------
    // 수정 저장 호출 (부분 업데이트)
    // ---------------------------
    fun updateMyInfo(onSaved: () -> Unit, onError: (String) -> Unit) {
        // canProceed에는 "닉네임 변경 시 중복검사 통과" 로직이 포함되어 있음
        if (!canProceed(FormMode.EDIT)) {
            if (nicknameChanged() && !(uiState.nickChecked && uiState.nickAvailable)) {
                onError("닉네임 중복검사를 먼저 완료해 주세요."); return
            }
            onError("입력값을 확인해 주세요."); return
        }

        val req = diffForUpdate()
        // 모든 필드가 null이면 변경 없음 → 그냥 성공 처리
        if (req == UpdateBasicRequest()) {
            onSaved(); return
        }

        viewModelScope.launch {
            loading = true
            try {
                val api = RetrofitInstance.create(AuthApi::class.java)
                val res = api.updateMyBasic(req)
                if (!res.status.equals("SUCCESS", true)) {
                    throw IllegalStateException(res.message ?: "수정 실패")
                }

                // 성공 → 로컬 원본 갱신
                original = original.copy(
                    nickname = req.nickname ?: original.nickname,
                    memberName = req.name ?: original.memberName,
                    phoneNumber = req.phone ?: original.phoneNumber,
                    birthday = req.birthday ?: original.birthday,
                    zipcode = req.zipcode ?: original.zipcode,
                    roadAddress = req.roadAddress ?: original.roadAddress,
                    detailAddress = req.detailAddress ?: original.detailAddress
                )
                originalNickname = original.nickname

                // 닉네임이 바뀌었으면 SharedPreferences 즉시 반영
                req.nickname?.let { newNick ->
                    UserPrefs.setNickname(newNick)
                }

                onSaved()
            } catch (t: Throwable) {
                onError(t.message ?: "수정에 실패했어요.")
            } finally {
                loading = false
            }
        }
    }
}