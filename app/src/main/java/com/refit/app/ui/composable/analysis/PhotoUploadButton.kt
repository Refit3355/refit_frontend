package com.refit.app.ui.composable.analysis

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext

@Composable
fun PhotoUploadButton(onImageSelected: (Uri?) -> Unit) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    // 촬영 전 생성한 URI를 구성변경에도 유지
    var cameraImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    // 갤러리 런처
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> onImageSelected(uri) }

    // 카메라 런처 (URI에 저장)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) onImageSelected(cameraImageUri) else onImageSelected(null)
    }

    // 권한 요청 런처
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = hasRequiredCameraPerms(result)
        if (granted) {
            cameraImageUri = createImageUri(context)
            cameraImageUri?.let { cameraLauncher.launch(it) }
        } else {
            // 권한 거부됨: 필요 시 스낵바/다이얼로그 안내
        }
    }

    Button(onClick = { showDialog = true }) {
        Text("+ 사진 업로드")
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("이미지 선택") },
            text = {},
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    galleryLauncher.launch("image/*")
                }) { Text("갤러리") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    val perms = requiredCameraPermissions()
                    permissionLauncher.launch(perms)
                }) { Text("카메라") }
            }
        )
    }
}

/** API 레벨별 필요한 권한 배열 */
private fun requiredCameraPermissions(): Array<String> = buildList {
    add(Manifest.permission.CAMERA)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        add(Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        add(Manifest.permission.READ_EXTERNAL_STORAGE)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }
}.toTypedArray()

/** 권한 요청 결과 해석 */
private fun hasRequiredCameraPerms(result: Map<String, Boolean>): Boolean {
    // 하나라도 false면 실패로 간주
    return result.values.all { it }
}

/** Q+에선 RELATIVE_PATH 포함하여 MediaStore에 안전하게 URI 생성 */
private fun createImageUri(context: Context): Uri? {
    val resolver = context.contentResolver
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "photo_${System.currentTimeMillis()}.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Refit")
            put(MediaStore.Images.Media.IS_PENDING, 0)
        }
    }
    return resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
}