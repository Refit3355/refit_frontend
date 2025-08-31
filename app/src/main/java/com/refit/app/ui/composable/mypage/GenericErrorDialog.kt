package com.refit.app.ui.composable.mypage

import androidx.compose.foundation.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.refit.app.R
import com.refit.app.ui.theme.MainPurple
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.painterResource

@Composable
fun PartialCancelErrorDialog(
    visible: Boolean,
    onConfirm: () -> Unit
) {
    if (!visible) return

    AlertDialog(
        icon = {
            Image(
                painter = painterResource(id = R.drawable.ic_exclamation_fill),
                contentDescription = null,
                modifier = Modifier
                    .then(Modifier)
            )
        },
        onDismissRequest = { /* 바깥터치/백버튼 닫힘 금지 */ },
        containerColor = Color.White,
        title = {
            Text(
                text = "요청이 정상적으로 처리되지 않았습니다.",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.Black
            )
        },
        text = null, // 상세 원인/코드 노출 안함
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MainPurple),
                content = { Text("확인", color = Color.White) }
            )
        },
        dismissButton = {}
    )
}