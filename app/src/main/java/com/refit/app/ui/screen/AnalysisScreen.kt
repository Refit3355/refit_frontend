package com.refit.app.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.refit.app.R
import com.refit.app.data.analysis.modelAndView.AnalysisViewModel
import com.refit.app.data.analysis.modelAndView.UiResult
import com.refit.app.data.analysis.modelAndView.rememberAnalysisViewModel
import com.refit.app.ui.composable.analysis.AnalysisDialog
import com.refit.app.ui.composable.analysis.LoadingOverlay
import com.refit.app.ui.composable.analysis.PhotoUploadButton
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard

@Composable
fun AnalysisScreen(
    navController: NavHostController,
    vm: AnalysisViewModel = rememberAnalysisViewModel()
) {
    val result by vm.result

    var showCamera by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf("뷰티") } // "뷰티" | "헬스"

    LaunchedEffect(result) {
        when (val r = result) {
            is UiResult.Cosmetic ->
                if (!r.isEmptyResult()) {
                    navController.navigate("ingredient/result") { launchSingleTop = true }
                }
            is UiResult.Supplement ->
                if (!r.isEmptyResult()) {
                    navController.navigate("ingredient/result") { launchSingleTop = true }
                }
            else -> Unit
        }
    }

    var showFailDialog by remember { mutableStateOf(false) }
    LaunchedEffect(result) {
        showFailDialog = when (val r = result) {
            is UiResult.Error      -> true
            is UiResult.Cosmetic   -> r.isEmptyResult()
            is UiResult.Supplement -> r.isEmptyResult()
            else -> false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFFFFF),
                        Color(0xFFD7BFDF),
                        Color(0xFFD1ABE1)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(Modifier.height(15.dp))

            Text(
                text = "사진 한 장으로,\n나에게 맞는 성분 확인하기",
                color = Color(0xFF6A1B9A),
                fontSize = 27.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                fontFamily = Pretendard,
                lineHeight = 30.sp
            )

            Spacer(Modifier.height(14.dp))

            Text(
                text = "더 빠르게, 더 안전하게, 더 똑똑하게\n나만의 제품을 선택할 수 있습니다.",
                color = Color.Gray,
                fontSize = 17.sp,
                textAlign = TextAlign.Center,
                fontFamily = Pretendard,
            )

            Spacer(Modifier.height(32.dp))

            Text(
                text = "어떤 상품인가요?",
                fontSize = 20.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Bold,
                fontFamily = Pretendard,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { selected = "뷰티" },
                    shape = RoundedCornerShape(24.dp),
                    border = ButtonDefaults.outlinedButtonBorder,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (selected == "뷰티") MainPurple.copy(alpha = 0.1f) else Color.Transparent,
                        contentColor = if (selected == "뷰티") MainPurple else Color.Gray
                    )
                ) { Text("뷰티", fontFamily = Pretendard) }

                OutlinedButton(
                    onClick = { selected = "헬스" },
                    shape = RoundedCornerShape(24.dp),
                    border = ButtonDefaults.outlinedButtonBorder,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (selected == "헬스") MainPurple.copy(alpha = 0.1f) else Color.Transparent,
                        contentColor = if (selected == "헬스") MainPurple else Color.Gray
                    )
                ) { Text("헬스", fontFamily = Pretendard) }
            }

            Spacer(Modifier.height(24.dp))

            PhotoUploadButton(
                onPickFromGallery = { uri ->
                    if (uri != null) {
                        vm.analyzeFromUri(uri, selected)
                    }
                },
                onOpenInAppCamera = { showCamera = true }
            )

            Spacer(Modifier.height(30.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_icon_analysis),
                    contentDescription = null,
                    modifier = Modifier.size(300.dp)
                )
            }

            Spacer(Modifier.height(16.dp))
        }

        if (showCamera) {
            InAppCameraScreen(
                onCancel = { showCamera = false },
                onCroppedBytes = { bytes ->
                    showCamera = false
                    vm.analyzeFromBytes(bytes, selected)
                }
            )
        }

        // 로딩/에러 UI
        val loading = result is UiResult.Loading
        val error = (result as? UiResult.Error)?.msg

        val hostState = remember { SnackbarHostState() }
        if (error != null) {
            LaunchedEffect(error) { hostState.showSnackbar(error) }
        }
        if (loading) {
            LoadingOverlay(visible = true)
        }
        SnackbarHost(hostState = hostState)

        // 실패 다이얼로그 (아이콘 원하는 걸로 지정)
        if (showFailDialog) {
            AnalysisDialog(
                title = "분석 실패",
                text = "이미지를 인식하지 못했어요.\n다시 시도해 주세요!",
                iconRes = R.drawable.ic_danger_analysis,
                onDismiss = { showFailDialog = false }
            )
        }
    }
}

// Cosmetic: 리스트 3개 모두 비고 + 설명문 3개 비고 + 요약 비면 빈 결과로 간주
private fun UiResult.Cosmetic.isEmptyResult(): Boolean {
    val listsEmpty = risky.isEmpty() && caution.isEmpty() && safe.isEmpty()
    val textsBlank = riskyText.isNullOrBlank() && cautionText.isNullOrBlank() && safeText.isNullOrBlank()
    val summaryBlank = summary.isBlank()
    // 필요하면 + (matchRate == 0) 도 추가 가능
    return listsEmpty && textsBlank && summaryBlank
}

// Supplement: summary 비고 + cautionText 비면 빈 결과
private fun UiResult.Supplement.isEmptyResult(): Boolean {
    return summary.isBlank() && cautionText.isNullOrBlank()
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AnalysisScreenPreview() { }
