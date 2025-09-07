package com.refit.app.ui.composable.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.ui.theme.Pretendard

// 간격/패딩
private val SECTION_H_PADDING = 16.dp
private val TITLE_V_PADDING   = 8.dp
private val CARD_RADIUS       = 16.dp
private val CARD_INNER_PAD    = 14.dp
private val SECTION_BOTTOM_SP = 18.dp
private val CHIP_GAP          = 8.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun IngredientSection(
    title: String,
    titleColor: Color,
    icon: @Composable () -> Unit,
    chips: List<String>?
) {
    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(horizontal = SECTION_H_PADDING, vertical = TITLE_V_PADDING),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.size(28.dp), contentAlignment = Alignment.Center) { icon() }
            Spacer(Modifier.width(8.dp))
            Text(
                text = title,
                color = titleColor,
                fontFamily = Pretendard,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Box(
            modifier = Modifier
                .padding(horizontal = SECTION_H_PADDING)
                .fillMaxWidth()
                .clip(RoundedCornerShape(CARD_RADIUS))
                .background(Color(0xFFF0F0F0))
                .padding(CARD_INNER_PAD)
        ) {
            val list = chips.orEmpty()
            if (list.isEmpty()) {

                Text("해당 성분이 없어요.", style = LocalTextStyle.current)
            } else {

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(CHIP_GAP),
                    verticalArrangement   = Arrangement.spacedBy(CHIP_GAP)
                ) {
                    list.forEach { IngredientChip(text = it) }
                }
            }
        }

        Spacer(Modifier.height(SECTION_BOTTOM_SP))
    }
}
