package com.refit.app.ui.composable.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard

@Immutable
enum class DetailTab { Detailed, Simple }

@Composable
fun DetailTabs(
    selected: DetailTab,
    onSelected: (DetailTab) -> Unit
) {
    Column(Modifier.fillMaxWidth()) {

        // 두 탭을 반반 차지하게 배치
        Row(Modifier.fillMaxWidth()) {
            TabText(
                text = "단순하게",
                selected = selected == DetailTab.Detailed,
                onClick = { onSelected(DetailTab.Detailed) },
                modifier = Modifier.weight(1f),
            )
            TabText(
                text = "자세하게",
                selected = selected == DetailTab.Simple,
                onClick = { onSelected(DetailTab.Simple) },
                modifier = Modifier.weight(1f)
            )
        }

        Divider(color = Color(0xFFE6E0EB), thickness = 1.dp)

        Row(Modifier.fillMaxWidth()) {
            if (selected == DetailTab.Detailed) {
                Box(
                    Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(Color(0xFF6A1B9A))
                )
            } else {
                Spacer(Modifier.weight(1f))
            }

            if (selected == DetailTab.Simple) {
                Box(
                    Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(Color(0xFF6A1B9A))
                )
            } else {
                Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun TabText(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        Text(
            text = text,
            color = if (selected) MainPurple else Color.Gray,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
            ),
            fontFamily = Pretendard
        )
    }
}

