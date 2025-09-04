package com.refit.app.ui.composable.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.refit.app.data.auth.modelAndView.FormMode
import com.refit.app.ui.theme.MainPurple
import java.time.LocalDate

@Composable
fun BasicInfoForm(
    mode: FormMode,
    email: String,
    password: String,
    passwordConfirm: String,
    nickname: String,
    memberName: String,
    phoneNumber: String,
    birthday: LocalDate?,
    zipcode: String,
    roadAddress: String,
    detailAddress: String,
    emailMsg: String?,
    emailAvailable: Boolean,
    emailCheckLoading: Boolean,
    nickMsg: String?,
    nickAvailable: Boolean,
    nickCheckLoading: Boolean,
    onEmail: (String) -> Unit,
    onPassword: (String) -> Unit,
    onPasswordConfirm: (String) -> Unit,
    onNick: (String) -> Unit,
    onMemberName: (String) -> Unit,
    onPhone: (String) -> Unit,
    onBirthday: (LocalDate?) -> Unit,
    onZip: (String) -> Unit,
    onRoad: (String) -> Unit,
    onDetail: (String) -> Unit,
    onCheckEmail: () -> Unit,
    onCheckNick: () -> Unit,
    onSearchAddress: () -> Unit,
    isPasswordRuleOk: Boolean,
    isPasswordConfirmMatch: Boolean,
    isPhoneStartsWith010: Boolean,
    isPhoneFormatOk: Boolean,
    emailReadOnly: Boolean = (mode == FormMode.EDIT),
) {
    val okColor = MainPurple
    val errColor = Color(0xFFD32F2F)

    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        // 이메일
        FieldWithSideButton(
            label = "이메일",
            value = email,
            onValueChange = if (mode == FormMode.EDIT) ({}) else onEmail,
            placeholder = "이메일 입력",
            readOnly = (mode == FormMode.EDIT),
            enabled = (mode != FormMode.EDIT),
            supportingText = if (mode == FormMode.SIGNUP) {
                {
                    emailMsg?.let { msg ->
                        Text(
                            msg,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (emailAvailable) okColor else errColor
                        )
                    }
                }
            } else null,
            buttonText = if (emailCheckLoading) "확인중..." else "중복확인",
            buttonEnabled = !emailCheckLoading && email.isNotBlank(),
            onButtonClick = onCheckEmail,
            showButton = (mode == FormMode.SIGNUP) // EDIT에선 버튼 숨김
        )

        // 비밀번호
        FieldWithSideButton(
            label = "비밀번호",
            value = password,
            onValueChange = onPassword,
            placeholder = "비밀번호 입력",
            showButton = false,
            visualTransformation = PasswordVisualTransformation(),
            supportingText = {
                if (password.isNotEmpty()) {
                    Text(
                        if (isPasswordRuleOk) "사용 가능한 비밀번호입니다."
                        else "영문+숫자 or 영문+특수문자 포함 8자 이상으로 입력해주세요.",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isPasswordRuleOk) MainPurple else Color(0xFFD32F2F)
                    )
                }
            }
        )

        // 비밀번호 확인
        FieldWithSideButton(
            label = "비밀번호 확인",
            value = passwordConfirm,
            onValueChange = onPasswordConfirm,
            placeholder = "비밀번호 확인 입력",
            showButton = false,
            visualTransformation = PasswordVisualTransformation(),
            supportingText = {
                if (passwordConfirm.isNotEmpty()) {
                    Text(
                        if (isPasswordConfirmMatch) "비밀번호가 일치합니다."
                        else "비밀번호가 일치하지 않습니다.",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isPasswordConfirmMatch) MainPurple else Color(0xFFD32F2F)
                    )
                }
            }
        )

        // 닉네임
        FieldWithSideButton(
            label = "닉네임",
            value = nickname,
            onValueChange = if (mode == FormMode.EDIT) ({}) else onNick,
            placeholder = "닉네임 입력",
            readOnly = (mode == FormMode.EDIT),
            enabled = (mode != FormMode.EDIT),
            supportingText = if (mode == FormMode.SIGNUP) {
                {
                    nickMsg?.let { msg ->
                        Text(
                            msg,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (nickAvailable) okColor else errColor
                        )
                    }
                }
            } else null,
            buttonText = if (nickCheckLoading) "확인중..." else "중복확인",
            buttonEnabled = !nickCheckLoading && nickname.isNotBlank(),
            onButtonClick = onCheckNick,
            showButton = (mode == FormMode.SIGNUP) // EDIT에선 버튼 숨김
        )

        // 이름
        FieldWithSideButton(
            label = "이름",
            value = memberName,
            onValueChange = onMemberName,
            placeholder = "이름 입력",
            showButton = false
        )

        // 휴대폰 번호
        FieldWithSideButton(
            label = "휴대폰 번호",
            value = phoneNumber,
            onValueChange = onPhone,
            placeholder = "휴대폰 번호 입력",
            showButton = false,
            supportingText = {
                if (phoneNumber.isNotEmpty()) {
                    Text(
                        when {
                            !isPhoneStartsWith010 -> "010으로 시작해야 합니다."
                            !isPhoneFormatOk -> "형식이 올바르지 않습니다. (총 11자리 숫자)"
                            else -> "번호 형식이 올바릅니다."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = when {
                            !isPhoneStartsWith010 || !isPhoneFormatOk -> Color(0xFFD32F2F)
                            else -> MainPurple
                        }
                    )
                }
            }
        )

        // 생년월일
        BirthdayField(
            value = birthday,
            onChange = onBirthday
        )

        // 주소 (우편번호/도로명/상세 + 검색)
        AddressRow(
            zipcode = zipcode, onZip = onZip,
            road = roadAddress, onRoad = onRoad,
            detail = detailAddress, onDetail = onDetail,
            onSearchAddress = onSearchAddress
        )
    }
}