package com.refit.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import com.refit.app.R
import com.refit.app.ui.composable.analysis.*
import com.refit.app.ui.composable.analysis.DetailTab

val Danger = Color(0xFFFF5C5C)
val Caution = Color(0xFFFFB020)
val Safe   = Color(0xFF2AC3A2)

data class AnalysisUiState(
    val memberName: String = "",
    val matchRate: Int = 0,

    // 자세하게(칩)용
    val risky: List<String> = emptyList(),
    val caution: List<String> = emptyList(),
    val safe: List<String> = emptyList(),

    // 단순하게(문단)용 — 칩과 별개 데이터!
    val riskyText: String = "",
    val cautionText: String = "",
    val safeText: String = "",

    val summary: String = "",
    val loading: Boolean = false,
    val error: String? = null,
)

private const val SIMPLE_LIMIT = 5

@Composable
fun AnalysisResultScreen(
    ui: AnalysisUiState,
    onBack: () -> Unit = {}
) {
    var tab by rememberSaveable { mutableStateOf(DetailTab.Detailed) }

    when {
        ui.loading -> {
            Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
        }
        ui.error != null -> {
            Box(Modifier.fillMaxSize(), Alignment.Center) { Text("에러: ${ui.error}") }
        }
        else -> {
            val risky   = ui.risky
            val caution = ui.caution
            val safe    = ui.safe

            Column(
                Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .verticalScroll(rememberScrollState())
            ) {
                MatchHeader(memberName = ui.memberName, matchRate = ui.matchRate)

                DetailTabs(selected = tab, onSelected = { tab = it })
                Spacer(Modifier.height(12.dp))

                if (tab == DetailTab.Simple) {
                    // 단순하게: 설명형 문단 사용 (칩 X)
                    IngredientParagraphSection(
                        title = "위험 성분",
                        titleColor = Color(0xFFF86755),
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_siren_analysis),
                                contentDescription = null,
                                tint = Color.Unspecified
                            )
                        },
                        text = ui.riskyText.ifBlank { "해당되는 설명이 없어요." }
                    )
                    IngredientParagraphSection(
                        title = "주의 성분",
                        titleColor = Color(0xFFFFBB20),
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_danger_analysis),
                                contentDescription = null,
                                tint = Color.Unspecified
                            )
                        },
                        text = ui.cautionText.ifBlank { "해당되는 설명이 없어요." }
                    )
                    IngredientParagraphSection(
                        title = "안심 성분",
                        titleColor = Color(0xFF41BDD0),
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_check_analysis),
                                contentDescription = null,
                                tint = Color.Unspecified
                            )
                        },
                        text = ui.safeText.ifBlank { "해당되는 설명이 없어요." }
                    )
                } else {
                    // 자세하게: 칩 리스트 사용 (필요시 SIMPLE_LIMIT로 미리보기 제한 가능)
                    IngredientSection(
                        title = "위험 성분",
                        titleColor = Color(0xFFF86755),
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_siren_analysis),
                                contentDescription = null,
                                tint = Color.Unspecified
                            )
                        },
                        chips = risky
                    )
                    IngredientSection(
                        title = "주의 성분",
                        titleColor = Color(0xFFFFBB20),
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_danger_analysis),
                                contentDescription = null,
                                tint = Color.Unspecified
                            )
                        },
                        chips = caution
                    )
                    IngredientSection(
                        title = "안심 성분",
                        titleColor = Color(0xFF41BDD0),
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_check_analysis),
                                contentDescription = null,
                                tint = Color.Unspecified
                            )
                        },
                        chips = safe
                    )
                }

                SummaryCard(
                    summary = ui.summary,
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_ai_analysis),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                    }
                )

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

/* 미리보기 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AnalysisResultScreenPreview() {
    val demo = AnalysisUiState(
        memberName = "외식고기",
        matchRate = 72,
        risky = listOf("파라벤", "포름알데히드", "트리클로산", "톨루엔", "프탈레이트"),
        caution = listOf("벤조페논", "메틸파라벤", "옥시벤존"),
        safe = listOf("글리세린", "히알루론산", "세라마이드", "나이아신아마이드", "토코페롤", "판테놀"),
        riskyText = "민감 피부는 자극·홍조가 나타날 수 있어요. 처음엔 소량으로 국소 테스트 후 사용해 주세요.",
        cautionText = "피부 컨디션에 따라 건조감/당김이 생길 수 있어요. 주 2~3회부터 천천히 횟수를 늘려 보세요.",
        safeText = "보습·장벽 강화에 도움을 주는 성분들이에요. 일상적인 사용에 무난하며 건성/복합성에 특히 적합해요.",
        summary = "자극 가능 성분이 일부 있으나 보습/장벽 강화 성분이 균형을 잡아줍니다. 민감 피부는 패치 테스트 후 사용을 권장합니다."
    )
    MaterialTheme(colorScheme = lightColorScheme()) {
        AnalysisResultScreen(ui = demo)
    }
}
