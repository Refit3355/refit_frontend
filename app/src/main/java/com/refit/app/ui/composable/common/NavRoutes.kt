package com.refit.app.ui.composable.common

object NavRoutes {
    const val CheckoutRoot = "checkout"

    // 주문서
    const val OrderSheet = "checkout/orderSheet?payload={payload}"
    // 결제창
    const val TossPay = "checkout/tossPay?orderId={orderId}&orderName={orderName}&amount={amount}&method={method}&clientKey={clientKey}&successUrl={successUrl}&failUrl={failUrl}"
    // 결제 성공 딥링크 (NavDeepLink로 진입)
    const val PayResult = "checkout/payResult?paymentKey={paymentKey}&orderId={orderId}&amount={amount}"
    // 결제 실패 딥링크
    const val PayFail = "checkout/payFail"

    const val TossPayBase = "tossPay"
    const val TossPayRoute =
        "tossPay?orderId={orderId}&orderName={orderName}&amount={amount}" +
                "&method={method}&clientKey={clientKey}&successUrl={successUrl}&failUrl={failUrl}"

}