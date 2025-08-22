package com.refit.app.ui.screen

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.refit.app.util.auth.PostcodeBridge

private const val POSTCODE_HTML = """
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="utf-8"/>
<meta name="viewport" content="width=device-width, initial-scale=1"/>
<script src="https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
<style> html,body,#wrap{height:100vh;margin:0;padding:0;} </style>
</head>
<body>
<div id="wrap"></div>
<script>
  (function() {
    var wrap = document.getElementById('wrap');
    new daum.Postcode({
      oncomplete: function(data) {
        if (window.Android && window.Android.onSelected) {
          window.Android.onSelected(String(data.zonecode), String(data.roadAddress || ""));
        }
      },
      width: "100%",
      height: "100%"
    }).embed(wrap);
  })();
</script>
</body>
</html>
"""

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun AddressSearchDialog(
    onDismiss: () -> Unit,
    onSelected: (zonecode: String, roadAddress: String) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                WebView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.cacheMode = WebSettings.LOAD_DEFAULT
                    webViewClient = object : WebViewClient() {}
                    webChromeClient = WebChromeClient()

                    addJavascriptInterface(PostcodeBridge { zone, road ->
                        onSelected(zone, road)
                        onDismiss()
                    }, "Android")

                    loadDataWithBaseURL(
                        "https://t1.daumcdn.net/",
                        POSTCODE_HTML,
                        "text/html",
                        "utf-8",
                        null
                    )
                }
            }
        )
    }
}