package com.refit.app.util.analysis

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

fun bytesToImagePart(
    bytes: ByteArray,
    partName: String,
    fileName: String = "upload.jpg",
    mimeType: String = "image/jpeg"
): MultipartBody.Part {
    // 임시 파일로 감싸기(바이트 바로 쓰려면 RequestBody.create로 직접 만들어도 OK)
    val tmp = File.createTempFile("refit_", "_img")
    tmp.writeBytes(bytes)
    val rb = tmp.asRequestBody(mimeType.toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(partName, fileName, rb)
}

fun String.toTextPart(): RequestBody =
    this.toRequestBody("text/plain".toMediaTypeOrNull())