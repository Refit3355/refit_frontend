package com.refit.app.data.order.flow

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object CheckoutFlowStore {
    private val _selectedCartIds = MutableStateFlow<List<Long>>(emptyList())
    val selectedCartIds = _selectedCartIds.asStateFlow()

    fun setSelected(ids: Collection<Long>) {
        _selectedCartIds.value = ids.toList()
    }

    fun clear() {
        _selectedCartIds.value = emptyList()
    }
}