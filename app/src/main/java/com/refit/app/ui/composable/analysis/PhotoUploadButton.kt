package com.refit.app.ui.composable.analysis

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.ui.theme.Pretendard

@Composable
fun PhotoUploadButton(
    onPickFromGallery: (Uri?) -> Unit,
    onOpenInAppCamera: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> onPickFromGallery(uri) }

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
        AnalysisDialog(
            title = "이미지 업로드",
            text = "원하는 업로드 방식을 선택하세요.",
            confirmText = "갤러리",
            onDismiss = { showDialog = false },
            onConfirm = {
                galleryLauncher.launch("image/*")
                showDialog = false
            },
            secondaryText = "카메라",
            onSecondary = {
                onOpenInAppCamera()
                showDialog = false
            }
        )
    }
}
