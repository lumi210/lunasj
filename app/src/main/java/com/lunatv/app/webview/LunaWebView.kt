package com.lunatv.app.webview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.ConsoleMessage
import android.webkit.CookieManager
import android.webkit.SslErrorHandler
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import com.lunatv.app.utils.AppConstants
import com.lunatv.app.utils.DeviceType
import com.lunatv.app.utils.DeviceUtils

class LunaWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var webView: WebView
    private var deviceType: DeviceType
    private var isTvMode: Boolean = false
    private var focusedView: View? = null

    var onPageStarted: ((String?) -> Unit)? = null
    var onPageFinished: ((String?) -> Unit)? = null
    var onLoadingProgress: ((Int) -> Unit)? = null
    var onError: ((String) -> Unit)? = null

    init {
        deviceType = DeviceUtils.getDeviceType(context)
        isTvMode = deviceType == DeviceType.TV
        
        webView = WebView(context).apply {
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            )
        }
        addView(webView)
        
        setupWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            cacheMode = WebSettings.LOAD_DEFAULT
            
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false
            
            loadWithOverviewMode = true
            useWideViewPort = true
            
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                isAlgorithmicDarkeningAllowed = true
            }
            
            userAgentString = when (deviceType) {
                DeviceType.TV -> AppConstants.USER_AGENT_TV
                DeviceType.TABLET -> AppConstants.USER_AGENT_TABLET
                DeviceType.PHONE -> AppConstants.USER_AGENT_PHONE
            }
            
            mediaPlaybackRequiresUserGesture = false
            
            setSupportMultipleWindows(true)
            javaScriptCanOpenWindowsAutomatically = true
        }
        
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                onPageStarted?.invoke(url)
                Log.d("LunaWebView", "Page started: $url")
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                onPageFinished?.invoke(url)
                Log.d("LunaWebView", "Page finished: $url")
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val errorMsg = error?.description?.toString() ?: "Unknown error"
                    onError?.invoke(errorMsg)
                    Log.e("LunaWebView", "Error: $errorMsg")
                }
            }

            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                handler?.proceed()
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url?.toString() ?: return false
                Log.d("LunaWebView", "Loading URL: $url")
                return false
            }
        }
        
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                onLoadingProgress?.invoke(newProgress)
            }

            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                Log.d("WebViewConsole", "${consoleMessage?.message()}")
                return true
            }
        }
        
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        cookieManager.setAcceptThirdPartyCookies(webView, true)
        
        webView.isFocusable = true
        webView.isFocusableInTouchMode = true
    }

    fun loadUrl(url: String) {
        Log.d("LunaWebView", "Loading URL: $url")
        webView.loadUrl(url)
    }

    fun reload() {
        webView.reload()
    }

    fun goBack(): Boolean {
        if (webView.canGoBack()) {
            webView.goBack()
            return true
        }
        return false
    }

    fun goForward() {
        if (webView.canGoForward()) {
            webView.goForward()
        }
    }

    fun canGoBack(): Boolean = webView.canGoBack()

    fun canGoForward(): Boolean = webView.canGoForward()

    fun getCurrentUrl(): String? = webView.url

    fun evaluateJavascript(script: String, resultCallback: ValueCallback<String>?) {
        webView.evaluateJavascript(script, resultCallback)
    }

    fun addJavascriptInterface(obj: Any, interfaceName: String) {
        webView.addJavascriptInterface(obj, interfaceName)
    }

    fun removeJavascriptInterface(interfaceName: String) {
        webView.removeJavascriptInterface(interfaceName)
    }

    fun clearCache(includeDiskFiles: Boolean) {
        webView.clearCache(includeDiskFiles)
    }

    fun clearHistory() {
        webView.clearHistory()
    }

    fun clearCookies() {
        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().flush()
    }

    fun pauseWebView() {
        webView.onPause()
    }

    fun resumeWebView() {
        webView.onResume()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (isTvMode) {
            when (keyCode) {
                KeyEvent.KEYCODE_DPAD_CENTER,
                KeyEvent.KEYCODE_ENTER -> {
                    return super.onKeyDown(keyCode, event)
                }
                KeyEvent.KEYCODE_DPAD_UP,
                KeyEvent.KEYCODE_DPAD_DOWN,
                KeyEvent.KEYCODE_DPAD_LEFT,
                KeyEvent.KEYCODE_DPAD_RIGHT -> {
                    return super.onKeyDown(keyCode, event)
                }
                KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
                KeyEvent.KEYCODE_MEDIA_PLAY,
                KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                    evaluateJavascript(
                        "if(typeof togglePlay === 'function') { togglePlay(); }",
                        null
                    )
                    return true
                }
                KeyEvent.KEYCODE_MEDIA_REWIND -> {
                    evaluateJavascript(
                        "if(typeof seekBackward === 'function') { seekBackward(); }",
                        null
                    )
                    return true
                }
                KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> {
                    evaluateJavascript(
                        "if(typeof seekForward === 'function') { seekForward(); }",
                        null
                    )
                    return true
                }
                KeyEvent.KEYCODE_BACK -> {
                    if (goBack()) {
                        return true
                    }
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    fun destroy() {
        webView.stopLoading()
        webView.settings.javaScriptEnabled = false
        webView.removeJavascriptInterface("AndroidInterface")
        webView.destroy()
    }
}
