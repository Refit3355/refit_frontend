package com.refit.app.data.chatbot.modelAndView

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.parcelize.Parcelize
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private const val KEY_MESSAGES = "chat.messages"
private const val KEY_VARS     = "chat.vars"

sealed class ChatItem : Parcelable {
    @Parcelize
    data class Bot(
        val templateId: String,
        val at: Long = System.currentTimeMillis()
    ) : ChatItem()

    @Parcelize
    data class User(
        val text: String,
        val at: Long = System.currentTimeMillis()
    ) : ChatItem()
}

class ChatbotViewModel(
    private val saved: SavedStateHandle
) : ViewModel() {

    // StateFlow로 노출 (Compose가 관찰)
    private val _messages = MutableStateFlow<List<ChatItem>>(
        saved.get<ArrayList<ChatItem>>(KEY_MESSAGES)?.toList()
            ?: listOf(ChatItem.Bot("greeting"))
    )
    val messages: StateFlow<List<ChatItem>> = _messages

    private val _vars = MutableStateFlow<Map<String, String>>(
        saved.get<Map<String, String>>(KEY_VARS) ?: emptyMap()
    )
    val vars: StateFlow<Map<String, String>> = _vars

    fun appendMessage(item: ChatItem) {
        val next = _messages.value + item
        _messages.value = next
        saved[KEY_MESSAGES] = ArrayList(next)
    }

    fun setVars(newVars: Map<String, String>) {
        val merged = _vars.value + newVars
        _vars.value = merged
        saved[KEY_VARS] = merged
    }

    fun reset(startTemplateId: String = "greeting") {
        val init = listOf(ChatItem.Bot(startTemplateId))
        _messages.value = init
        _vars.value = emptyMap()
        saved[KEY_MESSAGES] = ArrayList(init)
        saved[KEY_VARS] = emptyMap<String, String>()
    }
}
