package com.refit.app.util.auth

import android.webkit.JavascriptInterface

class PostcodeBridge(
    private val onSelected: (zonecode: String, roadAddress: String) -> Unit
) {
    @JavascriptInterface
    fun onSelected(zonecode: String, roadAddress: String) {
        onSelected.invoke(zonecode, roadAddress)
    }
}