package com.refit.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.refit.app.ui.composable.auth.*
import com.refit.app.ui.theme.MainPurple
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.refit.app.ui.theme.Pretendard
import com.refit.app.data.auth.modelAndView.SignupViewModel

@Composable
fun SignupStep1Screen(
    onBack: () -> Unit,
    onNextOrSubmit: () -> Unit,
    onSearchAddress: () -> Unit,
    vm: SignupViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            SignupTopBar(
                title = "회원가입",
                stepIndex = 1,
                stepCount = 3,
                onBack = onBack
            )
        },
        containerColor = Color.White
    ) { pad ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(pad)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(scrollState)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            val labelStyle = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp)

                            // 이메일 + 중복확인
                            LabeledField(
                                label = "이메일",
                                value = vm.uiState.email,
                                onValueChange = vm::onEmail,
                                placeholder = "이메일 입력",
                                modifier = Modifier.fillMaxWidth(),
                                labelTextStyle = labelStyle,
                                supportingText = {
                                    vm.uiState.emailMsg?.let { msg ->
                                        val ok = vm.uiState.emailAvailable
                                        Text(
                                            msg,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (ok) MainPurple else Color(0xFFD32F2F)
                                        )
                                    }
                                },
                                trailing = {
                                    Box(Modifier.padding(end = 6.dp)) {
                                        InlineActionButton(
                                            text = if (vm.uiState.emailCheckLoading) "확인중..." else "중복확인",
                                            enabled = !vm.uiState.emailCheckLoading && vm.uiState.email.isNotBlank(),
                                            onClick = { vm.checkEmailDuplicate() }
                                        )
                                    }
                                }
                            )


                            // 비밀번호
                            LabeledField(
                                label = "비밀번호",
                                value = vm.uiState.password,
                                onValueChange = vm::onPassword,
                                placeholder = "비밀번호 입력",
                                labelTextStyle = labelStyle,
                                visualTransformation = PasswordVisualTransformation(),
                                supportingText = {
                                    if (vm.uiState.password.isNotEmpty()) {
                                        if (vm.isPasswordRuleOk) {
                                            Text(
                                                "사용 가능한 비밀번호입니다.",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MainPurple
                                            )
                                        } else {
                                            Text(
                                                "영문+숫자/특수문자 포함 8자 이상으로 입력해주세요.",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFFD32F2F)
                                            )
                                        }
                                    }
                                }
                            )

                            // 비밀번호 확인
                            LabeledField(
                                label = "비밀번호 확인",
                                value = vm.uiState.passwordConfirm,
                                onValueChange = vm::onPasswordConfirm,
                                placeholder = "비밀번호 확인 입력",
                                labelTextStyle = labelStyle,
                                visualTransformation = PasswordVisualTransformation(),
                                supportingText = {
                                    if (vm.uiState.passwordConfirm.isNotEmpty()) {
                                        if (vm.isPasswordConfirmMatch) {
                                            Text(
                                                "비밀번호가 일치합니다.",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MainPurple
                                            )
                                        } else {
                                            Text(
                                                "비밀번호가 일치하지 않습니다.",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFFD32F2F)
                                            )
                                        }
                                    }
                                }
                            )

                            // 닉네임 + 중복확인
                            LabeledField(
                                label = "닉네임",
                                value = vm.uiState.nickname,
                                onValueChange = vm::onNick,
                                placeholder = "닉네임 입력",
                                labelTextStyle = labelStyle,
                                supportingText = {
                                    vm.uiState.nickMsg?.let { msg ->
                                        val ok = vm.uiState.nickAvailable
                                        Text(
                                            msg,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (ok) MainPurple else Color(0xFFD32F2F)
                                        )
                                    }
                                },
                                trailing = {
                                    Box(Modifier.padding(end = 6.dp)) {
                                        InlineActionButton(
                                            text = if (vm.uiState.nickCheckLoading) "확인중..." else "중복확인",
                                            enabled = !vm.uiState.nickCheckLoading && vm.uiState.nickname.isNotBlank(),
                                            onClick = { vm.checkNickDuplicate() }
                                        )
                                    }
                                }
                            )

                            // 이름
                            LabeledField(
                                label = "이름",
                                value = vm.uiState.memberName,
                                onValueChange = vm::onMemberName,
                                placeholder = "이름 입력",
                                labelTextStyle = labelStyle,
                            )

                            // 휴대폰
                            LabeledField(
                                label = "휴대폰 번호",
                                value = vm.uiState.phoneNumber,
                                onValueChange = vm::onPhone,
                                placeholder = "휴대폰 번호 입력",
                                labelTextStyle = labelStyle,
                                supportingText = {
                                    if (vm.uiState.phoneNumber.isNotEmpty()) {
                                        when {
                                            !vm.isPhoneStartsWith010 -> Text(
                                                "010으로 시작해야 합니다.",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFFD32F2F)
                                            )
                                            !vm.isPhoneFormatOk -> Text(
                                                "형식이 올바르지 않습니다. (총 11자리 숫자)",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFFD32F2F)
                                            )
                                            else -> Text(
                                                "형식이 올바릅니다.",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MainPurple
                                            )
                                        }
                                    }
                                }
                            )

                            // 생년월일
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // 생년월일 입력 필드
                                BirthdayField(
                                    value = vm.uiState.birthday,
                                    onChange = vm::onBirthday,
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                            }

                            // 주소
                            AddressRow(
                                zipcode = vm.uiState.zipcode, onZip = vm::onZipcode,
                                road = vm.uiState.roadAddress, onRoad = vm::onRoad,
                                detail = vm.uiState.detailAddress, onDetail = vm::onDetail,
                                onSearchAddress = onSearchAddress
                            )
                        }
                    }

                    // 하단 버튼
                    Button(
                        onClick = onNextOrSubmit,
                        enabled = vm.isStep1Ready,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(10),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MainPurple,
                            contentColor = Color.White
                        )
                    ) {
                        Text("다음",
                            style = MaterialTheme.typography.labelLarge.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Pretendard,
                        ))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 420, heightDp = 1000)
@Composable
private fun PreviewSignupStep1() {
    MaterialTheme {
        SignupStep1Screen(onBack = {}, onNextOrSubmit = {}, onSearchAddress = {})
    }
}