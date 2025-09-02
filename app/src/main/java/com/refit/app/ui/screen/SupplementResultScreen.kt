package com.refit.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.refit.app.R
import com.refit.app.data.analysis.modelAndView.UiResult
import com.refit.app.ui.composable.analysis.SectionCard
import com.refit.app.ui.composable.analysis.SupplementHeaderBanner

@Composable
fun SupplementResultScreen(data: UiResult.Supplement) {
    val summarySafe = data.summary?.takeIf { it.isNotBlank() } ?: "요약 정보가 부족해요."
    val cautionSafe = data.cautionText?.takeIf { it.isNotBlank() }

    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        SupplementHeaderBanner(memberName = data.memberName)

        Spacer(Modifier.height(50.dp))
        Column(Modifier.padding(horizontal = 20.dp)) {

            SectionCard(
                title = "전체 요약",
                titleColor = Color(0xFF5F0080),
                titleSize = 18,
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
                bodySize = 15
            )

            Spacer(Modifier.height(60.dp))

            cautionSafe?.let { caution ->
                SectionCard(
                    title = "주의 사항",
                    titleColor = Color(0xFFF86755),
                    titleSize = 18,
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
                    bodySize = 15
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
