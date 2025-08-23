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
    mode: FormMode,                    // SIGNUP / EDIT
    // 상태
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

    // 메시지/로딩
    emailMsg: String?,
    emailAvailable: Boolean,
    emailCheckLoading: Boolean,
    nickMsg: String?,
    nickAvailable: Boolean,
    nickCheckLoading: Boolean,

    // 핸들러
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

    // 액션
    onCheckEmail: () -> Unit,
    onCheckNick: () -> Unit,
    onSearchAddress: () -> Unit,

    // 파생 검증 상태 (ViewModel 계산값 전달)
    isPasswordRuleOk: Boolean,
    isPasswordConfirmMatch: Boolean,
    isPhoneStartsWith010: Boolean,
    isPhoneFormatOk: Boolean,

    emailReadOnly: Boolean = (mode == FormMode.EDIT),
) {
    val okColor = MainPurple
    val errColor = Color(0xFFD32F2F)

    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        // 이메일 (EDIT 모드에서는 읽기전용 + 중복검사 숨김)
        LabeledField(
            label = "이메일",
            value = email,
            onValueChange = if (mode == FormMode.EDIT) ({}) else onEmail,
            placeholder = "이메일 입력",
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                if (mode == FormMode.SIGNUP) {
                    emailMsg?.let { msg ->
                        Text(
                            msg,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (emailAvailable) okColor else errColor
                        )
                    }
                }
            },
            trailing = if (mode == FormMode.SIGNUP) {
                {
                    InlineActionButton(
                        text = if (emailCheckLoading) "확인중..." else "중복확인",
                        enabled = !emailCheckLoading && email.isNotBlank(),
                        onClick = onCheckEmail
                    )
                }
            } else null,
            readOnly = (mode == FormMode.EDIT),
            enabled = (mode != FormMode.EDIT)
        )

        // 비밀번호
        LabeledField(
            label = "비밀번호",
            value = password,
            onValueChange = onPassword,
            placeholder = "비밀번호 입력",
            visualTransformation = PasswordVisualTransformation(),
            supportingText = {
                if (password.isNotEmpty()) {
                    Text(
                        if (isPasswordRuleOk) "사용 가능한 비밀번호입니다."
                        else "영문+숫자/특수문자 포함 8자 이상으로 입력해주세요.",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isPasswordRuleOk) okColor else errColor
                    )
                }
            }
        )

        // 비밀번호 확인
        LabeledField(
            label = "비밀번호 확인",
            value = passwordConfirm,
            onValueChange = onPasswordConfirm,
            placeholder = "비밀번호 확인 입력",
            visualTransformation = PasswordVisualTransformation(),
            supportingText = {
                if (passwordConfirm.isNotEmpty()) {
                    Text(
                        if (isPasswordConfirmMatch) "비밀번호가 일치합니다."
                        else "비밀번호가 일치하지 않습니다.",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isPasswordConfirmMatch) okColor else errColor
                    )
                }
            }
        )

        // 닉네임
        LabeledField(
            label = "닉네임",
            value = nickname,
            onValueChange = onNick,
            placeholder = "닉네임 입력",
            supportingText = {
                nickMsg?.let { msg ->
                    Text(
                        msg,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (nickAvailable) okColor else errColor
                    )
                }
            },
            trailing = {
                Box(Modifier.padding(end = 6.dp)) {
                    InlineActionButton(
                        text = if (nickCheckLoading) "확인중..." else "중복확인",
                        enabled = !nickCheckLoading && nickname.isNotBlank(),
                        onClick = onCheckNick
                    )
                }
            }
        )

        // 이름
        LabeledField(
            label = "이름",
            value = memberName,
            onValueChange = onMemberName,
            placeholder = "이름 입력"
        )

        // 휴대폰
        LabeledField(
            label = "휴대폰 번호",
            value = phoneNumber,
            onValueChange = onPhone,
            placeholder = "휴대폰 번호 입력",
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
                            !isPhoneStartsWith010 || !isPhoneFormatOk -> errColor
                            else -> okColor
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