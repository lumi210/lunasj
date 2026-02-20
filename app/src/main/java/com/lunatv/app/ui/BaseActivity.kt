package com.lunatv.app.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.lunatv.app.R
import com.lunatv.app.utils.AppConstants
import com.lunatv.app.utils.DeviceType
import com.lunatv.app.utils.DeviceUtils
import com.lunatv.app.webview.LunaWebView

open class BaseActivity : AppCompatActivity() {

    protected lateinit var webView: LunaWebView
    protected lateinit var progressBar: ProgressBar
    protected lateinit var container: FrameLayout
    protected var deviceType: DeviceType = DeviceType.PHONE
    protected var currentUrl: String = AppConstants.DEFAULT_URL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        deviceType = DeviceUtils.getDeviceType(this)
        Log.d("BaseActivity", "Device type: $deviceType")
        
        setupWindow()
        
        setContentView(R.layout.activity_main)
        
        initViews()
        
        setupWebView()
        
        checkPermissions()
        
        loadInitialUrl()
    }

    private fun setupWindow() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )
        
        if (deviceType == DeviceType.TV) {
            DeviceUtils.enableImmersiveMode(window)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initViews() {
        container = findViewById(R.id.container)
        progressBar = findViewById(R.id.progressBar)
        webView = findViewById(R.id.webView)
    }

    private fun setupWebView() {
        webView.onPageStarted = { url ->
            runOnUiThread {
                progressBar.visibility = View.VISIBLE
                currentUrl = url ?: currentUrl
            }
        }
        
        webView.onPageFinished = { url ->
            runOnUiThread {
                progressBar.visibility = View.GONE
                currentUrl = url ?: currentUrl
            }
        }
        
        webView.onLoadingProgress = { progress ->
            runOnUiThread {
                progressBar.progress = progress
            }
        }
        
        webView.onError = { error ->
            runOnUiThread {
                Toast.makeText(this, getString(R.string.error_loading, error), Toast.LENGTH_SHORT).show()
            }
        }
    }

    protected open fun loadInitialUrl() {
        val prefs = getSharedPreferences(AppConstants.PREF_NAME, MODE_PRIVATE)
        currentUrl = prefs.getString(AppConstants.PREF_KEY_URL, AppConstants.DEFAULT_URL) 
            ?: AppConstants.DEFAULT_URL
        
        webView.loadUrl(currentUrl)
    }

    private fun checkPermissions() {
        val permissions = mutableListOf<String>()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) 
                != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        
        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissions.toTypedArray(),
                AppConstants.REQUEST_CODE_PERMISSIONS
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == AppConstants.REQUEST_CODE_PERMISSIONS) {
            grantResults.forEachIndexed { index, result ->
                if (result != PackageManager.PERMISSION_GRANTED) {
                    Log.w("BaseActivity", "Permission denied: ${permissions[index]}")
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onPause() {
        super.onPause()
        webView.pauseWebView()
    }

    override fun onResume() {
        super.onResume()
        webView.resumeWebView()
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }
}
