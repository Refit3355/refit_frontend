package com.refit.app.ui.screen.order

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.webkit.ConsoleMessage
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import java.net.URLEncoder

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun TossWebViewScreen(
    navController: NavController,
    orderId: String,
    orderName: String,
    amount: Long,
    method: String,       // "CARD" / "VIRTUAL_ACCOUNT" / "TRANSFER" / "MOBILE_PHONE"
    clientKey: String,
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
            .wrap{
                position: fixed;      
                inset: 0;             
                display: flex;
                align-items: center; 
                justify-content: center; 
            }
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
              var method      = ${jsStr(method)};
              var successUrl  = ${jsStr(successUrl)};
              var failUrl     = ${jsStr(failUrl)};

              console.log("[METHOD]", method);
              window.onerror = function(msg, src, line, col, err) {
                console.log("[JS_ONERROR]", msg, src, line, col, (err && err.stack) || "");
              };
              window.onunhandledrejection = function(e) {
                console.log("[JS_UNHANDLED_REJECTION]", (e && e.reason) || e);
              };

              function redirectFail(code, message) {
                var url = failUrl
                  + "?code=" + encodeURIComponent(code || "CLIENT_ERROR")
                  + "&message=" + encodeURIComponent(message || "결제창 호출 실패");
                console.log("[REDIRECT_FAIL]", code, message);
                location.href = url;
              }

              function start() {
                try {
                  if (!window.TossPayments) {
                    return redirectFail("CLIENT_ERROR", "TossPayments SDK가 로드되지 않았습니다.");
                  }
                  var tossPayments = TossPayments(clientKey);
                  var payment = tossPayments.payment({ customerKey: orderId });

                  var req = {
                    method: method,
                    amount: { value: amountValue, currency: "KRW" },
                    orderId: orderId,
                    orderName: orderName,
                    successUrl: successUrl,
                    failUrl: failUrl
                  };
                  if (method === "CARD") {
                    req.card = { flowMode: "DEFAULT" };
                  }

                  console.log("[REQUEST_PAYMENT]", req);
                  payment.requestPayment(req).catch(function (e) {
                    var code = (e && e.code) || "CLIENT_ERROR";
                    var msg  = (e && e.message) || "결제창 호출 실패";
                    console.log("[REQUEST_ERROR]", code, msg, e);
                    redirectFail(code, msg);
                  });
                } catch (e) {
                  console.log("[REQUEST_EXCEPTION]", e && e.message, e);
                  redirectFail("CLIENT_ERROR", e && e.message ? e.message : "예외 발생");
                }
              }

              var s = document.createElement("script");
              s.src = "https://js.tosspayments.com/v2/standard";
              s.onload = function() {
                var needsGesture = !(method === "CARD" || method === "VIRTUAL_ACCOUNT");
                if (needsGesture) {
                  console.log("[READY] SDK loaded; waiting for user gesture");
                  var msgBox = document.querySelector(".msg");
                  if (msgBox) msgBox.textContent = "결제를 시작하려면 화면을 탭하세요.";
                  setTimeout(function(){ try { start(); } catch(_){ } }, 80);
                  var once = function() {
                    document.removeEventListener("click", once, true);
                    console.log("[USER_GESTURE] click -> start()");
                    start();
                  };
                  document.addEventListener("click", once, true);
                } else {
                  start();
                }
              };
              s.onerror = function(){ redirectFail("CLIENT_ERROR", "SDK를 불러오지 못했습니다."); };
              document.head.appendChild(s);
            })();
          </script>
        </body>
        </html>
    """.trimIndent()

    AndroidView(
        modifier = Modifier
            .fillMaxSize(),
        factory = { ctx ->
            WebView(ctx).apply {

                // ---------- 공통 헬퍼들(여기서 선언: 둘 다에서 보이게) ----------
                fun handleAppDeepLink(uri: Uri): Boolean {
                    if (uri.scheme == "refitapp" && uri.host == "pay") {
                        when (uri.lastPathSegment) {
                            "success" -> {
                                val paymentKey = uri.getQueryParameter("paymentKey").orEmpty()
                                val ordId      = uri.getQueryParameter("orderId").orEmpty()
                                val amt        = uri.getQueryParameter("amount")?.toLongOrNull() ?: 0L
                                android.util.Log.i("TossWebView", "SUCCESS paymentKey=$paymentKey, orderId=$ordId, amount=$amt")
                                navController.navigate(
                                    "checkout/payResult?paymentKey=${encQuery(paymentKey)}&orderId=${encQuery(ordId)}&amount=$amt"
                                )
                            }
                            "fail" -> {
                                val code = uri.getQueryParameter("code") ?: "UNKNOWN"
                                val msg  = uri.getQueryParameter("message") ?: "결제에 실패했어요"
                                android.util.Log.w("TossWebView", "FAIL code=$code, message=$msg")
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

                fun launchExternal(ctx: android.content.Context, intent: android.content.Intent): Boolean {
                    return try {
                        ctx.startActivity(intent)
                        true
                    } catch (_: android.content.ActivityNotFoundException) {
                        false
                    } catch (_: Exception) {
                        false
                    }
                }

                fun handleIntentUrl(view: WebView, url: String): Boolean {
                    return try {
                        val ctx = view.context
                        val intent = android.content.Intent.parseUri(url, android.content.Intent.URI_INTENT_SCHEME)

                        if (launchExternal(ctx, intent)) return true

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

                fun handleCustomScheme(view: WebView, uri: Uri): Boolean {
                    val ctx = view.context
                    try {
                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, uri)
                        if (launchExternal(ctx, intent)) return true
                    } catch (_: Exception) { /* no-op */ }

                    val schemeToPkg = mapOf(
                        "kakaopay" to "com.kakao.talk",
                        "hdcardappcardansimclick" to "com.hyundaicard.appcard",
                        "shinhan-sr-ansimclick" to "com.shcard.smartpay",
                        "kb-acp" to "com.kbcard.kbkookmincard",
                        "lotte-appcard" to "com.lotte.lottesmartpay",
                        "nhallonepayansimclick" to "nh.smart.allonepay",
                        "wooripay" to "com.wooricard.wpay",
                        "ispmobile" to "kvp.jjy.MispAndroid320",
                        "kftc-bankpay" to "com.kftc.bankpay.android",
                        "vguard" to "kr.co.shiftworks.vguardmobile",
                        "tauthlink" to "com.samsung.android.authfw",
                        "upluscorporation" to "com.lguplus.paynow",
                        "supertoss" to "viva.republica.toss"
                    )
                    schemeToPkg[uri.scheme]?.let { pkg ->
                        val market = Uri.parse("market://details?id=$pkg")
                        if (launchExternal(ctx, android.content.Intent(android.content.Intent.ACTION_VIEW, market))) {
                            android.util.Log.w("TossWebView", "Redirected to market for scheme=${uri.scheme}")
                            return true
                        }
                    }

                    android.util.Log.e("TossWebView", "No handler for scheme=${uri.scheme}: $uri")
                    runCatching {
                        val code = encQuery("APP_NOT_INSTALLED")
                        val msg  = encQuery("필요한 앱이 설치되어 있지 않습니다.")
                        navController.navigate("checkout/payFail?code=${'$'}code&message=${'$'}msg")
                    }
                    return true
                }
                // ---------- 헬퍼 끝 ----------

                // 필수 WebView 설정
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                settings.javaScriptCanOpenWindowsAutomatically = true
                settings.setSupportMultipleWindows(true)
                settings.cacheMode = WebSettings.LOAD_DEFAULT
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    WebView.setWebContentsDebuggingEnabled(true)
                }
                CookieManager.getInstance().setAcceptCookie(true)
                CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)

                webChromeClient = object : WebChromeClient() {
                    override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                        android.util.Log.d(
                            "TossWebView",
                            "${consoleMessage.message()} (source=${consoleMessage.sourceId()}, line=${consoleMessage.lineNumber()})"
                        )
                        return true
                    }

                    // window.open 처리
                    override fun onCreateWindow(
                        view: WebView?,
                        isDialog: Boolean,
                        isUserGesture: Boolean,
                        resultMsg: android.os.Message?
                    ): Boolean {
                        val parent = view ?: return false
                        val transport = resultMsg?.obj as? WebView.WebViewTransport ?: return false

                        val popup = WebView(parent.context).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.setSupportMultipleWindows(false)
                            webViewClient = object : WebViewClient() {
                                override fun shouldOverrideUrlLoading(
                                    v: WebView,
                                    request: android.webkit.WebResourceRequest
                                ): Boolean {
                                    val uri = request.url
                                    val url = uri.toString()
                                    android.util.Log.d("TossWebView", "POPUP NAV $url")

                                    if (handleAppDeepLink(uri)) return true
                                    if (url.startsWith("intent:")) return handleIntentUrl(parent, url)
                                    if (uri.scheme !in listOf("http", "https", "about", "javascript")) {
                                        return handleCustomScheme(parent, uri)
                                    }
                                    parent.loadUrl(url)
                                    return true
                                }

                                @Deprecated("Deprecated in Java")
                                override fun shouldOverrideUrlLoading(v: WebView, url: String): Boolean {
                                    val uri = Uri.parse(url)
                                    android.util.Log.d("TossWebView", "POPUP NAV(legacy) $url")
                                    if (handleAppDeepLink(uri)) return true
                                    if (url.startsWith("intent:")) return handleIntentUrl(parent, url)
                                    if (uri.scheme !in listOf("http", "https", "about", "javascript")) {
                                        return handleCustomScheme(parent, uri)
                                    }
                                    parent.loadUrl(url)
                                    return true
                                }
                            }
                        }
                        transport.webView = popup
                        resultMsg.sendToTarget()
                        return true
                    }
                }

                webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                        android.util.Log.d("TossWebView", "onPageStarted $url")
                    }

                    override fun onPageFinished(view: WebView, url: String) {
                        android.util.Log.d("TossWebView", "onPageFinished $url")
                    }

                    override fun onReceivedError(
                        view: WebView,
                        request: android.webkit.WebResourceRequest,
                        error: android.webkit.WebResourceError
                    ) {
                        android.util.Log.e(
                            "TossWebView",
                            "onReceivedError main=${request.isForMainFrame} ${error.errorCode} ${error.description}"
                        )
                    }

                    override fun onReceivedHttpError(
                        view: WebView,
                        request: android.webkit.WebResourceRequest,
                        errorResponse: android.webkit.WebResourceResponse
                    ) {
                        android.util.Log.e(
                            "TossWebView",
                            "onReceivedHttpError main=${request.isForMainFrame} ${errorResponse.statusCode}"
                        )
                    }

                    override fun shouldOverrideUrlLoading(
                        view: WebView,
                        request: android.webkit.WebResourceRequest
                    ): Boolean {
                        val uri = request.url
                        val url = uri.toString()
                        android.util.Log.d("TossWebView", "NAV $url")

                        if (handleAppDeepLink(uri)) return true
                        if (url.startsWith("intent:")) return handleIntentUrl(view, url)
                        if (uri.scheme !in listOf("http", "https", "about", "javascript")) {
                            return handleCustomScheme(view, uri)
                        }
                        return false
                    }

                    @Deprecated("Deprecated in Java")
                    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                        val uri = Uri.parse(url)
                        android.util.Log.d("TossWebView", "NAV(legacy) $url")

                        if (handleAppDeepLink(uri)) return true
                        if (url.startsWith("intent:")) return handleIntentUrl(view, url)
                        if (uri.scheme !in listOf("http", "https", "about", "javascript")) {
                            return handleCustomScheme(view, uri)
                        }
                        return false
                    }
                }

                // baseURL을 https로 주면 외부 스크립트/리다이렉트 CSP 이슈 감소
                loadDataWithBaseURL(
                    "https://docs.tosspayments.com",
                    html,
                    "text/html",
                    "UTF-8",
                    null
                )
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

private fun urlEnc(s: String) = URLEncoder.encode(s, "UTF-8")
