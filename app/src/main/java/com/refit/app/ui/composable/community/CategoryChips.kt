package com.refit.app.ui.composable.community

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard

enum class CommunityCategory(val label: String) {
    ALL("전체"), BEAUTY("뷰티"), HEALTH("헬스")
}

@Composable
fun CategoryChips(
    selected: CommunityCategory,
    onSelect: (CommunityCategory) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        CommunityCategory.values().forEach { category ->
            val isSelected = category == selected
            Surface(
                color = if (isSelected) MainPurple else Color.Transparent,
                shape = RoundedCornerShape(50),
                border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.2.dp, Color.Gray),
                onClick = { onSelect(category) }
            ) {
                Text(
                    text = category.label,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                    color = if (isSelected) Color.White else Color.Black,
                    fontSize = 15.sp,
                    fontFamily = Pretendard
                )
            }
        }
    }
}
