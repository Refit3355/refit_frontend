package com.refit.app.data.order.model

/**
 * 주문서 생성은 3가지 진입을 모두 커버:
 * - 바로구매: source = DIRECT, lines = [ (productId, qty) ]
 * - 장바구니: source = CART, cartItemIds = [...]
 * - 조합왕:   source = COMBINATION, combinationId = ...
 */
data class DraftOrderRequest(
    val source: OrderSource,
    val lines: List<OrderLineItem>? = null,
    val cartItemIds: List<Long>? = null,
    val combinationId: Long? = null
)

enum class OrderSource { DIRECT, CART, COMBINATION }

data class OrderLineItem(
    val productId: Long,
    val quantity: Int
)
