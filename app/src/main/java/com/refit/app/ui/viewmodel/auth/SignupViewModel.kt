package com.refit.app.ui.viewmodel.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import java.time.LocalDate

class SignupViewModel : ViewModel() {
    var uiState: SignupUiState = SignupUiState()
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
}