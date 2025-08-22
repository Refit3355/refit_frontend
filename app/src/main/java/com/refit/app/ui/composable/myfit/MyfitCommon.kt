package com.refit.app.ui.composable.myfit

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import coil.compose.AsyncImage
import com.refit.app.R
import com.refit.app.ui.theme.GreyOutline
import com.refit.app.ui.theme.MainPurple

@Composable
fun MyfitThumb(url: String?, modifier: Modifier = Modifier, size: Dp = 80.dp) {
    val shape = RoundedCornerShape(10.dp)
    Box(
        modifier
            .size(size)
            .clip(shape)
            .border(1.dp, GreyOutline, shape)
    ) {
        AsyncImage(
            model = url,
            contentDescription = null,
            placeholder = painterResource(R.drawable.ic_product_placeholder),
            error = painterResource(R.drawable.ic_product_placeholder),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun MyfitProgressBar(
    value: Float,
    modifier: Modifier = Modifier,
    height: Dp = 16.dp
) {
    val shape = RoundedCornerShape(20)
    Box(
        modifier
            .height(height)
            .fillMaxWidth()
            .clip(shape)
            .border(0.5.dp, MainPurple, shape)
            .background(Color.White)
    ) {
        Box(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth(value.coerceIn(0f, 1f))
                .background(MainPurple)
        )
    }
}

@Composable
fun CircleOutlineIconButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    circleSize: Dp = 28.dp,
    strokeWidth: Dp = 1.5.dp,
    iconScale: Float = 0.58f             // 아이콘 비율(동그라미 대비)
) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .size(circleSize)
                .border(strokeWidth, MainPurple, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MainPurple,
                modifier = Modifier.size(circleSize * iconScale)
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(label, color = MainPurple, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
fun RecommendCircleButton(onClick: () -> Unit) {
    CircleOutlineIconButton(
        icon = Icons.AutoMirrored.Filled.ArrowForward,
        label = "추천받기",
        onClick = onClick
    )
}

@Composable
fun RegisterCircleButton(onClick: () -> Unit) {
    CircleOutlineIconButton(
        icon = Icons.Default.Add,
        label = "사용등록",
        onClick = onClick
    )
}