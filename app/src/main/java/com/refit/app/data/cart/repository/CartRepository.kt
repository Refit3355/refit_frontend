package com.refit.app.data.cart.repository

import com.refit.app.data.cart.api.CartApi
import retrofit2.HttpException

sealed interface CartResult {
    data class Success(val count: Int) : CartResult
    data object Unauth : CartResult          // 401
    data class Error(val msg: String) : CartResult
}

class CartRepository(
    private val api: CartApi
) {
    suspend fun fetchCount(): CartResult = try {
        val res = api.getCartCount()
        CartResult.Success(res.count)
    } catch (e: HttpException) {
        when (e.code()) {
            401 -> CartResult.Unauth
            else -> CartResult.Error("HTTP ${e.code()}")
        }
    } catch (e: Exception) {
        CartResult.Error(e.message ?: "네트워크 오류")
    }
}
