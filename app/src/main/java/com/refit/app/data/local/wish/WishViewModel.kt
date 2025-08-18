package com.refit.app.data.local.wish

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WishViewModel(app: Application) : AndroidViewModel(app) {
    private val store = WishStore(app.applicationContext)

    val wishedIds: StateFlow<Set<Long>> =
        store.wishedIds.stateIn(viewModelScope, SharingStarted.Lazily, emptySet())

    fun toggle(id: Long) = viewModelScope.launch { store.toggle(id) }
}