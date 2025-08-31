package com.refit.app.ui.composable.mypage

import androidx.compose.foundation.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.refit.app.R
import com.refit.app.ui.theme.MainPurple

@Composable
fun CancelSuccessDialog(
    visible: Boolean,
    message: String,
    onConfirm: () -> Unit
) {
    if (!visible) return

    AlertDialog(
        icon = {
            Image(
                painter = painterResource(id = R.drawable.ic_exclamation_fill),
                contentDescription = null,
                modifier = Modifier.then(Modifier)
            )
        },
        onDismissRequest = { /* 바깥터치/백버튼 닫힘 금지 */ },
        containerColor = Color.White,
        title = {
            Text(
                text = message,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.Black
            )
        },
        text = null,
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