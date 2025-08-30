package com.refit.app.data.chat.modelAndView

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refit.app.data.chat.model.ChatRoom
import com.refit.app.data.chat.usecase.GetChatRoomsUseCase
import com.refit.app.ui.composable.community.CommunityCategory
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

data class ChatRoomsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedCategory: CommunityCategory = CommunityCategory.ALL,
    val rooms: List<ChatRoom> = emptyList()
)

class ChatRoomsViewModel(
    private val getChatRooms: GetChatRoomsUseCase
) : ViewModel() {

    var uiState by mutableStateOf(ChatRoomsUiState())
        private set

    private var loadJob: Job? = null

    fun load(category: CommunityCategory = uiState.selectedCategory) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null, selectedCategory = category)
            runCatching { getChatRooms(category) }
                .onSuccess { list ->
                    uiState = uiState.copy(isLoading = false, rooms = list)
                }
                .onFailure { e ->
                    uiState = uiState.copy(isLoading = false, error = e.message ?: "알 수 없는 오류")
                }
        }
    }
}
