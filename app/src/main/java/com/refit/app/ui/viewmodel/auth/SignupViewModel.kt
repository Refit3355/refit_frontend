package com.refit.app.ui.viewmodel.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.time.LocalDate

class SignupViewModel : ViewModel() {
    var uiState by mutableStateOf(SignupUiState())
        private set

    fun onEmail(v: String)            { uiState = uiState.copy(email = v.trim()) }
    fun onPassword(v: String)         { uiState = uiState.copy(password = v) }
    fun onPasswordConfirm(v: String)  { uiState = uiState.copy(passwordConfirm = v) }
    fun onNick(v: String)             { uiState = uiState.copy(nickname = v) }
    fun onMemberName(v: String)       { uiState = uiState.copy(memberName = v) }
    fun onPhone(v: String)            { uiState = uiState.copy(phoneNumber = v.filter { it.isDigit() }.take(11)) }
    fun onBirthday(d: LocalDate?)     { uiState = uiState.copy(birthday = d) }
    fun onZipcode(v: String)          { uiState = uiState.copy(zipcode = v.take(5)) }
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
}