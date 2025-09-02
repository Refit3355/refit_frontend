package com.refit.app.data.analysis.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File

object MultipartUtils {

    fun mapProductTypeForBackend(uiValue: String): String =
        when (uiValue.trim()) {
            "헬스" -> "영양제"
            "뷰티" -> "화장품"
            else   -> uiValue
        }

    fun textPart(value: String): RequestBody =
        value.toRequestBody("text/plain".toMediaType())

    fun uriToImagePart(context: Context, uri: Uri, partName: String = "image"): MultipartBody.Part {
        val bitmap = if (Build.VERSION.SDK_INT >= 28) {
            val src = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(src)
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }

        var quality = 92
        var bytes: ByteArray
        do {
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos)
            bytes = bos.toByteArray()
            quality -= 7
        } while (bytes.size > 9_000_000 && quality > 30) // 9MB target

        val tmp = File.createTempFile("upload_", ".jpg", context.cacheDir).apply {
            writeBytes(bytes); deleteOnExit()
        }
        val body = tmp.asRequestBody("image/jpeg".toMediaType())
        return MultipartBody.Part.createFormData(partName, "upload.jpg", body)
    }

    fun bytesToImagePart(
        context: Context,
        bytes: ByteArray,
        partName: String = "image"
    ): MultipartBody.Part {
        val file = File.createTempFile("upload_", ".jpg", context.cacheDir).apply {
            writeBytes(bytes); deleteOnExit()
        }
        val body = file.asRequestBody("image/jpeg".toMediaType())
        return MultipartBody.Part.createFormData(partName, "upload.jpg", body)
    }
}
