package com.refit.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.refit.app.ui.composable.auth.*
import com.refit.app.ui.theme.MainPurple
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.refit.app.data.auth.modelAndView.FormMode
import com.refit.app.ui.theme.Pretendard
import com.refit.app.data.auth.modelAndView.SignupViewModel

@Composable
fun SignupStep1Screen(
    mode: FormMode,
    onBack: () -> Unit,
    onNextOrSubmit: () -> Unit,
    onSearchAddress: () -> Unit,
    vm: SignupViewModel = viewModel()
) {
    val showDialog = remember { mutableStateOf(false) }

    Scaffold(
        topBar = { SignupTopBar("회원가입", 1, 3, onBack) },
        containerColor = Color.White
    ) { pad ->
        val scroll = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(pad)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(scroll)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    BasicInfoForm(
                        mode = FormMode.SIGNUP,
                        // 상태
                        email = vm.uiState.email,
                        password = vm.uiState.password,
                        passwordConfirm = vm.uiState.passwordConfirm,
                        nickname = vm.uiState.nickname,
                        memberName = vm.uiState.memberName,
                        phoneNumber = vm.uiState.phoneNumber,
                        birthday = vm.uiState.birthday,
                        zipcode = vm.uiState.zipcode,
                        roadAddress = vm.uiState.roadAddress,
                        detailAddress = vm.uiState.detailAddress,
                        // 메시지/로딩
                        emailMsg = vm.uiState.emailMsg,
                        emailAvailable = vm.uiState.emailAvailable,
                        emailCheckLoading = vm.uiState.emailCheckLoading,
                        nickMsg = vm.uiState.nickMsg,
                        nickAvailable = vm.uiState.nickAvailable,
                        nickCheckLoading = vm.uiState.nickCheckLoading,
                        // 핸들러
                        onEmail = vm::onEmail,
                        onPassword = vm::onPassword,
                        onPasswordConfirm = vm::onPasswordConfirm,
                        onNick = vm::onNick,
                        onMemberName = vm::onMemberName,
                        onPhone = vm::onPhone,
                        onBirthday = vm::onBirthday,
                        onZip = vm::onZipcode,
                        onRoad = vm::onRoad,
                        onDetail = vm::onDetail,
                        // 액션
                        onCheckEmail = vm::checkEmailDuplicate,
                        onCheckNick = vm::checkNickDuplicate,
                        onSearchAddress = { showDialog.value = true },
                        // 파생 검증
                        isPasswordRuleOk = vm.isPasswordRuleOk,
                        isPasswordConfirmMatch = vm.isPasswordConfirmMatch,
                        isPhoneStartsWith010 = vm.isPhoneStartsWith010,
                        isPhoneFormatOk = vm.isPhoneFormatOk
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onNextOrSubmit,
                enabled = vm.canProceed(mode),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MainPurple, contentColor = Color.White)
            ) { Text("다음", fontFamily = Pretendard) }
        }
    }

    if (showDialog.value) {
        AddressSearchDialog(
            onDismiss = { showDialog.value = false },
            onSelected = { zone, road ->
                vm.onZipcode(zone); vm.onRoad(road); showDialog.value = false
            }
        )
    }
}
