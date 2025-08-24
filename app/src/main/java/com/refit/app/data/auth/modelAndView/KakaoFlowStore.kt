package com.refit.app.data.auth.modelAndView

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 카카오 검증 결과를 잠깐 들고 있다가
 * - Step1에 닉네임/이메일 프리필
 * - Step2 "가입하기" 클릭 시 kakaoAccessToken 사용
 * 하는 용도의 임시 저장소
 */
object KakaoFlowStore {
    private val _kakaoAccessToken  = MutableStateFlow<String?>(null)
    private val _prefillNickname   = MutableStateFlow<String?>(null)
    private val _prefillEmail      = MutableStateFlow<String?>(null)
    private val _kakaoId           = MutableStateFlow<String?>(null)

    val kakaoAccessToken = _kakaoAccessToken.asStateFlow()
    val prefillNickname  = _prefillNickname.asStateFlow()
    val prefillEmail     = _prefillEmail.asStateFlow()
    val kakaoId          = _kakaoId.asStateFlow()

    fun set(accessToken: String?, nickname: String?, email: String?, kakaoId: String?) {
        _kakaoAccessToken.value = accessToken
        _prefillNickname.value  = nickname
        _prefillEmail.value     = email
        _kakaoId.value          = kakaoId
    }

    fun clear() = set(null, null, null, null)
}