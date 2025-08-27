package com.refit.app.ui.composable.analysis

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi


@OptIn(ExperimentalLayoutApi::class, ExperimentalLayoutApi::class)
@Composable
fun IngredientSection(
    title: String,
    titleColor: Color,
    icon: @Composable () -> Unit,
    chips: List<String>
) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)) {
        Row {
            icon(); Spacer(Modifier.width(8.dp))
            Text(title, color = titleColor, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
        }
        Spacer(Modifier.height(8.dp))
        Surface(color = Color(0xFFF2F3F5), shape = RoundedCornerShape(16.dp)) {
            FlowRow(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp), // ✅ 변경
                verticalArrangement = Arrangement.spacedBy(8.dp)    // ✅ 변경
            ) {
                if (chips.isEmpty()) {
                    Text("해당되는 성분이 없어요", color = Color.Gray, modifier = Modifier.padding(4.dp))
                } else {
                    chips.forEach { IngredientChip(it) }
                }
            }
        }
    }
}