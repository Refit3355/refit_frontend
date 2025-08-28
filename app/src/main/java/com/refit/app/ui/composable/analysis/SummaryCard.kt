package com.refit.app.ui.composable.analysis

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard


@Composable
fun SummaryCard(text: String) {
    Column(Modifier.fillMaxWidth().padding(20.dp)) {
        Row {
            Icon(Icons.Filled.AutoAwesome, null, tint = MainPurple); Spacer(Modifier.width(8.dp))
            Text("전체 요약", color = MainPurple, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
        }
        Spacer(Modifier.height(8.dp))
        Surface(shape = RoundedCornerShape(16.dp), color = Color(0xFFF7F2FF)) {
            Text(text, Modifier.padding(16.dp), fontSize = 15.sp, lineHeight = 22.sp, fontFamily = Pretendard)
        }
    }
}