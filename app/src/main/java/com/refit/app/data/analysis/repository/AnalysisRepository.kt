package com.refit.app.data.analysis.repository

//import com.refit.app.data.analysis.api.AnalysisApi
//import com.refit.app.data.analysis.model.FullAnalysisResponse
//import com.refit.app.data.analysis.model.IngredientAnalysisResponse
//import com.refit.app.data.analysis.modelAndView.AnalysisUiState
//import com.refit.app.network.UserPrefs
//import com.refit.app.util.analysis.bytesToImagePart
//import com.refit.app.util.analysis.toTextPart
//import okhttp3.MediaType.Companion.toMediaType
//import okhttp3.MultipartBody
//import okhttp3.RequestBody
//import okhttp3.RequestBody.Companion.toRequestBody

//interface AnalysisRepository {
//    suspend fun analyzeImage(bytes: ByteArray, isHealth: Boolean): FullAnalysisResponse
//    suspend fun analyzeImageUi(bytes: ByteArray, isHealth: Boolean): AnalysisUiState
//}
//
//class AnalysisRepositoryImpl(
//    private val api: AnalysisApi
//) : AnalysisRepository {
//
//    override suspend fun analyzeImage(bytes: ByteArray, isHealth: Boolean): FullAnalysisResponse {
//        val imagePart = bytesToImagePart(
//            bytes = bytes,
//            partName = "image",          // 서버 컨트롤러 @RequestPart("image")
//            fileName = "photo.jpg",
//            mimeType = "image/jpeg"
//        )
//        val productType = if (isHealth) "영양제" else "화장품"
//        val productTypePart = productType.toTextPart()
//
//        return api.analyzeImage(
//            image = imagePart,
//            productType = productTypePart
//        )
//    }
//
//    override suspend fun analyzeImageUi(bytes: ByteArray, isHealth: Boolean): AnalysisUiState {
//        val r = analyzeImage(bytes, isHealth)
//        return AnalysisUiState(
//            memberName = r.memberName,
//            matchRate = r.matchRate,
//            risky = r.risky,
//            caution = r.caution,
//            safe = r.safe,
//            riskyText = r.riskyText,
//            cautionText = r.cautionText,
//            safeText = r.safeText,
//            summary = r.summary,
//            loading = false,
//            error = null
//        )
//    }
//}
//
///* ---------- 헬퍼(재사용 가능) ---------- */
//
//private fun bytesToImagePart(
//    bytes: ByteArray,
//    partName: String,
//    fileName: String,
//    mimeType: String
//): MultipartBody.Part {
//    val body: RequestBody = bytes.toRequestBody(mimeType.toMediaType(), 0, bytes.size)
//    return MultipartBody.Part.createFormData(partName, fileName, body)
//}
//
//private fun String.toTextPart(): RequestBody =
//    this.toRequestBody("text/plain".toMediaType())