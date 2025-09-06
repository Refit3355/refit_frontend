package com.refit.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.refit.app.R
import com.refit.app.data.product.model.Product
import com.refit.app.network.UserPrefs
import com.refit.app.ui.composable.health.GifCard
import com.refit.app.ui.composable.product.ProductGrid
import com.refit.app.ui.theme.MainPurple

@Composable
fun RecommendationScreen(
    navController: NavController,
    type: Int
) {
    val products = remember {
        navController.previousBackStackEntry
            ?.savedStateHandle
            ?.get<List<Product>>("recommendation_items")
            ?: emptyList()
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            add(GifDecoder.Factory())
            add(ImageDecoderDecoder.Factory())
        }
        .build()

    val nickname = UserPrefs.getNickname() ?: "사용자"

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // ---------------- GIF 카드 ----------------
        when (type) {
            0 -> {
                // 걸음 수 기반 추천
                GifCard(
                    nickname = nickname,
                    imageLoader = imageLoader,
                    gifRes = R.raw.walking_jellbbo,
                    message = buildAnnotatedString {
                        append("${nickname}님의 ")
                        withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) {
                            append("발걸음 속도")
                        }
                        append("에\n")
                        append("맞춘 추천 상품들이에요.")
                    }
                )
            }

            1 -> {
                // 수면 기반 추천
                GifCard(
                    nickname = nickname,
                    imageLoader = imageLoader,
                    gifRes = R.raw.sleeping_jellbbo,
                    message = buildAnnotatedString {
                        append("${nickname}님의 ")
                        withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) {
                            append("수면 시간")
                        }
                        append("을 고려한\n")
                        append("상품들을 준비해 보았어요.")
                    }
                )
            }

            2 -> {
                // 날씨 기반 추천
                GifCard(
                    nickname = nickname,
                    imageLoader = imageLoader,
                    gifRes = R.raw.weather_jellbbo,
                    message = buildAnnotatedString {
                        append("오늘의 ")
                        withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) {
                            append("날씨")
                        }
                        append("에 필요한\n")
                        append("상품들을 만나보세요!")
                    }
                )
            }

            3 -> {
                // 생활 리듬 기반 추천
                GifCard(
                    nickname = nickname,
                    imageLoader = imageLoader,
                    gifRes = R.raw.rhythming_jellbbo,
                    message = buildAnnotatedString {
                        append("${nickname}님의 ")
                        withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) {
                            append("생활 리듬")
                        }
                        append("을 고려한\n")
                        append("건강 관리 솔루션이에요.")
                    }
                )
            }

            else -> {
                // 기본: 수면 카드 재사용
                GifCard(
                    nickname = nickname,
                    imageLoader = imageLoader,
                    gifRes = R.raw.sleeping_jellbbo,
                    message = buildAnnotatedString {
                        append("${nickname}님을 위한 ")
                        withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) {
                            append("맞춤형 추천")
                        }
                        append("을 준비했어요.\n")
                        append("필요한 케어 제품을 확인해보세요!")
                    }
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // 상품 그리드
        ProductGrid(
            navController = navController,
            items = products,
            isLoading = false,
            hasMore = false,
            error = null,
            onLoadMore = {},
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
        )
    }
}
