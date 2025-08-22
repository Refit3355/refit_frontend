package com.refit.app.ui.screen
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.refit.app.data.auth.modelAndView.FormMode
import com.refit.app.data.auth.modelAndView.SignupViewModel
import com.refit.app.ui.composable.auth.*
import com.refit.app.ui.theme.MainPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBasicInfoScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    vm: SignupViewModel = viewModel()
) {
    val showDialog = remember { mutableStateOf(false) }

    // 처음 진입 시 프리필
    LaunchedEffect(Unit) { vm.prefillFromMe() }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("내 정보 수정") }, navigationIcon = {
                TextButton(onClick = onBack) { Text("뒤로") }
            })
        }
    ) { pad ->
        Column(Modifier.padding(pad).padding(16.dp).verticalScroll(rememberScrollState())) {
            BasicInfoForm(
                mode = FormMode.EDIT,
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
                emailMsg = null,                  // EDIT: 이메일 고정
                emailAvailable = true,
                emailCheckLoading = false,
                nickMsg = vm.uiState.nickMsg,
                nickAvailable = vm.uiState.nickAvailable,
                nickCheckLoading = vm.uiState.nickCheckLoading,
                // 핸들러 (이메일은 수정 불가 → 무시 람다)
                onEmail = {},
                onPassword = vm::onPassword,
                onPasswordConfirm = vm::onPasswordConfirm,
                onNick = vm::onNick,
                onMemberName = vm::onMemberName,
                onPhone = vm::onPhone,
                onBirthday = vm::onBirthday,
                onZip = vm::onZipcode,
                onRoad = vm::onRoad,
                onDetail = vm::onDetail,
                // 액션 (이메일 체크 없음)
                onCheckEmail = {},
                onCheckNick = vm::checkNickDuplicateForEdit, // EDIT 전용 (원닉 동일 시 통과)
                onSearchAddress = { showDialog.value = true },
                // 파생 검증
                isPasswordRuleOk = vm.isPasswordRuleOkOrEmpty,   // 빈값 허용
                isPasswordConfirmMatch = vm.isPasswordConfirmMatchOrEmpty,
                isPhoneStartsWith010 = vm.isPhoneStartsWith010,
                isPhoneFormatOk = vm.isPhoneFormatOk
            )

            Spacer(Modifier.height(16.dp))

//            Button(
//                onClick = { vm.updateMyInfo(onSaved, onError = {}) },
//                enabled = vm.canProceed(FormMode.EDIT),
//                modifier = Modifier.fillMaxWidth().height(52.dp),
//                colors = ButtonDefaults.buttonColors(containerColor = MainPurple)
//            ) { Text("저장") }
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