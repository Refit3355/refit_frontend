package com.refit.app.ui.composable.myfit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.R
import com.refit.app.data.myfit.viewmodel.MyfitTab
import com.refit.app.data.myfit.viewmodel.MyfitUiState
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.DarkBlack
import com.refit.app.ui.theme.GreyText

@Composable
fun MyfitBanner(ui: MyfitUiState) {
    val purple = MainPurple
    val black = DarkBlack
    Column(
        Modifier.fillMaxWidth().padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val text = when (ui.tab) {
            MyfitTab.USING ->
                buildAnnotatedString {
                    withStyle(SpanStyle(color = purple)) {
                        append(ui.nickname)
                    }
                    withStyle(SpanStyle(color = black)) {
                        append("님, 등록한 제품의\n교체시기가 다가오고 있어요")
                    }
                }
            MyfitTab.COMPLETED ->
                buildAnnotatedString {
                    withStyle(SpanStyle(color = purple)) {
                        append(ui.nickname)
                    }
                    withStyle(SpanStyle(color = black)) {
                        append("님의 사용 완료한 제품이에요\n잘 맞았던 상품을 바탕으로 추천 받아보세요!")
                    }
                }
            MyfitTab.REGISTER ->
                buildAnnotatedString {
                    withStyle(SpanStyle(color = black)) {
                        append("리핏에서 구매한 상품이에요\n")
                    }
                    withStyle(SpanStyle(color = purple)) {
                        append("사용등록을 눌러 주기를 관리")
                    }
                    withStyle(SpanStyle(color = black)) {
                        append("해보세요")
                    }
                }
        }
        Spacer(Modifier.height(18.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            fontSize = 17.sp,
            lineHeight = 29.sp
        )
        if(ui.tab == MyfitTab.USING) {
            Spacer(Modifier.height(19.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_exclamation),
                    contentDescription = null,
                    tint = GreyText,
                    modifier = Modifier.size(13.dp)
                )

                Text(
                    text = "왼쪽으로 슬라이드해서 수정/삭제",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    color = GreyText
                )
            }
        }
    }
}

