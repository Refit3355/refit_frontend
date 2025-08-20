package com.refit.app.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.refit.app.R
import com.refit.app.ui.composable.auth.KakaoButton
import com.refit.app.ui.composable.auth.PurpleButton
import com.refit.app.ui.composable.auth.RefitTextField
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard
import com.refit.app.ui.viewmodel.auth.AuthViewModel

@Composable
fun LoginScreen(
    onClose: () -> Unit,
    onSignup: () -> Unit,
    onLoggedIn: () -> Unit,
    vm: AuthViewModel = viewModel()
) {
    LaunchedEffect(vm.loggedIn) {
        if (vm.loggedIn) onLoggedIn()
    }
   // 배경
    Scaffold { padding ->
        Box (
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFAFAFD))
                .padding(padding)
        ) {
            // 본문
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
                    .align(Alignment.Center)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
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
                        modifier = Modifier
                            .width(382.dp)
                            .align(Alignment.CenterHorizontally)
                    )

                    Spacer(Modifier.height(15.dp))

                    RefitTextField(
                        value = vm.password,
                        onValueChange = { vm.onPasswordChange(it); vm.clearError() },
                        hint = "비밀번호",
                        isPassword = true,
                        modifier = Modifier
                            .width(382.dp)
                            .align(Alignment.CenterHorizontally)
                    )

                }

                // 에러 안내
                vm.error?.let { msg ->
                    Spacer(Modifier.height(16.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ){
                        // 간단한 경고
                        Box(
                            Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF8E24AA))
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = msg,
                            color = Color(0xFF6A1B9A),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(Modifier.height(25.dp))

                // 로그인 버튼
                PurpleButton(
                    text = "로그인",
                    onClick = {vm.login()},
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(Modifier.height(15.dp))

                KakaoButton(
                    onClick = { vm.loginKakao() },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(Modifier.height(15.dp))

                Text(
                    text = "회원가입",
                    color = MainPurple,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clickable { onSignup() },
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