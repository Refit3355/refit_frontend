package com.refit.app.ui.screen

import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.R
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard
import com.refit.app.ui.theme.RefitTheme

@Composable
fun AnalysisScreen() {
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
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // 제목
            Text(
                text = "사진 한 장으로,\n나에게 맞는 성분 확인하기",
                color = Color(0xFF6A1B9A),
                fontSize = 27.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                fontFamily = Pretendard,
                lineHeight = 34.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 설명
            Text(
                text = "더 빠르게, 더 안전하게, 더 똑똑하게\n나만의 제품을 선택할 수 있습니다.",
                color = Color.Gray,
                fontSize = 17.sp,
                textAlign = TextAlign.Center,
                fontFamily = Pretendard,
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "어떤 상품인가요?",
                fontSize = 20.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Bold,
                fontFamily = Pretendard,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 선택 상태 관리
            var selected by remember { mutableStateOf("뷰티") }

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { selected = "뷰티" },
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, if (selected == "뷰티") MainPurple else Color.Gray),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (selected == "뷰티") MainPurple.copy(alpha = 0.1f) else Color.Transparent,
                        contentColor = if (selected == "뷰티") MainPurple else Color.Gray
                    )
                ) {
                    Text("뷰티", fontFamily = Pretendard)
                }

                OutlinedButton(
                    onClick = { selected = "헬스" },
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, if (selected == "헬스") MainPurple else Color.Gray),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (selected == "헬스") MainPurple.copy(alpha = 0.1f) else Color.Transparent,
                        contentColor = if (selected == "헬스") MainPurple else Color.Gray
                    )
                ) {
                    Text("헬스", fontFamily = Pretendard)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 사진 업로드 버튼 통합
            PhotoUploadButton { uri ->
                uri?.let {
                    // Uri를 이용한 처리
                    println("선택된 이미지 URI: $it")
                }
            }

            Spacer(modifier = Modifier.height(50.dp))

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

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun PhotoUploadButton(onImageSelected: (Uri?) -> Unit) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }

    // 갤러리 선택
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> onImageSelected(uri) }

    // 카메라 촬영
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            onImageSelected(cameraImageUri)
        }
    }

    Button(
        onClick = { showDialog = true },
        modifier = Modifier
            .width(250.dp)
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF6A1B9A),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text = "+ 사진 업로드", fontSize = 17.sp, fontFamily = Pretendard)
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("이미지 선택") },
            text = {},
            confirmButton = {
                TextButton(onClick = {
                    galleryLauncher.launch("image/*")
                    showDialog = false
                }) { Text("갤러리") }
            },
            dismissButton = {
                TextButton(onClick = {
                    // 카메라용 Uri 생성
                    val resolver = context.contentResolver
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, "photo_${System.currentTimeMillis()}")
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    }
                    cameraImageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    cameraImageUri?.let { cameraLauncher.launch(it) }
                    showDialog = false
                }) { Text("카메라") }
            }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewIngredientAnalysisScreen() {
    RefitTheme {
        AnalysisScreen()
    }
}
