package com.refit.app.data.local.combination.modelAndView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refit.app.data.local.combination.model.CombinationResponse
import com.refit.app.ui.fake.fakeCombinations
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CombinationViewModel : ViewModel() {

    private val _combinations = MutableStateFlow<List<CombinationResponse>>(emptyList())
    val combinations: StateFlow<List<CombinationResponse>> = _combinations

    fun loadCombinations() {
        viewModelScope.launch {
            _combinations.value = fakeCombinations
        }
    }
}
