package com.refit.app.data.auth.modelAndView

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refit.app.data.auth.api.AuthApi
import com.refit.app.data.auth.model.LoginRequest
import com.refit.app.network.RetrofitInstance
import com.refit.app.network.TokenManager
import com.refit.app.network.UserPrefs
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var loading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set
    var loggedIn by mutableStateOf(false)
        private set

    fun onEmailChange(v: String)    { email = v.trim() }
    fun onPasswordChange(v: String) { password = v }
    fun clearError()                { error = null }

    fun login() {
        if (email.isBlank() || password.isBlank()) {
            error = "아이디 또는 비밀번호를 확인해주세요."
            return
        }

        viewModelScope.launch {
            loading = true
            error = null
            loggedIn = false

            try {
                val api = RetrofitInstance.create(AuthApi::class.java)
                val resp = api.login(LoginRequest(email = email, password = password))

                if (!resp.isSuccessful) {
                    error = "로그인 실패 (${resp.code()})"
                    return@launch
                }

                val body = resp.body()
                if (body == null) {
                    error = "응답 본문이 없습니다."
                    return@launch
                }

                // 1) 헤더에서 access token 추출
                val bearer = resp.headers()["Authorization"] ?: resp.headers()["authorization"]
                val access = extractBearer(bearer)

                // 2) 바디에서 refresh / 프로필 데이터 추출
                val data = body.data
                val refresh = data?.refreshToken

                // 3) 저장
                TokenManager.saveTokens(access = access, refresh = refresh)
                UserPrefs.saveUser(
                    memberId = data?.memberId,
                    nickname = data?.nickname,
                    health   = data?.health
                )

                // access가 비어있어도(예외상황) refresh로 재발급 로직을 이후에 돌릴 수 있으니 성공 처리
                loggedIn = true

            } catch (t: Throwable) {
                error = t.message ?: "네트워크 오류가 발생했습니다."
            } finally {
                loading = false
            }
        }
    }

    fun logout() {
        TokenManager.clearAll()
        UserPrefs.clear()
        loggedIn = false
        email = ""
        password = ""
        error = null
    }
    fun loginKakao() {

    }
    private fun extractBearer(header: String?): String? {
        if (header.isNullOrBlank()) return null
        val parts = header.trim().split(" ")
        return when {
            parts.size == 2 && parts[0].equals("Bearer", ignoreCase = true) -> parts[1].trim()
            else -> header.trim()
        }.takeIf { it.isNotBlank() }
    }

}