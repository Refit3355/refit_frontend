package com.refit.app.ui.composable.myfit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.refit.app.R
import com.refit.app.ui.theme.GreyOutline
import kotlin.math.max

@Composable
fun MyfitThumb(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    corner: Dp = 12.dp,
    borderWidth: Dp = 0.5.dp,
    borderColor: Color = GreyOutline
) {
    val shape = RoundedCornerShape(corner)
    val placeholderRes = R.drawable.ic_product_placeholder

    Box(
        modifier = modifier
            .aspectRatio(1f)                 // 1:1 고정
            .clip(shape)                          // 모서리
            .background(Color.White)
            .border(borderWidth, borderColor, shape)
            .drawWithContent {
                drawContent()
                // px 계산 (최소 1px 보장)
                val strokePx = max(1f, with(density) { borderWidth.toPx() })
                val inset = strokePx / 2f
                val r = with(density) { corner.toPx() }
                drawRoundRect(
                    color = borderColor,
                    topLeft = Offset(inset, inset),
                    size = Size(size.width - strokePx, size.height - strokePx),
                    cornerRadius = CornerRadius(r, r),
                    style = Stroke(width = strokePx)
                )
            },
        contentAlignment = Alignment.Center
    ) {
        val data = imageUrl?.takeIf { it.isNotBlank() }

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Inside,
            alignment = Alignment.Center,

            placeholder = painterResource(placeholderRes),
            error = painterResource(placeholderRes),
            fallback = painterResource(placeholderRes) // model == null일 때
        )
    }
}