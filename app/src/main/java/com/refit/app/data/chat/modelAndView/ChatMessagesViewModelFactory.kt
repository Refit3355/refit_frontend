package com.refit.app.data.chat.modelAndView

import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.refit.app.data.chat.api.ChatApi
import com.refit.app.data.chat.repository.ChatRepository
import com.refit.app.data.chat.usecase.GetChatMessagesPageUseCase
import com.refit.app.network.RetrofitInstance

val ChatMessagesViewModelFactory = viewModelFactory {
    initializer {
        val api = RetrofitInstance.create(ChatApi::class.java)
        val repo = ChatRepository(api)
        val usecase = GetChatMessagesPageUseCase(repo)
        ChatMessagesViewModel(usecase)
    }
}