package com.refit.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.refit.app.ui.composable.analysis.*
import com.refit.app.ui.composable.analysis.DetailTab
// 임시 UI 전용 상태, 코드 옮길 예정!

val Danger = Color(0xFFFF5C5C)
val Caution = Color(0xFFFFB020)
val Safe = Color(0xFF2AC3A2)
data class AnalysisUiState(
    val memberName: String = "",
    val matchRate: Int = 0,
    val risky: List<String> = emptyList(),
    val caution: List<String> = emptyList(),
    val safe: List<String> = emptyList(),
    val summary: String = "",
    val loading: Boolean = false,
    val error: String? = null
)

private const val SIMPLE_LIMIT = 5

@Composable
fun AnalysisResultScreen(
    ui: AnalysisUiState,
    onBack: () -> Unit = {}
) {
    var tab by rememberSaveable { mutableStateOf(DetailTab.Detailed) }

    Scaffold(
        Modifier.background(Color.White)
    ) { inner ->
        when {
            ui.loading -> {
                Box(Modifier.fillMaxSize().padding(inner), Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            ui.error != null -> {
                Box(Modifier.fillMaxSize().padding(inner), Alignment.Center) {
                    Text("에러: ${ui.error}")
                }
            }
            else -> {
                val risky = if (tab == DetailTab.Simple) ui.risky.take(SIMPLE_LIMIT) else ui.risky
                val caution = if (tab == DetailTab.Simple) ui.caution.take(SIMPLE_LIMIT) else ui.caution
                val safe = if (tab == DetailTab.Simple) ui.safe.take(SIMPLE_LIMIT) else ui.safe

                Column(
                    Modifier
                        .padding(inner)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    MatchHeader(memberName = ui.memberName, matchRate = ui.matchRate)
                    DetailTabs(selected = tab, onSelected = { tab = it })
                    Spacer(Modifier.height(12.dp))

                    IngredientSection(
                        title = "위험 성분",
                        titleColor = Danger,
                        icon = { Icon(Icons.Filled.Report, null, tint = Danger) },
                        chips = risky
                    )
                    IngredientSection(
                        title = "주의 성분",
                        titleColor = Caution,
                        icon = { Icon(Icons.Filled.WarningAmber, null, tint = Caution) },
                        chips = caution
                    )
                    IngredientSection(
                        title = "안심 성분",
                        titleColor = Safe,
                        icon = { Icon(Icons.Filled.CheckCircle, null, tint = Safe) },
                        chips = safe
                    )

                    SummaryCard(ui.summary)

                    Spacer(Modifier.height(44.dp))
                }
            }
        }
    }
}

/* 미리보기용 더미 */
@Preview(showBackground = true)
@Composable
private fun AnalysisResultScreenPreview() {
    val demo = AnalysisUiState(
        memberName = "외식고기",
        matchRate = 72,
        risky = listOf("파라벤", "포름알데히드", "트리클로산"),
        caution = listOf("프탈레이트", "벤조페논"),
        safe = listOf("글리세린", "히알루론산", "세라마이드", "나이아신아마이드", "토코페롤"),
        summary = "이 제품에는 피부에 자극을 줄 수 있는 성분(파라벤, 포름알데히드 등)과 조심해서 써야 하는 성분이 들어 있어요.\n하지만, 글리세린·히알루론산·세라마이드처럼 피부를 촉촉하게 하고 보호해 주는 좋은 성분도 많이 들어 있습니다.\n피부가 예민하다면, 얼굴 전체에 바르기 전에 팔 안쪽 등에 먼저 테스트해 보는 걸 추천드려요."
    )
    MaterialTheme(colorScheme = lightColorScheme()) {
        AnalysisResultScreen(ui = demo)
    }
}