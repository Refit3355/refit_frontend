package com.refit.app.data.cart.repository

import com.refit.app.data.cart.api.CartApi
import com.refit.app.data.cart.model.CartAddBulkRequest
import com.refit.app.data.cart.model.CartAddRequest
import com.refit.app.data.cart.model.CartDeleteBulkRequest
import com.refit.app.data.cart.model.CartItemDto
import com.refit.app.data.cart.model.CartQuantityUpdateRequest
import retrofit2.HttpException


sealed interface CartResult {
    data class ListSuccess(val items: List<CartItemDto>) : CartResult
    data class CountSuccess(val count: Int) : CartResult
    data object Unauth : CartResult
    data class Error(val msg: String) : CartResult
    data object Ok : CartResult
}

class CartRepository(
    private val api: CartApi
) {
    suspend fun fetchCount(): CartResult = try {
        val res = api.getCartCount()
        CartResult.CountSuccess(res.count)
    } catch (e: HttpException) {
        when (e.code()) {
            401 -> CartResult.Unauth
            else -> CartResult.Error("HTTP ${e.code()}")
        }
    } catch (e: Exception) {
        CartResult.Error(e.message ?: "네트워크 오류")
    }

    suspend fun fetchCart(): CartResult = try {
        val res = api.getCart().data.orEmpty()
        CartResult.ListSuccess(res)
    } catch (e: HttpException) {
        when (e.code()) { 401 -> CartResult.Unauth; else -> CartResult.Error("HTTP ${e.code()}") }
    } catch (e: Exception) {
        CartResult.Error(e.message ?: "네트워크 오류")
    }

    suspend fun addOne(productId: Long, quantity: Int): CartResult = try {
        api.addCartItem(CartAddRequest(productId, quantity))
        CartResult.Ok
    } catch (e: HttpException) {
        when (e.code()) { 401 -> CartResult.Unauth; else -> CartResult.Error("HTTP ${e.code()}") }
    } catch (e: Exception) {
        CartResult.Error(e.message ?: "네트워크 오류")
    }

    suspend fun addBulk(items: List<CartAddRequest>): CartResult = try {
        api.addCartItemsBulk(CartAddBulkRequest(items))
        CartResult.Ok
    } catch (e: HttpException) {
        when (e.code()) { 401 -> CartResult.Unauth; else -> CartResult.Error("HTTP ${e.code()}") }
    } catch (e: Exception) {
        CartResult.Error(e.message ?: "네트워크 오류")
    }

    suspend fun deleteOne(cartId: Long): CartResult = try {
        api.deleteCartItem(cartId)
        CartResult.Ok
    } catch (e: HttpException) {
        when (e.code()) { 401 -> CartResult.Unauth; else -> CartResult.Error("HTTP ${e.code()}") }
    } catch (e: Exception) {
        CartResult.Error(e.message ?: "네트워크 오류")
    }

    suspend fun deleteBulk(ids: List<Long>): CartResult = try {
        api.deleteCartItemsBulk(CartDeleteBulkRequest(ids))
        CartResult.Ok
    } catch (e: HttpException) {
        when (e.code()) { 401 -> CartResult.Unauth; else -> CartResult.Error("HTTP ${e.code()}") }
    } catch (e: Exception) {
        CartResult.Error(e.message ?: "네트워크 오류")
    }

    suspend fun updateQuantity(cartId: Long, quantity: Int): CartResult = try {
        api.updateCartQuantity(cartId, CartQuantityUpdateRequest(quantity))
        CartResult.Ok
    } catch (e: HttpException) {
        when (e.code()) { 401 -> CartResult.Unauth; else -> CartResult.Error("HTTP ${e.code()}") }
    } catch (e: Exception) {
        CartResult.Error(e.message ?: "네트워크 오류")
    }
}
