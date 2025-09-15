package com.refit.app.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.refit.app.R
import com.refit.app.data.analysis.modelAndView.AnalysisViewModel
import com.refit.app.data.analysis.modelAndView.UiResult
import com.refit.app.ui.composable.analysis.CuteLoadingOverlay
import com.refit.app.ui.composable.analysis.AnalysisDialog
import com.refit.app.ui.composable.analysis.EmptyStateCard
import com.refit.app.ui.composable.analysis.ErrorCard

@Composable
fun ResultRouterScreen(
    vm: AnalysisViewModel
) {
    when (val ui = vm.result.value) {
        UiResult.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
            CuteLoadingOverlay(
                visible = true,
                gifResId = R.raw.ic_loading_analysis,
                gifSize = 220.dp,
                messages = listOf(
                    "Refit AI가 분석 중이에요",
                    "당신에게 맞는 성분을 찾고 있어요",
                    "조금만 기다려 주세요",
                    "거의 다 됐어요!"
                )
            )
        }
        is UiResult.Error -> ErrorCard(ui.msg)
        UiResult.Empty -> EmptyStateCard(
            title = "정보를 추출하지 못했어요",
            body = "라벨이 흐리거나 각도가 기울면 인식이 어려울 수 있어요.\n정면, 밝은 조명에서 다시 시도해 주세요."
        )
        is UiResult.Cosmetic -> CosmeticResultScreen(ui)
        is UiResult.Supplement -> SupplementResultScreen(ui)
        is UiResult.Blocked -> {
            AnalysisDialog(
                title = ui.title,
                text = ui.message,
                iconRes = R.drawable.ic_danger_analysis,
                onDismiss = { vm.acknowledgeBlocked() }
            )
        }
    }
}
