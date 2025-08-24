package com.refit.app.ui.screen

import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

import com.refit.app.data.auth.modelAndView.FormMode
import com.refit.app.data.auth.modelAndView.KakaoFlowStore
import com.refit.app.data.auth.modelAndView.SignupViewModel
import com.refit.app.data.auth.modelAndView.KakaoLoginViewModel

/**
 * Step1 → Step2 → Step3 를 오가는 상위 컨테이너.
 * - Step1: 기본정보 (카카오 프리필 닉네임 세팅)
 * - Step2: 고민 체크
 * - Step2 "가입하기" 클릭 시: 카카오 토큰 있으면 kakao signup, 없으면 일반 signup
 * - 성공 시 Step3로 이동
 */
enum class Step { STEP1, STEP2, STEP3 }


@Composable
fun SignupFlowScreen(
    onFinishLogin: () -> Unit // 가입·로그인 플로우 이후 홈으로 갈 때 사용
) {
    val signupVm: SignupViewModel = viewModel()
    val kakaoVm: KakaoLoginViewModel = viewModel()



    var step by remember { mutableStateOf(Step.STEP1) }

    // ✅ StateFlow를 Compose 상태로 수집
    val prefillNick by KakaoFlowStore.prefillNickname.collectAsState()
    val prefillEmail by KakaoFlowStore.prefillEmail.collectAsState()
    val kakaoToken  by KakaoFlowStore.kakaoAccessToken.collectAsState()
    val kakaoId    by KakaoFlowStore.kakaoId.collectAsState()

    // ✅ 값이 들어오면 VM에 주입
    LaunchedEffect(prefillNick, prefillEmail) {
        if (!prefillNick.isNullOrBlank() || !prefillEmail.isNullOrBlank()) {
            signupVm.prefillFromKakao(prefillNick, prefillEmail)
        }
    }

    when (step) {
        Step.STEP1 -> {
            SignupStep1Screen(
                mode = FormMode.SIGNUP,
                onBack = { /* 닫기 or 이전 */ },
                onNextOrSubmit = { step = Step.STEP2 },
                onSearchAddress = { /* 주소검색 다이얼로그 */ },
                // ✅ prefill 값을 실제로 내려보내서 내부 LaunchedEffect가 동작하게 한다
                prefillNickname = prefillNick,
                prefillEmail = prefillEmail,
                vm = signupVm
            )
        }
        Step.STEP2 -> {
            // Step2는 UI만, 제출은 여기서 처리
            SignupStep2Screen(
                selectedSkinType = signupVm.uiState.skinType,
                selectedSkinConcerns = signupVm.uiState.skinConcerns,
                selectedScalpConcerns = signupVm.uiState.scalpConcerns,
                selectedHealthConcerns = signupVm.uiState.healthConcerns,
                onSkinTypeChange = signupVm::setSkinType,
                onToggleSkinConcern = signupVm::toggleSkinConcern,
                onToggleScalpConcern = signupVm::toggleScalpConcern,
                onToggleHealthConcern = signupVm::toggleHealthConcern,
                onBack = { step = Step.STEP1 },
                submitEnabled = signupVm.isStep2Valid,
                onNextOrSubmit = {
                    if (!kakaoToken.isNullOrBlank()) {
                        val req = signupVm.buildSignupAllRequest()
                        kakaoVm.signupWithKakao(
                            kakaoAccessToken = kakaoToken!!,
                            signupAll = req,
                            kakaoId = kakaoId!!,
                            onSuccessLogin = {
                                KakaoFlowStore.clear()
                                step = Step.STEP3
                            },
                            onError = { /* TODO */ }
                        )
                    } else {
                        signupVm.submitSignup(
                            onSuccess = { step = Step.STEP3 },
                            onError = { /* TODO */ }
                        )
                    }
                }
            )
        }
        Step.STEP3 -> {
            SignupStep3Screen(
                nickname = signupVm.uiState.nickname,
                onBack = { step = Step.STEP2 },
                onLogin = {
                    // Step3에서 "로그인" 버튼 눌렀을 때 처리
                    onFinishLogin()
                }
            )
        }
    }
}