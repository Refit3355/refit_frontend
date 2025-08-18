package com.refit.app.ui.composable.product

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard

@Composable
fun GroupSegmented(
    selected: Group,
    onSelected: (Group) -> Unit,
    modifier: Modifier = Modifier
) {
    val containerShape = RoundedCornerShape(28.dp)
    val height = 40.dp
    val borderColor = Color(0xFFE0E0E0)
    val primary = MainPurple
    val pad = 4.dp

    Box(
        modifier
            .height(height)
            .fillMaxWidth()
            .clip(containerShape)
            .border(1.dp, borderColor, containerShape)
            .background(Color.White)
    ) {
        BoxWithConstraints(Modifier.padding(pad)) {
            val innerWidth = maxWidth
            val thumbWidth = innerWidth / 2
            val selectedIndex = if (selected == Group.BEAUTY) 0 else 1
            val targetOffset = thumbWidth * selectedIndex
            val offsetX by animateDpAsState(targetValue = targetOffset, label = "seg_offset")

            Box(
                Modifier
                    .offset(x = offsetX)
                    .width(thumbWidth)
                    .fillMaxHeight()
                    .clip(containerShape)
                    .background(primary)
            )

            Row(Modifier.fillMaxSize()) {
                SegButton(
                    text = Group.BEAUTY.label,
                    selected = selectedIndex == 0,
                    onClick = { onSelected(Group.BEAUTY) },
                    modifier = Modifier.weight(1f)
                )
                SegButton(
                    text = Group.HEALTH.label,
                    selected = selectedIndex == 1,
                    onClick = { onSelected(Group.HEALTH) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SegButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier
            .fillMaxHeight()
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else Color.Black,
            style = MaterialTheme.typography.labelLarge,
            fontFamily = Pretendard,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            letterSpacing = 0.sp
        )
    }
}
