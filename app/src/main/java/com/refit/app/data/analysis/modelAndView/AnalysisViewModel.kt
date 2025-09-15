package com.refit.app.data.analysis.modelAndView

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.refit.app.data.analysis.api.AnalysisApi
import com.refit.app.data.analysis.repository.AnalysisRepository
import com.refit.app.network.RetrofitInstance
import com.refit.app.network.TokenManager
import com.refit.app.network.UserPrefs
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

class AnalysisViewModel(
    app: Application,
    private val repo: AnalysisRepository
) : AndroidViewModel(app) {

    var productType = mutableStateOf(ProductTypeUi.BEAUTY)
        private set

    var result = mutableStateOf<UiResult>(UiResult.Empty)
        private set

    fun setProductTypeFromUi(uiValue: String) {
        productType.value = if (uiValue.trim() == "헬스") ProductTypeUi.HEALTH else ProductTypeUi.BEAUTY
    }

    fun analyzeFromUri(uri: Uri, productTypeUi: String) {
        setProductTypeFromUi(productTypeUi)
        viewModelScope.launch {
            result.value = UiResult.Loading
            try {
                val res = repo.analyzeFromUri(getApplication(), uri, productTypeUi)
                val ui = res.toUiResult(resolveMemberName(getApplication(), res.memberName), productType.value)
                result.value = ui
            } catch (e: Exception) {
                result.value = UiResult.Error(friendlyMessage(e))
            }
        }
    }

    fun analyzeFromBytes(bytes: ByteArray, productTypeUi: String) {
        setProductTypeFromUi(productTypeUi)
        viewModelScope.launch {
            result.value = UiResult.Loading
            try {
                val res = repo.analyzeFromBytes(getApplication(), bytes, productTypeUi)
                val ui = res.toUiResult(resolveMemberName(getApplication(), res.memberName), productType.value)
                result.value = ui
            } catch (e: Exception) {
                result.value = UiResult.Error(friendlyMessage(e))
            }
        }
    }

    private fun resolveMemberName(app: Application, serverName: String?): String {
        if (!serverName.isNullOrBlank()) return serverName
        val spName = try { UserPrefs.getNickname() } catch (_: Exception) { null }
        if (!spName.isNullOrBlank()) return spName!!
        val token = TokenManager.getAccessToken()
        val jwtName = token?.let { TokenManager.parseNicknameFromJwt(it) }
        if (!jwtName.isNullOrBlank()) return jwtName!!
        return "사용자"
    }

    private fun friendlyMessage(e: Exception): String = when (e) {
        is UnknownHostException     -> "네트워크 연결을 확인해 주세요."
        is SocketTimeoutException   -> "서버 응답이 지연되고 있어요. 잠시 후 다시 시도해 주세요."
        else                        -> e.message ?: "알 수 없는 오류가 발생했어요."
    }

    fun acknowledgeBlocked() {
        result.value = UiResult.Empty
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
