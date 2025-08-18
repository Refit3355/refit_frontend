package com.refit.app.ui.composable.product

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard

@Composable
fun CategoryTabs(
    tabs: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val scroll = rememberScrollState()

    Row(
        modifier
            .horizontalScroll(scroll)
            .padding(horizontal = 12.dp)
    ) {
        tabs.forEachIndexed { i, label ->
            val selected = i == selectedIndex

            var textWidthDp by remember(label) { mutableStateOf(0.dp) }
            val animatedWidth by animateDpAsState(
                targetValue = if (selected) textWidthDp else 0.dp,
                label = "tab_indicator_width"
            )

            Column(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .noRippleClickable { onSelect(i) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val density = LocalDensity.current
                Text(
                    text = label,
                    color = if (selected) MainPurple
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = Pretendard,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    onTextLayout = { result ->
                        textWidthDp = with(density) { result.size.width.toDp() }
                    }
                )

                Spacer(Modifier.height(4.dp))

                Box(
                    Modifier
                        .height(2.dp)
                        .width(animatedWidth)
                        .background(MainPurple)
                )
            }
        }
    }
}

// 선택: ripple 없는 클릭 헬퍼
private fun Modifier.noRippleClickable(onClick: () -> Unit) =
    this.then(Modifier.clickable(indication = null, interactionSource = androidx.compose.foundation.interaction.MutableInteractionSource()) { onClick() })
