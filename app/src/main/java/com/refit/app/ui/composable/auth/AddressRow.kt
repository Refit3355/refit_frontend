package com.refit.app.ui.composable.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sin
import kotlin.math.truncate

@Composable
fun AddressRow(
    zipcode: String,
    onZip: (String) -> Unit,
    road: String,
    onRoad: (String) -> Unit,
    detail: String,
    onDetail: (String) -> Unit,
    onSearchAddress: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 각 입력란용 Modifier 준비
    val zipBringModifier   = bringIntoViewOnFocusModifier()
    val roadBringModifier  = bringIntoViewOnFocusModifier()
    val detailBringModifier= bringIntoViewOnFocusModifier()

    Column(modifier = modifier) {
        // 우편번호 + [주소검색] 버튼
        FieldWithSideButton(
            label = "우편번호",
            value = zipcode,
            onValueChange = onZip,
            placeholder = "우편번호",
            buttonText = "주소검색",
            buttonEnabled = true,
            onButtonClick = onSearchAddress,
            showButton = true,
            textFieldModifier = zipBringModifier          // ★ 추가
        )

        Spacer(Modifier.height(10.dp))

        // 도로명 주소
        FieldWithSideButton(
            label = "도로명 주소",
            value = road,
            onValueChange = onRoad,
            placeholder = "도로명 주소",
            showButton = false,
            textFieldModifier = roadBringModifier         // ★ 추가
        )

        Spacer(Modifier.height(10.dp))

        // 상세 주소
        FieldWithSideButton(
            label = "상세 주소",
            value = detail,
            onValueChange = onDetail,
            placeholder = "상세 주소",
            showButton = false,
            textFieldModifier = detailBringModifier       // ★ 추가
        )
    }
}
