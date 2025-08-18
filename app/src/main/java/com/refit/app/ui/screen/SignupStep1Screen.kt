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
import com.refit.app.ui.viewmodel.auth.SignupViewModel

private val FIELD_WIDTH = 382.dp
private val RADIUS = 16.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupStep1Screen(
    onBack: () -> Unit,
    onNextOrSubmit: () -> Unit,
    onSearchAddress: () -> Unit,
    vm: SignupViewModel = viewModel()
){
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("회원가입") },
                    navigationIcon = { TextButton(onClick = onBack) { Text("뒤로") } }
                )
                SignupProgressBar(step = 1)
            }
        }
    ){ pad ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(scrollState)
                .padding(pad)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.width(FIELD_WIDTH),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(RADIUS)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            LabeledField(
                                label = "이메일",
                                value = vm.uiState.email,
                                onValueChange = vm::onEmail,
                                placeholder = "example@email.com"
                            )
                            LabeledField(
                                label = "비밀번호",
                                value = vm.uiState.password,
                                onValueChange = vm::onPassword,
                                placeholder = "영문/숫자/특수문자 2종 이상 8~64자",
                                visualTransformation = PasswordVisualTransformation(),
                                trailing = {}
                            )
                            LabeledField(
                                label = "비밀번호 확인",
                                value = vm.uiState.passwordConfirm,
                                onValueChange = vm::onPasswordConfirm,
                                placeholder = "비밀번호 재입력",
                                visualTransformation = PasswordVisualTransformation()
                            )
                            LabeledField(
                                label = "닉네임",
                                value = vm.uiState.nickname,
                                onValueChange = vm::onNick,
                                placeholder = "닉네임"
                            )
                            LabeledField(
                                label = "이름",
                                value = vm.uiState.memberName,
                                onValueChange = vm::onMemberName,
                                placeholder = "실명"
                            )
                            LabeledField(
                                label = "휴대폰 번호",
                                value = vm.uiState.phoneNumber,
                                onValueChange = vm::onPhone,
                                placeholder = "01012345678"
                            )

                            Text(text = "성별", style = MaterialTheme.typography.labelLarge)
                            GenderSelector(
                                selected = vm.uiState.gender,
                                onSelect = vm::onGender
                            )

                            BirthdayField(
                                value = vm.uiState.birthday,
                                onChange = vm::onBirthday
                            )

                            AddressRow(
                                zipcode = vm.uiState.zipcode, onZip = vm::onZipcode,
                                road = vm.uiState.roadAddress, onRoad = vm::onRoad,
                                detail = vm.uiState.detailAddress, onDetail = vm::onDetail,
                                onSearchAddress = onSearchAddress
                            )
                        }
                    }

                    Button(
                        onClick = onNextOrSubmit,
                        enabled = vm.isValid,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(RADIUS),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MainPurple,
                            contentColor = MainPurple
                        )
                    ) {
                        Text("다음")
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
        SignupStep1Screen(
            onBack = {},
            onNextOrSubmit = {},
            onSearchAddress = {}
        )
    }
}