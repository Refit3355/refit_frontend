package com.refit.app.ui.composable.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.ui.theme.Pretendard
import com.refit.app.R
import com.refit.app.ui.theme.MainPurple

@Composable
fun SectionHeader(
    title: AnnotatedString,
    onMore: () -> Unit,
    moreLabel: String = "더보기"
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 20.dp, end = 6.dp)
    ) {
        // 제목
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = Pretendard,
                fontSize = 17.sp
            ),
            modifier = Modifier.weight(1f)
        )

        // 더보기(텍스트 + 아이콘)
        TextButton(
            onClick = onMore,
            contentPadding = ButtonDefaults.TextButtonContentPadding
        ) {
            Text(
                text = moreLabel,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontFamily = Pretendard,
                    color = MainPurple
                )
            )
            Image(
                painter = painterResource(R.drawable.ic_arrow_more),
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
