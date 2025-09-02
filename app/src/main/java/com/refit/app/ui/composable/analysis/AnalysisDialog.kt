package com.refit.app.ui.composable.analysis

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.refit.app.R
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard

@Composable
fun AnalysisDialog(
    title: String,
    text: String,
    onDismiss: () -> Unit,
    // 옵션 버튼들 (없으면 닫기 버튼만)
    confirmText: String? = null,
    onConfirm: (() -> Unit)? = null,
    secondaryText: String? = null,
    onSecondary: (() -> Unit)? = null,
    // 아이콘 커스터마이즈
    iconRes: Int = R.drawable.ic_icon_upload,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                fontFamily = Pretendard,
            )
        },
        text = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                fontFamily = Pretendard,
            )
        },
        confirmButton = {
            if (secondaryText != null && onSecondary != null && confirmText != null && onConfirm != null) {
                // 보조 + 확인 두 개
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onSecondary,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEDE7F6),
                            contentColor = MainPurple
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                    ) { Text(secondaryText, maxLines = 1, textAlign = TextAlign.Center, fontFamily = Pretendard) }

                    Button(
                        onClick = onConfirm,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MainPurple,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                    ) { Text(confirmText, maxLines = 1, textAlign = TextAlign.Center, fontFamily = Pretendard) }
                }
            } else if (confirmText != null && onConfirm != null) {
                // 확인 한 개
                Button(
                    onClick = onConfirm,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainPurple,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) { Text(confirmText, maxLines = 1, textAlign = TextAlign.Center, fontFamily = Pretendard) }
            } else {
                // 닫기 한 개
                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainPurple,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) { Text("닫기", maxLines = 1, textAlign = TextAlign.Center, fontFamily = Pretendard) }
            }
        },
        dismissButton = null,
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White
    )
}
