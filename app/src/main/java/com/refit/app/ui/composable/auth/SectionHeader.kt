package com.refit.app.ui.composable.auth

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.ui.composable.common.topbar.TopBarTokens.Icon
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard

@Composable
fun SectionHeader(
    title: String,
    color: Color = MainPurple,
    @DrawableRes iconResId: Int? = null,
    iconTint: Color = MainPurple,
    iconSize: Dp? = null,
    spaceBetween: Dp = 8.dp
) {
    val textStyle = MaterialTheme.typography.titleMedium
    val autoIconSize = with(LocalDensity.current) {
        textStyle.lineHeight.toDp()
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        if (iconResId != null) {
            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(iconSize ?: autoIconSize)
            )
            Spacer(Modifier.width(spaceBetween))
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            ),
            color = color,
            fontFamily = Pretendard,
        )
    }
}