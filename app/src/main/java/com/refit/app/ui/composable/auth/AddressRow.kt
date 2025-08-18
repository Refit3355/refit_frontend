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
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = zipcode, onValueChange = onZip,
                label = {Text("우편번호")},
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            Button (
                onClick = onSearchAddress,
                modifier = Modifier.width(120.dp)
            ) {Text("주소검색")}
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = road, onValueChange = onRoad,
            label = { Text("도로명 주소") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = detail, onValueChange = onDetail,
            label = { Text("상세 주소") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}