package com.refit.app.data.analysis.modelAndView

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.refit.app.data.analysis.api.AnalysisApi
import com.refit.app.data.analysis.model.FullAnalysisResponse
import com.refit.app.data.analysis.repository.AnalysisRepository
import com.refit.app.network.RetrofitInstance
import com.refit.app.network.TokenManager
import com.refit.app.network.UserPrefs
import com.refit.app.ui.screen.AnalysisUiState
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class AnalysisViewModel(
    app: Application,
    private val repo: AnalysisRepository
) : AndroidViewModel(app) {

    val ui = mutableStateOf(AnalysisUiState())

    fun analyzeFromUri(uri: Uri, productTypeUi: String) {
        viewModelScope.launch {
            ui.value = ui.value.copy(loading = true, error = null)
            try {
                val res = repo.analyzeFromUri(getApplication(), uri, productTypeUi)
                ui.value = ui.value.mergeWithFallback(getApplication(), res)
            } catch (e: Exception) {
                ui.value = ui.value.copy(loading = false, error = friendlyMessage(e))
            }
        }
    }

    fun analyzeFromBytes(bytes: ByteArray, productTypeUi: String) {
        viewModelScope.launch {
            ui.value = ui.value.copy(loading = true, error = null)
            try {
                val res = repo.analyzeFromBytes(getApplication(), bytes, productTypeUi)
                ui.value = ui.value.mergeWithFallback(getApplication(), res)
            } catch (e: Exception) {
                ui.value = ui.value.copy(loading = false, error = friendlyMessage(e))
            }
        }
    }

    /** 서버 memberName이 비어오면 SharedPreferences(JWT) 폴백 사용 */
    private fun AnalysisUiState.mergeWithFallback(
        app: Application,
        res: FullAnalysisResponse
    ): AnalysisUiState {
        val displayName = resolveMemberName(app, res.memberName)
        return this.copy(
            loading = false,
            error = null,
            memberName = displayName,
            matchRate = res.matchRate,
            risky = res.risky,
            caution = res.caution,
            safe = res.safe,
            riskyText = res.riskyText,
            cautionText = res.cautionText,
            safeText = res.safeText,
            summary = res.summary
        )
    }

    /** 폴백 우선순위: 서버값 > UserPrefs.getNickname() > JWT 닉네임 > "사용자" */
    private fun resolveMemberName(app: Application, serverName: String?): String {
        if (!serverName.isNullOrBlank()) return serverName

        // UserPrefs는 Application에서 UserPrefs.init(this) 선행 필요
        val spName = try { UserPrefs.getNickname() } catch (_: Exception) { null }
        if (!spName.isNullOrBlank()) return spName!!

        val token = TokenManager.getAccessToken()
        val jwtName = token?.let { TokenManager.parseNicknameFromJwt(it) }
        if (!jwtName.isNullOrBlank()) return jwtName!!

        return "사용자"
    }

    private fun friendlyMessage(e: Exception): String = when (e) {
        is UnknownHostException -> "네트워크 연결을 확인해 주세요."
        is SocketTimeoutException -> "서버 응답이 지연되고 있어요. 잠시 후 다시 시도해 주세요."
        else -> e.message ?: "알 수 없는 오류가 발생했어요."
    }
}

class AnalysisViewModelFactory(private val app: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val api = RetrofitInstance.create(AnalysisApi::class.java)
        val repo = AnalysisRepository(api)
        return AnalysisViewModel(app, repo) as T
    }
}

@Composable
fun rememberAnalysisViewModel(): AnalysisViewModel {
    val app = LocalContext.current.applicationContext as Application
    return viewModel(factory = AnalysisViewModelFactory(app))
}
