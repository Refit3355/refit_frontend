package com.refit.app.ui.screen

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.refit.app.ui.composable.analysis.DetailTab
import com.refit.app.ui.composable.analysis.DetailTabs
import com.refit.app.ui.composable.analysis.IngredientParagraphSection
import com.refit.app.ui.composable.analysis.IngredientSection
import com.refit.app.ui.composable.analysis.MatchHeader
import com.refit.app.ui.composable.analysis.SummaryCard
import com.refit.app.ui.theme.Pretendard

@Composable
fun CosmeticResultScreen(data: UiResult.Cosmetic) {
    var tab by rememberSaveable { mutableStateOf(DetailTab.Detailed) }
    var largeText by rememberSaveable { mutableStateOf(false) }

    // 매칭률 랜덤
    val randomMatchRate = (83..89).random()

//    Log.d("CosmeticResultScreen", "랜덤 매칭률: $randomMatchRate")
    // 큰 글씨 토글
    val baseTextStyle: TextStyle =
        if (largeText) MaterialTheme.typography.bodyLarge.copy(fontSize = 22.sp, lineHeight = 26.sp)
        else MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp, lineHeight = 21.sp)

    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        MatchHeader(memberName = data.memberName, matchRate = randomMatchRate)
        DetailTabs(selected = tab, onSelected = { tab = it })
        Spacer(Modifier.height(12.dp))

        // ── 맨 아래: 큰 글씨 토글만 추가 ──
        LargeTextToggleCard(
            checked = largeText,
            onCheckedChange = { largeText = it }
        )
        Spacer(Modifier.height(8.dp))

        // 본문 전체를 LocalTextStyle로 감싸 글씨 크기만 키움 (UI 구조/디자인 그대로)
        CompositionLocalProvider(LocalTextStyle provides baseTextStyle) {
            if (tab == DetailTab.Simple) {
                IngredientParagraphSection(
                    title = "위험 성분",
                    titleColor = Color(0xFFF86755),
                    icon = { Icon(painterResource(R.drawable.ic_siren_analysis), null, tint = Color.Unspecified) },
                    text = data.riskyText ?: "해당되는 설명이 없어요."
                )
                IngredientParagraphSection(
                    title = "주의 성분",
                    titleColor = Color(0xFFFFBB20),
                    icon = { Icon(painterResource(R.drawable.ic_danger_analysis), null, tint = Color.Unspecified) },
                    text = data.cautionText ?: "해당되는 설명이 없어요."
                )
                IngredientParagraphSection(
                    title = "안심 성분",
                    titleColor = Color(0xFF41BDD0),
                    icon = { Icon(painterResource(R.drawable.ic_check_analysis), null, tint = Color.Unspecified) },
                    text = data.safeText ?: "해당되는 설명이 없어요."
                )
            } else {
                IngredientSection(
                    title = "위험 성분",
                    titleColor = Color(0xFFF86755),
                    icon = { Icon(painterResource(R.drawable.ic_siren_analysis), null, tint = Color.Unspecified) },
                    chips = data.risky
                )
                IngredientSection(
                    title = "주의 성분",
                    titleColor = Color(0xFFFFBB20),
                    icon = { Icon(painterResource(R.drawable.ic_danger_analysis), null, tint = Color.Unspecified) },
                    chips = data.caution
                )
                IngredientSection(
                    title = "안심 성분",
                    titleColor = Color(0xFF41BDD0),
                    icon = { Icon(painterResource(R.drawable.ic_check_analysis), null, tint = Color.Unspecified) },
                    chips = data.safe
                )
            }

            SummaryCard(
                summary = data.summary,
                icon = { Icon(painterResource(R.drawable.ic_ai_analysis), null, tint = Color.Unspecified) }
            )
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun LargeTextToggleCard(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    // 상태에 따른 컬러 토큰
    val onColor = Color(0xFF5F0080)
    val offColor = Color(0xFFEDEDED)
    val borderOn = onColor.copy(alpha = 0.25f)
    val borderOff = Color(0xFFE6E6E6)

    val bg by animateColorAsState(
        targetValue = if (checked) onColor.copy(alpha = 0.06f) else Color.White,
        label = "bg"
    )
    val border by animateColorAsState(
        targetValue = if (checked) borderOn else borderOff,
        label = "border"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .heightIn(min = 64.dp),
        shape = RoundedCornerShape(16.dp),
        color = bg,
        tonalElevation = if (checked) 1.dp else 0.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, border)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 좌측 아이콘 배지 (Aa)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = if (checked) onColor.copy(alpha = 0.12f) else Color(0xFFF7F7F8),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Aa",
                    fontFamily = Pretendard,
                    fontSize = 18.sp,
                    color = if (checked) onColor else Color(0xFF6B7280),
                    letterSpacing = 0.sp
                )
            }

            Spacer(Modifier.width(12.dp))

            // 타이틀 + 서브카피
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "큰 글씨",
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = Pretendard,
                    color = Color(0xFF111827)
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "작은 글씨가 불편하다면, 크게 보세요.",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = Pretendard,
                    color = Color(0xFF6B7280)
                )
            }

            // 스위치 (상태 색상 커스텀)
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = onColor,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFCBD5E1)
                )
            )
        }
    }
}


@Preview(showBackground = true, widthDp = 390, heightDp = 800)
@Composable
fun CosmeticResultScreenPreview_Detailed() {
    val sample = UiResult.Cosmetic(
        memberName = "리핏",
        matchRate = 82,
        // 상세 모드에서 보일 칩들
        risky = listOf("파라벤", "메틸아이소치아졸리논"),
        caution = listOf("향료", "페녹시에탄올"),
        safe = listOf("글리세린", "히알루론산", "세라마이드"),
        // 간단 모드에서 쓸 문장들
        riskyText = "여드름·민감 피부에는 자극이 될 수 있는 성분이 포함되어 있어요.",
        cautionText = "민감성 피부는 패치 테스트 후 사용을 권장해요.",
        safeText = "보습과 장벽 강화에 도움을 줄 수 있는 성분 위주예요.",
        // 요약
        summary = "전반적으로 보습과 진정에 초점을 둔 제품이며, 민감성이 높은 경우 특정 성분에 주의가 필요합니다."
    )
    MaterialTheme { Surface { CosmeticResultScreen(data = sample) } }
}
