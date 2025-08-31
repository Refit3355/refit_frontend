package com.refit.app.data.order.model

import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import java.net.URLDecoder

@Serializable
data class DraftOrderRequestSer(
    val source: String,
    val lines: List<Line>? = null,
    val cartItemIds: List<Long>? = null,
    val combinationId: Long? = null
) {
    @Serializable data class Line(val productId: Long, val quantity: Int)
}

fun encodeDraftOrderRequest(req: DraftOrderRequest): String {
    val ser = DraftOrderRequestSer(
        source = req.source.name,
        lines = req.lines?.map { DraftOrderRequestSer.Line(it.productId, it.quantity) },
        cartItemIds = req.cartItemIds,
        combinationId = req.combinationId
    )
    val json = Json.encodeToString(ser)
    return java.net.URLEncoder.encode(json, "utf-8")
}

fun decodeDraftOrderRequest(encoded: String): DraftOrderRequest {
    val json = URLDecoder.decode(encoded, "utf-8")
    val ser = Json.decodeFromString<DraftOrderRequestSer>(json)
    return DraftOrderRequest(
        source = OrderSource.valueOf(ser.source),
        lines = ser.lines?.map { OrderLineItem(it.productId, it.quantity) },
        cartItemIds = ser.cartItemIds,
        combinationId = ser.combinationId
    )
}