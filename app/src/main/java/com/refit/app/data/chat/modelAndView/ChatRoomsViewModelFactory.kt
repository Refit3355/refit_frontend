package com.refit.app.data.chat.modelAndView

import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.refit.app.data.chat.api.ChatApi
import com.refit.app.data.chat.repository.ChatRepository
import com.refit.app.data.chat.usecase.GetChatRoomsUseCase
import com.refit.app.network.RetrofitInstance

val ChatRoomsViewModelFactory = viewModelFactory {
    initializer {
        val api = RetrofitInstance.create(ChatApi::class.java)
        val repo = ChatRepository(api)
        val usecase = GetChatRoomsUseCase(repo)
        ChatRoomsViewModel(usecase)
    }
}
