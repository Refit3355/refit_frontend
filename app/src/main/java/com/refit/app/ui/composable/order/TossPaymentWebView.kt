package com.refit.app.ui.composable.order

import android.webkit.WebView
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun TossPaymentWebView(
    orderId: String,
    orderName: String,
    amount: Long,
    method: String,                    // "CARD","TRANSFER","VIRTUAL_ACCOUNT","MOBILE_PHONE",...
    clientKey: String,                 // test_ck_***
    successUrl: String = "refitapp://pay/success",
    failUrl: String = "refitapp://pay/fail"
) {
    val html = """
    <!doctype html><html><head><meta charset="utf-8"/>
      <script src="https://js.tosspayments.com/v2/standard"></script>
    </head><body>
      <script>
        (async function(){
          const toss = TossPayments("$clientKey");
          await toss.requestPayment("$method", {
            amount: { value: $amount, currency: "KRW" },
            orderId: "$orderId",
            orderName: "$orderName",
            successUrl: "$successUrl",
            failUrl: "$failUrl",
            flowMode: "DEFAULT"
          });
        })();
      </script>
    </body></html>
  """.trimIndent()

    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                settings.javaScriptEnabled = true
                loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
            }
        }
    )
}
