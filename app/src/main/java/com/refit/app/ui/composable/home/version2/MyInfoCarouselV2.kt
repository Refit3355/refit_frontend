package com.refit.app.ui.composable.home.version2

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.refit.app.R
import com.refit.app.ui.theme.MainPurple
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyInfoCarouselV2(
    nickname: String,
    steps: Long,
    sleepMinutes: Long,
    tempC: Int,
    autoScrollMillis: Long = 5000L,
    onStepsClick: () -> Unit = {},
    onSleepClick: () -> Unit = {},
    onWeatherClick: () -> Unit = {}
) {
    val pages = listOf<@Composable () -> Unit>(
        {
            StepsCard(
                nickname,
                5810L,
                mascot = { MascotGif(R.raw.walking_jellbbo, Modifier.size(120.dp)) },
                onClick = onStepsClick
            )
        },
        {
            SleepCard(
                sleepMinutes,
                mascot = { MascotGif(R.raw.sleeping_jellbbo, Modifier.size(120.dp)) },
                onClick = onSleepClick
                )
        },
        {
            TemperatureCard(
                tempC,
                mascot = { MascotGif(R.raw.weather_jellbbo, Modifier.size(120.dp)) },
                onClick = onWeatherClick
            )
        }
    )

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    // 자동 스크롤
    LaunchedEffect(pagerState.currentPage, pages.size) {
        if (pages.isEmpty()) return@LaunchedEffect
        kotlinx.coroutines.delay(autoScrollMillis)
        val next = (pagerState.currentPage + 1) % pages.size
        scope.launch {
            pagerState.animateScrollToPage(next)
        }
    }

    Column {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 25.dp),
            pageSpacing = 12.dp,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 300.dp)
        ) { page ->
            pages[page].invoke()
        }

        Spacer(Modifier.height(15.dp))

        // 인디케이터
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pages.size) { i ->
                val active = i == pagerState.currentPage
                Box(
                    Modifier
                        .padding(horizontal = 3.dp)
                        .height(6.dp)
                        .width(if (active) 18.dp else 6.dp)
                        .clip(CircleShape)
                        .background(if (active) MainPurple else Color(0xFFE0E0E0))
                )
            }
        }
    }
}
