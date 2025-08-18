package com.refit.app.ui.composable.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    Column(modifier = modifier) {

        // 우편번호 + 주소검색
        LabeledField(
            label = "우편번호",
            value = zipcode,
            onValueChange = onZip,
            placeholder = "우편번호",
            trailing = {
                InlineActionButton(
                    text = "주소검색",
                    onClick = onSearchAddress
                )
            }
        )

        Spacer(Modifier.height(8.dp))

        // 도로명 주소
        LabeledField(
            label = "도로명 주소",
            value = road,
            onValueChange = onRoad,
            placeholder = "도로명 주소"
        )

        Spacer(Modifier.height(8.dp))

        // 상세 주소
        LabeledField(
            label = "상세 주소",
            value = detail,
            onValueChange = onDetail,
            placeholder = "상세 주소"
        )
    }
}