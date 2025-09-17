package com.refit.app.ui.screen

import android.graphics.Typeface
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.refit.app.R
import com.refit.app.data.health.HealthRepo
import com.refit.app.data.health.modelAndView.HealthViewModel
import com.refit.app.network.UserPrefs
import com.refit.app.ui.composable.health.GifCard
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.HealthConnectClient
import com.refit.app.data.product.modelAndView.RecommendationViewModel
import com.refit.app.ui.composable.health.chart.KcalChart
import com.refit.app.ui.composable.health.chart.KcalCompareChart
import com.refit.app.ui.composable.health.chart.StepChart
import com.refit.app.ui.composable.health.chart.StepCompareChart
import com.refit.app.ui.composable.health.chartHeader.InfoCallout
import com.refit.app.ui.composable.health.chartHeader.TitleLine
import com.refit.app.ui.composable.home.HomeProductRow
import com.refit.app.ui.composable.home.SectionHeader
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun StepsDetailScreen(
    navController: NavHostController,
    vm: HealthViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()
    val rows = uiState.rows
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        val allGranted = HealthRepo.readPerms.all { granted.contains(it) }
        if (allGranted) {
            vm.onPermissionGranted(context)
            vm.onDaysChanged(context, 7)
        }
    }

    val recommendVm: RecommendationViewModel = viewModel()
    val recommendState by recommendVm.state.collectAsState()

    LaunchedEffect(Unit) {
        val healthConnectClient = HealthConnectClient.getOrCreate(context)
        val granted = healthConnectClient.permissionController.getGrantedPermissions()
        if (HealthRepo.readPerms.all { granted.contains(it) }) {
            vm.onPermissionGranted(context)
            vm.onDaysChanged(context, 7)
        } else {
            permissionLauncher.launch(HealthRepo.readPerms)
        }
        recommendVm.loadRecommendations(type = 0, limit = 100)
    }

    val navyBlue = Color(red = 30, green = 60, blue = 114)
    val wineRed = Color(red = 150, green = 50, blue = 90)

    val pretendardBold: Typeface? = ResourcesCompat.getFont(context, R.font.pretendard_medium)

    // 일반인 평균 기준 값
    val koreanAvgSteps = 9611f
    val kcalAvg = 2350f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        val imageLoader = ImageLoader.Builder(context)
            .components {
                add(GifDecoder.Factory())
                add(ImageDecoderDecoder.Factory())
            }
            .build()

        val nickname = UserPrefs.getNickname() ?: "사용자"

        // ---------------- GIF 카드 ----------------
        GifCard(
            nickname = nickname,
            imageLoader = imageLoader,
            gifRes = R.raw.walking_jellbbo,
            message = buildAnnotatedString {
                append("${nickname}님의 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("주간 기록") }
                append("을 모았어요.\n오늘도 가볍게 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("한 걸음") }
                append(" 더해볼까요?")
            }
        )

        if (rows.isEmpty()) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
            return
        }

        val todaySteps = rows.lastOrNull()?.steps ?: 0L
        val todayKcal = rows.lastOrNull()?.totalKcal ?: 0.0

        val today = LocalDate.now()
        val days = (0..6).map { today.minusDays(it.toLong()) }.reversed()
        val xLabels = days.map {
            if (it == today) "오늘"
            else it.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN)
        }

        val sortedData = days.zip(rows.takeLast(7))
        val stepEntries = sortedData.mapIndexed { idx, pair ->
            BarEntry(
                idx.toFloat(),
                (pair.second.steps ?: 0L).toFloat()
            )
        }
        val kcalEntries = sortedData.mapIndexed { idx, pair ->
            BarEntry(
                idx.toFloat(),
                (pair.second.totalKcal ?: 0.0).toFloat()
            )
        }

        val maxSteps = stepEntries.maxOfOrNull { it.y } ?: 0f
        val maxKcal = kcalEntries.maxOfOrNull { it.y } ?: 0f

        val indexFormatter = IndexAxisValueFormatter(xLabels)

        var chart1Visible by remember { mutableStateOf(false) }
        var chart2Visible by remember { mutableStateOf(false) }
        var chart3Visible by remember { mutableStateOf(false) }
        var chart4Visible by remember { mutableStateOf(false) }

        Spacer(Modifier.height(30.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            // 1) 주간 걸음수 추이
            TitleLine(
                iconRes = R.drawable.ic_steps,
                title = "주간 걸음수 추이",
            )
            Spacer(Modifier.height(8.dp))
            InfoCallout(
                text = buildAnnotatedString {
                    append("지난 7일의 걸음 변화를 한눈에 볼 수 있어요.\n")
                    append("하루에 조금씩 ")
                    withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("꾸준히 걷기") }
                    append("가 ")
                    withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("혈액순환") }
                    append("과 피부 컨디션에 도움이 됩니다.")
                }
            )
            Spacer(Modifier.height(8.dp))
            StepChart(
                stepEntries,
                maxSteps,
                koreanAvgSteps,
                pretendardBold,
                indexFormatter,
                chart1Visible
            ) {
                chart1Visible = true
            }

            Spacer(Modifier.height(80.dp))

            // 2) 주간 칼로리 소모
            TitleLine(
                iconRes = R.drawable.ic_fire,
                title = "주간 칼로리 소모",
            )
            Spacer(Modifier.height(10.dp))
            InfoCallout(
                text = buildAnnotatedString {
                    append("일주일 동안의 ")
                    withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("에너지 소모") }
                    append("를 정리했어요.\n")
                    append("무리하지 않는 ")
                    withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("규칙적인 움직임") }
                    append("이 체중 관리와 피부 노화 완화에 도움돼요.")
                }
            )
            Spacer(Modifier.height(10.dp))
            KcalChart(
                kcalEntries,
                maxKcal,
                kcalAvg,
                pretendardBold,
                indexFormatter,
                chart2Visible
            ) {
                chart2Visible = true
            }

            Spacer(Modifier.height(80.dp))

            // 3) 오늘 걸음수 vs 평균
            TitleLine(
                iconRes = R.drawable.ic_compare,
                title = "오늘 걸음수 vs 평균"
            )
            Spacer(Modifier.height(6.dp))
            val stepCompareNote = if (todaySteps >= koreanAvgSteps) {
                buildAnnotatedString {
                    append("오늘은 평균보다 ")
                    withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("더 많이") }
                    append(" 걸으셨어요. 아주 잘하고 계세요!\n")
                    append("이 리듬을 유지하면 ")
                    withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("혈류 개선") }
                    append("과 피부 활력에 더 좋아요.")
                }
            } else {
                buildAnnotatedString {
                    append("오늘은 평균보다 ")
                    withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("조금 적게") }
                    append(" 걸으셨네요.\n")
                    append("잠깐의 ")
                    withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("가벼운 산책") }
                    append("만 더해도 금방 평균에 가까워져요.")
                }
            }

            InfoCallout(
                text = stepCompareNote
            )
            Spacer(Modifier.height(8.dp))
            StepCompareChart(todaySteps.toFloat(), koreanAvgSteps, chart3Visible) {
                chart3Visible = true
            }

            Spacer(Modifier.height(80.dp))

            // 4) 오늘 칼로리 vs 성별 평균
            TitleLine(
                iconRes = R.drawable.ic_compare,
                title = "오늘 칼로리 vs 성별 평균"
            )
            Spacer(Modifier.height(6.dp))
            val kcalCompareNote = if (todayKcal >= kcalAvg) {
                buildAnnotatedString {
                    append("오늘 소모량이 평균을 ")
                    withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("넘겼어요") }
                    append(". 컨디션 좋게 잘 관리 중이시네요!\n")
                    append("내일도 ")
                    withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("무리 없이") }
                    append(" 비슷한 페이스로 이어가보세요.")
                }
            } else {
                buildAnnotatedString {
                    append("오늘은 평균보다 소모량이 ")
                    withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) {
                        append(
                            "조금 덜"
                        )
                    }
                    append("했어요.\n")
                    append("짧은 ")
                    withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) {
                        append(
                            "유산소나 스트레칭"
                        )
                    }
                    append("으로 부담 없이 소모량을 올려볼까요?")
                }
            }

            InfoCallout(
                text = kcalCompareNote
            )
            Spacer(Modifier.height(8.dp))
            KcalCompareChart(todayKcal.toFloat(), navyBlue, wineRed, kcalAvg, chart4Visible) {
                chart4Visible = true
            }

            Spacer(Modifier.height(62.dp))

            // ---------------- 추천 상품 섹션 ----------------
            val recommendMsg = buildAnnotatedString {
                append(nickname)
                append("님을 위한 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) {
                    append("걸음수 맞춤 상품")
                }
            }

            SectionHeader(
                title = recommendMsg,
                onMore = {
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "recommendation_items",
                        recommendState.items
                    )
                    navController.navigate("recommendation/0")
                }
            )

            HomeProductRow(
                products = recommendState.items.take(10),
                onClick = { p -> navController.navigate("product/${p.id}") }
            )

            Spacer(Modifier.height(32.dp))

        }
    }
}
