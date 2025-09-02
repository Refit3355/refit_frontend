package com.refit.app.ui.screen

import android.Manifest
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.refit.app.ui.composable.analysis.ScannerOverlay
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard
import kotlinx.coroutines.guava.await

@Composable
fun InAppCameraScreen(
    onCancel: () -> Unit,
    onCroppedBytes: (ByteArray) -> Unit,
    guideWidthRatio: Float = 0.86f,
    guideHeightRatio: Float = 0.55f,
    hintText: String = "가이드 라인 안에 맞춰 촬영해주세요",
    showHint: Boolean = true
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCamPermission by remember { mutableStateOf(false) }
    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { hasCamPermission = it }

    LaunchedEffect(Unit) {
        val ok = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        if (ok) hasCamPermission = true else permLauncher.launch(Manifest.permission.CAMERA)
    }

    if (!hasCamPermission) {
        Box(Modifier.fillMaxSize(), contentAlignment =  Alignment.Center) {
            Text("카메라 권한이 필요합니다.")
        }
        return
    }

    val previewView = remember {
        PreviewView(context).apply { scaleType = PreviewView.ScaleType.FILL_CENTER }
    }
    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }

    // CameraX 바인딩
    LaunchedEffect(Unit) {
        try {
            val provider = ProcessCameraProvider.getInstance(context).await()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            provider.unbindAll()
            provider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageCapture
            )
        } catch (_: Throwable) {
            // 에러 처리
        }
    }

    Box(Modifier.fillMaxSize()) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

        // 가이드
        ScannerOverlay(guideWidthRatio, guideHeightRatio)

        if (showHint) {
            Text(
                text = hintText,
                color = Color.White,
                fontFamily = Pretendard,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 100.dp)
                    .background(
                        MainPurple.copy(alpha = 0.55f),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(999.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // 닫기
        TextButton(
            onClick = onCancel,
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
        ) { Text("닫기", color = Color.White, fontSize = 18.sp, fontFamily = Pretendard) }

        // 촬영
        Button(
            onClick = {
                imageCapture.takePicture(
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(image: ImageProxy) {
                            try {
                                val bytes: ByteArray = when (image.format) {
                                    ImageFormat.JPEG -> {
                                        val buf = image.planes[0].buffer
                                        val arr = ByteArray(buf.remaining())
                                        buf.get(arr)
                                        arr
                                    }
                                    ImageFormat.YUV_420_888 -> {
                                        yuv420888ToJpeg(image, quality = 95)
                                    }
                                    else -> ByteArray(0)
                                }
                                if (bytes.isNotEmpty()) {
                                    onCroppedBytes(bytes)
                                }
                            } finally {
                                image.close()
                            }
                        }

                        override fun onError(exc: ImageCaptureException) {
                            // TODO: 필요시 에러 처리
                        }
                    }
                )
            },
            modifier = Modifier
                .width(250.dp)
                .align(Alignment.BottomCenter)
                .padding(bottom = 28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MainPurple,
                contentColor = Color.White
            )
        ) {
            Text("촬영", fontFamily = Pretendard, fontSize = 15.sp)
        }
    }
}

private fun yuv420888ToJpeg(image: ImageProxy, quality: Int = 95): ByteArray {

    val yPlane = image.planes[0]
    val uPlane = image.planes[1]
    val vPlane = image.planes[2]

    val width = image.width
    val height = image.height

    val ySize = width * height
    val uvSize = width * height / 2
    val nv21 = ByteArray(ySize + uvSize)

    var outputOffset = 0
    val yBuffer = yPlane.buffer
    val yRowStride = yPlane.rowStride
    val yPixelStride = yPlane.pixelStride

    for (row in 0 until height) {
        val rowStart = row * yRowStride
        for (col in 0 until width) {
            nv21[outputOffset++] = yBuffer.get(rowStart + col * yPixelStride)
        }
    }

    val vBuffer = vPlane.buffer
    val uBuffer = uPlane.buffer
    val vRowStride = vPlane.rowStride
    val uRowStride = uPlane.rowStride
    val vPixelStride = vPlane.pixelStride
    val uPixelStride = uPlane.pixelStride

    val chromaHeight = height / 2
    val chromaWidth = width / 2
    var uvOffset = ySize

    for (row in 0 until chromaHeight) {
        val vRowStart = row * vRowStride
        val uRowStart = row * uRowStride
        for (col in 0 until chromaWidth) {
            val vIndex = vRowStart + col * vPixelStride
            val uIndex = uRowStart + col * uPixelStride

            nv21[uvOffset++] = vBuffer.get(vIndex)
            nv21[uvOffset++] = uBuffer.get(uIndex)
        }
    }

    val yuvImage = YuvImage(nv21, ImageFormat.NV21, width, height, null)
    val out = java.io.ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(0, 0, width, height), quality, out)
    return out.toByteArray()
}
