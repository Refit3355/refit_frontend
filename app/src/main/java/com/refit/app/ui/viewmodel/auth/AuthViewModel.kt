package com.refit.app.ui.viewmodel.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel() {
    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun onEmailChange(v: String) {email = v}
    fun onPasswordChange(v: String) {password = v}

    fun login() {
        error = if (email.isBlank() || password.isBlank()) {
            "아이디 또는 비밀번호를 확인해주세요."
        } else null
    }

    fun loginKakao() {

    }

    fun clearError() {error = null}

}