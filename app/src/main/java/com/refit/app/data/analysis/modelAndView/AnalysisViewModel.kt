//package com.refit.app.data.analysis.modelAndView
//
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.setValue
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.viewModelScope
//import androidx.lifecycle.viewmodel.initializer
//import androidx.lifecycle.viewmodel.viewModelFactory
//import com.refit.app.data.analysis.repository.AnalysisRepository
//import kotlinx.coroutines.launch
//
//class AnalysisViewModel(private val repo: AnalysisRepository) : ViewModel() {
//    var ui by mutableStateOf(AnalysisUiState(loading = false))
//        private set
//
//    fun analyze(imageBytes: ByteArray, isHealth: Boolean) {
//        ui = ui.copy(loading = true, error = null)
//        viewModelScope.launch {
//            try {
//                ui = repo.analyzeImageUi(imageBytes, isHealth)
//            } catch (e: Exception) {
//                ui = ui.copy(loading = false, error = e.message ?: "네트워크 오류")
//            }
//        }
//    }
//}
//
//
///* 간단한 DI 팩토리 (필요 시 수정) */
//fun provideAnalysisViewModel(repo: AnalysisRepository) = viewModelFactory {
//    initializer {
//        AnalysisViewModel(repo)
//    }
//}
//
//
//data class AnalysisUiState(
//    val memberName: String = "",
//    val matchRate: Int = 0,
//    val risky: List<String> = emptyList(),
//    val caution: List<String> = emptyList(),
//    val safe: List<String> = emptyList(),
//    val riskyText: String = "",
//    val cautionText: String = "",
//    val safeText: String = "",
//    val summary: String = "",
//    val loading: Boolean = false,
//    val error: String? = null
//)
//
//enum class DetailTab { Detailed, Simple }