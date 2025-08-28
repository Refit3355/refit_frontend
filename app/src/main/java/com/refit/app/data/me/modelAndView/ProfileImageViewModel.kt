package com.refit.app.data.me.modelAndView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refit.app.data.me.api.MeApi
import com.refit.app.data.me.model.ProfileImageResponse
import com.refit.app.network.RetrofitInstance
import com.refit.app.network.UserPrefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class ProfileImageViewModel : ViewModel() {

    private val meApi: MeApi = RetrofitInstance.retrofit.create(MeApi::class.java)

    private val _profileUrl = MutableStateFlow<String?>(null)
    val profileUrl: StateFlow<String?> = _profileUrl

    fun updateProfileImage(filePart: MultipartBody.Part) {
        viewModelScope.launch {
            try {
                val response: ProfileImageResponse = meApi.updateProfileImage(filePart)
                _profileUrl.value = response.profileUrl

                UserPrefs.setProfileUrl(response.profileUrl)

            } catch (e: Exception) {
                _profileUrl.value = null
            }
        }
    }
}

