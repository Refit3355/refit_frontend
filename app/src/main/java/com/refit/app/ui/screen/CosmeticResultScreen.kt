package com.refit.app.ui.screen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.saveable.rememberSaveable
import com.refit.app.R
import com.refit.app.data.analysis.modelAndView.UiResult
import com.refit.app.ui.composable.analysis.DetailTab
import com.refit.app.ui.composable.analysis.DetailTabs
import com.refit.app.ui.composable.analysis.IngredientParagraphSection
import com.refit.app.ui.composable.analysis.IngredientSection
import com.refit.app.ui.composable.analysis.MatchHeader
import com.refit.app.ui.composable.analysis.SummaryCard
@Composable
fun CosmeticResultScreen(data: UiResult.Cosmetic) {
    var tab by rememberSaveable { mutableStateOf(DetailTab.Detailed) }

    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        MatchHeader(memberName = data.memberName, matchRate = data.matchRate)
        DetailTabs(selected = tab, onSelected = { tab = it })
        Spacer(Modifier.height(12.dp))

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
        Spacer(Modifier.height(16.dp))
    }
}