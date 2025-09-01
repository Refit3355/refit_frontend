package com.refit.app.ui.screen.order

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun TossWebViewScreen(
    navController: NavController,
    orderId: String,      // 문자열 (예: "ORD-2025...")
    orderName: String,
    amount: Long,
    method: String,       // "CARD" 권장
    clientKey: String,    // BuildConfig.TOSS_CLIENT_KEY
    successUrl: String,   // "refitapp://pay/success"
    failUrl: String       // "refitapp://pay/fail"
) {
    val html = """
        <!doctype html>
        <html lang="ko">
        <head>
          <meta charset="utf-8"/>
          <meta name="viewport" content="width=device-width, initial-scale=1"/>
          <title>Toss Pay</title>
          <style>
            html,body{margin:0;padding:0;height:100%;font-family:system-ui,-apple-system,Segoe UI,Roboto}
            .wrap{display:flex;align-items:center;justify-content:center;height:100%}
            .msg{opacity:.7}
          </style>
        </head>
        <body>
          <div class="wrap"><div class="msg">결제창을 불러오는 중…</div></div>
          <script>
            (function() {
              var clientKey   = ${jsStr(clientKey)};
              var orderId     = ${jsStr(orderId)};
              var orderName   = ${jsStr(orderName)};
              var amountValue = ${amount};
              var method = ${jsStr(method)}; 
              var successUrl  = ${jsStr(successUrl)};
              var failUrl     = ${jsStr(failUrl)};

              function redirectFail(code, message) {
                // 토스 에러 코드를 그대로 넘겨서 앱에서 표시
                var url = failUrl
                  + "?code=" + encodeURIComponent(code || "CLIENT_ERROR")
                  + "&message=" + encodeURIComponent(message || "결제창 호출 실패");
                location.href = url;
              }

              function start() {
                try {
                  if (!window.TossPayments) {
                    return redirectFail("CLIENT_ERROR", "TossPayments SDK가 로드되지 않았습니다.");
                  }
                  // v2 초기화
                  var tossPayments = TossPayments(clientKey);

                  // 표준결제 '결제창' 초기화 - customerKey 필수
                  // 고객 식별자: 유추 어려운 고유값 권장(여기선 orderId 재사용)
                  var payment = tossPayments.payment({ customerKey: orderId });

                  // 모바일 환경은 Redirect 방식 사용
                  payment.requestPayment({
                    method: method,
                    amount: { value: amountValue, currency: "KRW" },
                    orderId: orderId,
                    orderName: orderName,
                    successUrl: successUrl,
                    failUrl: failUrl,
                    card: {
                      // 통합결제창(기본): DEFAULT / 특정사 자체창: DIRECT
                      flowMode: "DEFAULT"
                      // flowMode: "DIRECT", cardCompany: "KB" 등도 가능 (문서 가이드)
                    }
                  }).catch(function (e) {
                    // 토스 SDK에서 넘기는 표준 에러
                    var code = e && e.code ? e.code : "CLIENT_ERROR";
                    var msg  = e && e.message ? e.message : "결제창 호출 실패";
                    redirectFail(code, msg);
                  });
                } catch (e) {
                  redirectFail("CLIENT_ERROR", e && e.message ? e.message : "예외 발생");
                }
              }

              // SDK를 동적으로 로드 (onload 보장)
              var s = document.createElement("script");
              s.src = "https://js.tosspayments.com/v2/standard";
              s.onload = start;
              s.onerror = function(){ redirectFail("CLIENT_ERROR", "SDK를 불러오지 못했습니다."); };
              document.head.appendChild(s);
            })();

            // Kotlin 문자열 안전 삽입을 위한 유틸 (위에서 서버 생성함)
          </script>
        </body>
        </html>
    """.trimIndent()

    fun handleUri(uri: Uri): Boolean {
        if (uri.scheme == "refitapp" && uri.host == "pay") {
            when (uri.lastPathSegment) {
                "success" -> {
                    val paymentKey = uri.getQueryParameter("paymentKey").orEmpty()
                    val ordId      = uri.getQueryParameter("orderId").orEmpty()
                    val amt        = uri.getQueryParameter("amount")?.toLongOrNull() ?: 0L
                    navController.navigate(
                        "checkout/payResult?paymentKey=${encQuery(paymentKey)}&orderId=${encQuery(ordId)}&amount=$amt"
                    )
                }
                "fail" -> {
                    // 토스에서 내려준 code/message를 그대로 전달
                    val code = uri.getQueryParameter("code") ?: "UNKNOWN"
                    val msg  = uri.getQueryParameter("message") ?: "결제에 실패했어요"
                    val route = "checkout/payFail?code=${encQuery(code)}&message=${encQuery(msg)}"
                    runCatching { navController.navigate(route) }
                        .onFailure {
                            navController.previousBackStackEntry?.savedStateHandle?.set("pay_fail_code", code)
                            navController.previousBackStackEntry?.savedStateHandle?.set("pay_fail_message", msg)
                            navController.popBackStack()
                        }
                }
            }
            return true
        }
        return false
    }

    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                // 콘솔 로그 보이게 (디버깅 편의)
                settings.javaScriptCanOpenWindowsAutomatically = true
                settings.setSupportMultipleWindows(true)

                webChromeClient = object : WebChromeClient() {
                    override fun onCreateWindow(
                        view: WebView?, isDialog: Boolean, isUserGesture: Boolean, resultMsg: android.os.Message?
                    ): Boolean {
                        // 새창 요청을 현재 WebView로 처리
                        val transport = resultMsg?.obj as? WebView.WebViewTransport ?: return false
                        transport.webView = view
                        resultMsg.sendToTarget()
                        return true
                    }
                }

                webViewClient = object : WebViewClient() {

                    private fun launchExternal(ctx: android.content.Context, intent: android.content.Intent): Boolean {
                        return try {
                            ctx.startActivity(intent) // 바로 시도
                            true
                        } catch (e: android.content.ActivityNotFoundException) {
                            false
                        } catch (e: Exception) {
                            false
                        }
                    }

                    private fun handleIntentUrl(view: WebView, url: String): Boolean {
                        return try {
                            val ctx = view.context
                            val intent = android.content.Intent.parseUri(url, android.content.Intent.URI_INTENT_SCHEME)

                            // 바로 실행 시도 (resolveActivity 검사 생략: Android 11+ 패키지 가시성 이슈 회피)
                            if (launchExternal(ctx, intent)) return true

                            // 실패 시 fallback
                            val fallback = intent.getStringExtra("browser_fallback_url")
                            if (!fallback.isNullOrBlank()) {
                                view.loadUrl(fallback)
                                return true
                            }

                            val pkg = intent.`package`
                            if (!pkg.isNullOrBlank()) {
                                val market = Uri.parse("market://details?id=$pkg")
                                launchExternal(ctx, android.content.Intent(android.content.Intent.ACTION_VIEW, market))
                                return true
                            }
                            false
                        } catch (e: Exception) {
                            android.util.Log.e("TossWebView", "handleIntentUrl error: ${e.message}", e)
                            false
                        }
                    }

                    private fun handleCustomScheme(view: WebView, uri: Uri): Boolean {
                        val ctx = view.context

                        // 커스텀 스킴을 외부 앱으로
                        try {
                            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, uri)
                            if (launchExternal(ctx, intent)) return true
                        } catch (_: Exception) { /* no-op */ }

                        // (선택) 스킴→패키지 맵핑으로 마켓 이동 (몇 개만 추가해도 체감 좋아짐)
                        val schemeToPkg = mapOf(
                            "kakaopay" to "com.kakao.talk",
                            "hdcardappcardansimclick" to "com.hyundaicard.appcard",
                            "shinhan-sr-ansimclick" to "com.shcard.smartpay",
                            "kb-acp" to "com.kbcard.kbkookmincard",
                            "lotte-appcard" to "com.lotte.lottesmartpay",
                            "nhallonepayansimclick" to "nh.smart.allonepay",
                            "wooripay" to "com.wooricard.wpay"
                        )
                        schemeToPkg[uri.scheme]?.let { pkg ->
                            val market = Uri.parse("market://details?id=$pkg")
                            launchExternal(ctx, android.content.Intent(android.content.Intent.ACTION_VIEW, market))
                            return true
                        }
                        return false
                    }

                    override fun shouldOverrideUrlLoading(
                        view: WebView,
                        request: android.webkit.WebResourceRequest
                    ): Boolean {
                        val uri = request.url
                        val url = uri.toString()

                        // 우리 앱 딥링크 처리
                        if (handleUri(uri)) return true

                        // 1) intent: (슬래시 유/무 모두 커버)
                        if (url.startsWith("intent:")) return handleIntentUrl(view, url)

                        // 2) http/https 외 스킴은 외부 앱으로
                        if (uri.scheme !in listOf("http", "https", "about", "javascript")) {
                            return handleCustomScheme(view, uri)
                        }
                        return false
                    }

                    @Deprecated("Deprecated in Java")
                    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                        val uri = Uri.parse(url)

                        if (handleUri(uri)) return true
                        if (url.startsWith("intent:")) return handleIntentUrl(view, url)
                        if (uri.scheme !in listOf("http", "https", "about", "javascript")) {
                            return handleCustomScheme(view, uri)
                        }
                        return false
                    }
                }


                // baseURL을 https로 주면 외부 스크립트/리다이렉트 CSP 이슈 줄어듦
                loadDataWithBaseURL("https://docs.tosspayments.com", html, "text/html", "UTF-8", null)
            }
        }
    )
}

// 공백을 '+'가 아니라 '%20'로 만들기 위해 Uri.encode 사용
private fun encQuery(s: String) = Uri.encode(s)

// Kotlin에서 JS 안전 문자열로 만드는 헬퍼
private fun jsStr(s: String) = "\"" + s
    .replace("\\", "\\\\")
    .replace("\"", "\\\"")
    .replace("\n", "\\n")
    .replace("\r", "") + "\""
