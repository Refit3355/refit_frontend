package com.refit.app.ui.composable.productDetail

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.refit.app.ui.theme.Pretendard

@Composable
fun QuantityStepper(
    value: Int,
    onMinus: () -> Unit,
    onPlus: () -> Unit
) {
    Row(
        modifier = Modifier
            .height(40.dp)
            .width(120.dp)
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        StepBtn(text = "−", onClick = onMinus)
        Text(
            text = value.toString(),
            modifier = Modifier.width(36.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = Pretendard,
                fontWeight = FontWeight.Medium
            )
        )
        StepBtn(text = "+", onClick = onPlus)
    }
}

@Composable
private fun StepBtn(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(width = 48.dp, height = 40.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = Pretendard,
                fontWeight = FontWeight.Medium
            )
        )
    }
}
