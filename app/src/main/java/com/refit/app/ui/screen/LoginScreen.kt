package com.refit.app.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.refit.app.R
import com.refit.app.data.auth.model.SamsungHealthSaveRequest
import com.refit.app.ui.composable.auth.KakaoButton
import com.refit.app.ui.composable.auth.PurpleButton
import com.refit.app.ui.composable.auth.RefitTextField
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard
import com.refit.app.data.auth.modelAndView.AuthViewModel
import com.refit.app.data.auth.modelAndView.KakaoFlowStore
import com.refit.app.data.auth.modelAndView.KakaoLoginViewModel
import com.refit.app.data.auth.modelAndView.SamsungHealthViewModel
import com.refit.app.data.health.HealthRepo

@Composable
fun LoginScreen(
    onClose: () -> Unit,
    onSignup: (prefilledFromKakao: Boolean) -> Unit, // ← 카카오 프리필로 회원가입 진입
    onLoggedIn: () -> Unit,
    vm: AuthViewModel = viewModel(),
    kakaoVm: KakaoLoginViewModel = viewModel(),
    samsungVm: SamsungHealthViewModel = viewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(vm.loggedIn) {
        if (vm.loggedIn) {

            // 로그인 성공시 삼성헬스의 오늘 하루치 데이터를 불러와서 저장
            val rows = HealthRepo.readDailyAll(context, days = 1)
            rows.firstOrNull()?.let { today ->
                val req = SamsungHealthSaveRequest(
                    steps = today.steps,
                    totalKcal = today.totalKcal,
                    bloodGlucoseMgdl = today.bloodGlucoseMgdl,
                    systolicMmhg = today.systolicMmhg,
                    diastolicMmhg = today.diastolicMmhg,
                    intakeKcal = today.intakeKcal,
                    sleepMinutes = today.sleepMinutes
                )
                samsungVm.saveHealthInfo(req)
            }

            onLoggedIn()
        }
    }

    Scaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFAFAFD))
                .padding(it)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
                    .align(Alignment.Center)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_login_logo),
                        contentDescription = "Mascot",
                        modifier = Modifier
                            .size(300.dp)
                            .align(Alignment.CenterHorizontally)
                    )

                    Spacer(Modifier.height(24.dp))

                    RefitTextField(
                        value = vm.email,
                        onValueChange = { vm.onEmailChange(it); vm.clearError() },
                        hint = "이메일",
                        modifier = Modifier.width(382.dp)
                    )

                    Spacer(Modifier.height(15.dp))

                    RefitTextField(
                        value = vm.password,
                        onValueChange = { vm.onPasswordChange(it); vm.clearError() },
                        hint = "비밀번호",
                        isPassword = true,
                        modifier = Modifier.width(382.dp)
                    )
                }

                vm.error?.let { msg ->
                    Spacer(Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF8E24AA))
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(text = msg, color = Color(0xFF6A1B9A), style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Spacer(Modifier.height(25.dp))

                PurpleButton(
                    text = "로그인",
                    onClick = { vm.login() },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(Modifier.height(15.dp))

                KakaoButton(
                    onClick = {
                        kakaoVm.startLogin(
                            context = context,
                            onNeedSignup = { nickname, email, kakaoToken, kakaoId ->
                                KakaoFlowStore.set(
                                    accessToken = kakaoToken,
                                    nickname = nickname,
                                    email = email,
                                    kakaoId = kakaoId
                                )
                                onSignup(true)
                            },
                            onLoggedIn = {
                                // 바로 로그인된 케이스
                                onLoggedIn()
                            },
                            onError = { msg ->
                                // TODO: 스낵바/다이얼로그로 표시
                            }
                        )
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(Modifier.height(15.dp))

                Text(
                    text = "회원가입",
                    color = MainPurple,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clickable { onSignup(false) },
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    fontFamily = Pretendard,
                )
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreen(
            onClose = {},
            onSignup = {},
            onLoggedIn = {}
        )
    }
}
