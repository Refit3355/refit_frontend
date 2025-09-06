package com.refit.app.ui.screen

import android.graphics.Typeface
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import com.refit.app.ui.composable.health.ChartHeader
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

    val pretendardBold: Typeface? = ResourcesCompat.getFont(context, R.font.pretendard_bold)

    // 일반인 평균 기준 값
    val koreanAvgSteps = 9611f
    val kcalAvg = 2350f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
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
                append("${nickname}님이 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) {
                    append("한 주 동안")
                }
                append("\n")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) {
                    append("걸어서")
                }
                append(" 해낸 결과에요!")
            }
        )

        if (rows.isEmpty()) {
            Text(
                "데이터가 없습니다.",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            )
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
        val stepEntries = sortedData.mapIndexed { idx, pair -> BarEntry(idx.toFloat(), (pair.second.steps ?: 0L).toFloat()) }
        val kcalEntries = sortedData.mapIndexed { idx, pair -> BarEntry(idx.toFloat(), (pair.second.totalKcal ?: 0.0).toFloat()) }

        val maxSteps = stepEntries.maxOfOrNull { it.y } ?: 0f
        val maxKcal = kcalEntries.maxOfOrNull { it.y } ?: 0f

        val indexFormatter = IndexAxisValueFormatter(xLabels)

        var chart1Visible by remember { mutableStateOf(false) }
        var chart2Visible by remember { mutableStateOf(false) }
        var chart3Visible by remember { mutableStateOf(false) }
        var chart4Visible by remember { mutableStateOf(false) }

        Spacer(Modifier.height(12.dp))

        // ---------------- 1번 차트 ----------------
        ChartHeader(
            iconRes = R.drawable.jellbbo_research,
            text = buildAnnotatedString {
                append("지난 일주일 동안의 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("걸음수") }
                append("를 확인했어요.\n")
                append("걷기는 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("혈액순환") }
                append("을 촉진해 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("피부 건강") }
                append("을 유지하는 데 도움을 줍니다.")
            }
        )
        StepChart(stepEntries, maxSteps, koreanAvgSteps, pretendardBold, chart1Visible) {
            chart1Visible = true
        }

        Spacer(Modifier.height(32.dp))

        // ---------------- 2번 차트 ----------------
        ChartHeader(
            iconRes = R.drawable.jellbbo_research,
            text = buildAnnotatedString {
                append("일주일간의 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("칼로리 소모량") }
                append("을 보여드릴게요!\n")
                append("꾸준한 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("에너지 소모") }
                append("는 체중 관리뿐만 아니라 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("피부 노화 억제") }
                append("에도 긍정적입니다.")
            }
        )
        KcalChart(kcalEntries, maxKcal, kcalAvg, pretendardBold, chart2Visible) {
            chart2Visible = true
        }

        Spacer(Modifier.height(32.dp))

        // ---------------- 3번 차트 ----------------
        ChartHeader(
            iconRes = R.drawable.jellbbo_research,
            text = buildAnnotatedString {
                append("오늘의 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("걸음수") }
                append("를 일반인 평균값과 비교해 보았어요.\n")
                if (todaySteps >= koreanAvgSteps) {
                    append("평균보다 많이 걸으면 피부 ")
                    withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("혈류 개선") }
                    append("에 도움이 됩니다.")
                } else {
                    append("평균보다 적게 걸으면 ")
                    withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("피부 활력") }
                    append("이 줄어들 수 있어요.")
                }
            }
        )
        StepCompareChart(todaySteps.toFloat(), koreanAvgSteps, chart3Visible) {
            chart3Visible = true
        }

        Spacer(Modifier.height(32.dp))

        // ---------------- 4번 차트 ----------------
        ChartHeader(
            iconRes = R.drawable.jellbbo_research,
            text = buildAnnotatedString {
                append("오늘의 ")
                withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("칼로리 소모") }
                append("를 성별 평균과 비교해 보았어요.\n")
                if (todayKcal >= kcalAvg) {
                    append("평균보다 많은 칼로리 소모는 피부 ")
                    withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("노화 억제") }
                    append("와 체지방 관리에 긍정적입니다.")
                } else {
                    append("평균보다 적은 칼로리 소모는 ")
                    withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) { append("피부 활력") }
                    append("이 떨어질 수 있어요.")
                }
            }
        )
        KcalCompareChart(todayKcal.toFloat(), navyBlue, wineRed, kcalAvg, chart4Visible) {
            chart4Visible = true
        }

        Spacer(Modifier.height(32.dp))

        // ---------------- 추천 상품 섹션 ----------------
        val recommendMsg = buildAnnotatedString {
            append(nickname)
            append("님을 위한 ")
            withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) {
                append("맞춤형 상품들 보러가기")
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
    }
}
