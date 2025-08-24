package com.refit.app.data.cart.modelAndView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.refit.app.data.cart.repository.CartRepository

class CartViewModelFactory(
    private val repo: CartRepository,
    private val badgeVm: CartBadgeViewModel? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(CartBadgeViewModel::class.java) -> {
                CartBadgeViewModel(repo) as T
            }
            modelClass.isAssignableFrom(CartEditViewModel::class.java) -> {
                CartEditViewModel(repo, badgeVm) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
