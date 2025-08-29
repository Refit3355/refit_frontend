package com.refit.app.ui.screen

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageProxy
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.refit.app.R
import com.refit.app.ui.composable.analysis.PhotoUploadButton
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard
import com.refit.app.ui.theme.RefitTheme
import java.io.ByteArrayOutputStream


@Composable
fun AnalysisScreen(navController: NavHostController) {
    val context = LocalContext.current
    
    var showCamera by remember { mutableStateOf(false) }
    var previewBytes by remember { mutableStateOf<ByteArray?>(null) }

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

            // 제목
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

            // 뷰티 or 헬스 선택
            var selected by remember { mutableStateOf("뷰티") }
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { selected = "뷰티" },
                    shape = RoundedCornerShape(24.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.linearGradient(listOf(if (selected == "뷰티") MainPurple else Color.Gray, if (selected == "뷰티") MainPurple else Color.Gray))
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (selected == "뷰티") MainPurple.copy(alpha = 0.1f) else Color.Transparent,
                        contentColor = if (selected == "뷰티") MainPurple else Color.Gray
                    )
                ) { Text("뷰티", fontFamily = Pretendard) }

                OutlinedButton(
                    onClick = { selected = "헬스" },
                    shape = RoundedCornerShape(24.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.linearGradient(listOf(if (selected == "헬스") MainPurple else Color.Gray, if (selected == "헬스") MainPurple else Color.Gray))
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (selected == "헬스") MainPurple.copy(alpha = 0.1f) else Color.Transparent,
                        contentColor = if (selected == "헬스") MainPurple else Color.Gray
                    )
                ) { Text("헬스", fontFamily = Pretendard) }
            }

            Spacer(Modifier.height(24.dp))

            // 갤러리 or 내부 카메라 연결
            PhotoUploadButton(
                onPickFromGallery = { uri ->
                    // 미리보기용 - 임시
                    previewBytes = uri?.let { u ->
                        context.contentResolver.openInputStream(u)?.use { it.readBytes() }
                    }
                },
                onOpenInAppCamera = { showCamera = true }
            )

            Spacer(Modifier.height(20.dp))

            // 미리보기 - 임시
            previewBytes?.let { bytes ->
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                if (bmp != null) {
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = "preview",
                        modifier = Modifier
                            .size(220.dp)
                            .padding(top = 8.dp)
                    )
                }
            }

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

        // 내부 카메라
        if (showCamera) {
            InAppCameraScreen(
                onCancel = { showCamera = false },
                onCroppedBytes = { bytes ->
                    // TODO: 여기서 bytes를 API로 업로드
                    previewBytes = bytes // 미리보기 업데이트 - 임시!
                    showCamera = false
                }
            )
        }
    }
}
