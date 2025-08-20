package com.refit.app.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.refit.app.R
import com.refit.app.data.product.modelAndView.RecommendationViewModel
import com.refit.app.ui.composable.product.ProductGrid
import com.refit.app.ui.theme.Pretendard

@Composable
fun RecommendationScreen(
    navController: NavController,
    type: Int,
    vm: RecommendationViewModel = viewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()

    LaunchedEffect(type) {
        vm.loadRecommendations(type, 20)
    }

    val (bannerRes, bannerText) = when (type) {
        0 -> R.drawable.jellbbo_walk to "걸음 수가 많은 당신을 위한 피부 솔루션"
        1 -> R.drawable.jellbbo_sleep to "수면 패턴에 맞춘 뷰티 케어"
        2 -> R.drawable.jellbbo_sunny to "날씨에 맞춘 헤어 솔루션"
        3 -> R.drawable.jellbbo_doctor to "생활 리듬에 맞춘 건강 케어"
        else -> R.drawable.jellbbo_default to "추천 상품"
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 배너
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .padding(horizontal = 16.dp)
                .background(
                    color = androidx.compose.ui.graphics.Color(0xFFE9E4F2),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = bannerRes),
                    contentDescription = "배너 이미지",
                    modifier = Modifier.size(100.dp)
                )

                Spacer(Modifier.width(12.dp))

                Text(
                    text = bannerText,
                    style = MaterialTheme.typography.titleMedium.copy(fontFamily = Pretendard),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // 상품 그리드
        ProductGrid(
            navController = navController,
            items = state.items,
            isLoading = state.isLoading,
            hasMore = false,
            error = state.error,
            onLoadMore = {},
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
        )
    }
}
