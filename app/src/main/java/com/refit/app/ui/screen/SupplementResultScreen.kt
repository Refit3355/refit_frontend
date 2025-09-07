package com.refit.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.R
import com.refit.app.data.analysis.modelAndView.UiResult
import com.refit.app.ui.composable.analysis.SectionCard
import com.refit.app.ui.composable.analysis.SupplementHeaderBanner
import com.refit.app.ui.theme.Pretendard

@Composable
fun SupplementResultScreen(data: UiResult.Supplement) {
    val summarySafe = data.summary.takeIf { it.isNotBlank() } ?: "요약 정보가 부족해요."
    val cautionSafe = data.cautionText?.takeIf { it.isNotBlank() }

    var largeText by rememberSaveable { mutableStateOf(false) }

    // ★ 화장품 화면과 동일한 본문 글자 크기/줄간격
    val baseTextStyle: TextStyle =
        if (largeText) MaterialTheme.typography.bodyLarge.copy(fontSize = 22.sp, lineHeight = 26.sp)
        else MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp, lineHeight = 21.sp)

    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        SupplementHeaderBanner(memberName = data.memberName)

        // 큰 글씨 토글
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "큰 글씨",
                style = MaterialTheme.typography.labelLarge,
                fontFamily = Pretendard
            )
            Switch(checked = largeText, onCheckedChange = { largeText = it })
        }

        Spacer(Modifier.height(24.dp))

        // ★ 본문 전체를 LocalTextStyle로 감싸서 글씨 크기만 통일 증대
        CompositionLocalProvider(LocalTextStyle provides baseTextStyle) {
            Column(Modifier.padding(horizontal = 20.dp)) {

                // 제목은 디자인 유지(고정), 본문은 LocalTextStyle을 따라감
                SectionCard(
                    title = "전체 요약",
                    titleColor = Color(0xFF5F0080),
                    titleSize = 18, // 제목은 기존 그대로
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_ai_analysis),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    body = summarySafe,
                    bodyColor = Color.DarkGray,
                    bodySize = -1 // ← 의미 없음(SectionCard에서 LocalTextStyle 우선)
                )

                Spacer(Modifier.height(36.dp))

                cautionSafe?.let { caution ->
                    SectionCard(
                        title = "주의 사항",
                        titleColor = Color(0xFFF86755),
                        titleSize = 18, // 제목 고정
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_ai_danger),
                                contentDescription = null,
                                tint = Color.Unspecified,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        body = caution,
                        bodyColor = Color.DarkGray,
                        bodySize = -1 // ← LocalTextStyle 우선
                    )
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SupplementResultScreenPreview() {
    val sampleData = UiResult.Supplement(
        memberName = "리핏",
        summary = "이 영양제는 피부 건강과 면역력에 도움을 줄 수 있습니다.",
        cautionText = "임산부나 수유부는 섭취 전 전문가와 상담하세요."
    )
    MaterialTheme { Surface { SupplementResultScreen(data = sampleData) } }
}
