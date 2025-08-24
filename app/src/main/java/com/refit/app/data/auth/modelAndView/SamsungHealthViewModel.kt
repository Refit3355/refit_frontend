package com.refit.app.data.auth.modelAndView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refit.app.data.auth.api.AuthApi
import com.refit.app.data.auth.model.SamsungHealthSaveRequest
import com.refit.app.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SamsungHealthViewModel : ViewModel() {

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    private val authApi: AuthApi = RetrofitInstance.retrofit.create(AuthApi::class.java)

    fun saveHealthInfo(request: SamsungHealthSaveRequest) {
        viewModelScope.launch {
            try {
                val response = authApi.saveSamsungHealth(request)
                _message.value = response.message
            } catch (e: Exception) {
                _message.value = "저장 실패: ${e.message}"
            }
        }
    }
}
