package com.refit.app.ui.composable.health

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.ImageLoader
import com.refit.app.ui.theme.LightPurple
import com.refit.app.ui.theme.Pretendard

@Composable
fun GifCard(
    nickname: String,
    imageLoader: ImageLoader,
    gifRes: Int,
    message: androidx.compose.ui.text.AnnotatedString
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(LightPurple)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .wrapContentWidth()
                .offset(x = (-8).dp)
        ) {
            AsyncImage(
                model = gifRes,
                contentDescription = null,
                imageLoader = imageLoader,
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    buildAnnotatedString {
                        append(message)
                    },
                    fontFamily = Pretendard,
                    fontWeight = FontWeight(500),
                    fontSize = 16.sp
                )
            }
        }
    }
}
