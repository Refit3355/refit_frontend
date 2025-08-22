package com.refit.app.data.auth.modelAndView

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.refit.app.data.auth.api.AuthApi
import com.refit.app.network.RetrofitInstance

class KakaoAuthViewModel (
    private val api: AuthApi = RetrofitInstance.create(AuthApi::class.java)
) : ViewModel() {
    var error by mutableStateOf<String?>(null); private set
    var loggedIn by mutableStateOf(false); private set

    var kakaoSignupAccessToken: String? = null; private set
    var kakaoPrefillNickname: String? = null; private set

    fun clearError() { error = null }
    fun clearKakaoSignupSession() { kakaoSignupAccessToken = null; kakaoPrefillNickname = null }


}