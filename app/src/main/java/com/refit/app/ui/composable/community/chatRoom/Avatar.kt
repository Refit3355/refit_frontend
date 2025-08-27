package com.refit.app.ui.composable.community.chatRoom

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.refit.app.R

@Composable
fun Avatar(
    profileUrl: String?,
    size: Int = 42,
    borderWidthDp: Int = 1
) {
    val context = LocalContext.current
    val model: Any = remember(profileUrl) {
        if (!profileUrl.isNullOrBlank()) profileUrl else R.drawable.ic_icon_chat
    }

    val borderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(model)
            .crossfade(true)
            .build(),
        contentDescription = null,
        placeholder = painterResource(R.drawable.ic_icon_chat),
        error = painterResource(R.drawable.ic_icon_chat),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .border(
                width = borderWidthDp.dp,
                color = borderColor,
                shape = CircleShape
            )
    )
}
