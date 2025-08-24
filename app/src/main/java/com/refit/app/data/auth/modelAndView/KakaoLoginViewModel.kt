package com.refit.app.data.auth.modelAndView

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.refit.app.data.auth.api.AuthApi
import com.refit.app.data.auth.model.*
import com.refit.app.network.RetrofitInstance
import com.refit.app.network.TokenManager
import com.refit.app.network.UserPrefs
import kotlinx.coroutines.launch


class KakaoLoginViewModel : ViewModel() {

    /** 카카오 로그인 시작 */
    fun startLogin(
        context: Context,
        onNeedSignup: (prefillNickname: String?, prefillEmail: String?, kakaoAccessToken: String, kakaoId: String?) -> Unit, // ✅ 4번째 파라미터 추가
        onLoggedIn: () -> Unit,
        onError: (String) -> Unit
    ) {
        // 콜백 (라벨로 조기 return 가능)
        val callback: (OAuthToken?, Throwable?) -> Unit = cb@ { token, error ->
            if (error != null) {
                onError("카카오 로그인 실패: ${error.localizedMessage ?: "알 수 없는 오류"}")
                return@cb
            }
            val accessToken = token?.accessToken
            if (accessToken.isNullOrBlank()) {
                onError("카카오 액세스 토큰을 가져오지 못했습니다.")
                return@cb
            }
            // 서버 검증
            verifyOnServer(accessToken, onNeedSignup, onLoggedIn, onError)
        }

        // 카카오톡 앱 → 실패 시 계정 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    UserApiClient.instance.loginWithKakaoAccount(
                        context = context,
                        callback = callback
                    )
                } else {
                    callback(token, null)
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(
                context = context,
                callback = callback
            )
        }
    }

    /** 서버에 카카오 토큰 검증 */
    private fun verifyOnServer(
        kakaoAccessToken: String,
        onNeedSignup: (String?, String?, String, String?) -> Unit,
        onLoggedIn: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val api = RetrofitInstance.create(AuthApi::class.java)
                val resp = api.kakaoVerify(KakaoVerifyRequest(kakaoAccessToken))

                if (!resp.isSuccessful) {
                    onError("카카오 검증 실패 (${resp.code()})"); return@launch
                }
                val body = resp.body() ?: run {
                    onError("서버 응답 본문이 비었습니다."); return@launch
                }
                val data = body.data ?: run {
                    onError(body.message ?: "검증 데이터 없음"); return@launch
                }

                if (data.needSignup) {
                    onNeedSignup(
                        data.nickname,
                        data.email,
                        kakaoAccessToken,
                        data.kakaoId
                    )
                } else {
                    // 이미 연동 → 바로 로그인 처리
                    val bearer = resp.headers()["Authorization"] ?: resp.headers()["authorization"]
                    val access = extractBearer(bearer)
                    val refresh = data.refreshToken
                    TokenManager.saveTokens(access, refresh)

                    UserPrefs.saveUser(
                        memberId = data.memberId,
                        nickname = data.userNickname ?: data.nickname,
                        health   = data.health,
                        hair     = data.hair,
                        skin     = data.skin
                    )
                    onLoggedIn()
                }
            } catch (t: Throwable) {
                onError(t.message ?: "카카오 검증 중 오류")
            }
        }
    }

    /** 카카오 회원가입 (Step2에서 가입 완료 시 호출) */
    fun signupWithKakao(
        kakaoAccessToken: String,
        signupAll: SignupAllRequest,
        kakaoId: String,
        onSuccessLogin: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val api = RetrofitInstance.create(AuthApi::class.java)
                val resp = api.kakaoSignup(
                    KakaoSignupRequest(
                        accessToken = kakaoAccessToken,
                        kakaoId = kakaoId,
                        signupAll = signupAll
                    )
                )

                if (!resp.isSuccessful) {
                    onError("회원가입 실패 (${resp.code()})"); return@launch
                }
                val body = resp.body() ?: run { onError("응답 없음"); return@launch }
                val data = body.data ?: run { onError(body.message ?: "데이터 없음"); return@launch }

                val bearer = resp.headers()["Authorization"] ?: resp.headers()["authorization"]
                val access = extractBearer(bearer)
                TokenManager.saveTokens(access, data.refreshToken)

                UserPrefs.saveUser(
                    memberId = data.memberId,
                    nickname = data.nickname,
                    health   = data.health,
                    hair     = data.hair,
                    skin     = data.skin
                )
                onSuccessLogin()
            } catch (t: Throwable) {
                onError(t.message ?: "카카오 회원가입 중 오류")
            }
        }
    }

    private fun extractBearer(header: String?): String? {
        if (header.isNullOrBlank()) return null
        val parts = header.trim().split(" ")
        return if (parts.size == 2 && parts[0].equals("Bearer", true)) parts[1].trim()
        else header.trim()
    }
}