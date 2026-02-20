package com.lunatv.app.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.lunatv.app.R
import com.lunatv.app.utils.AppConstants
import com.lunatv.app.utils.DeviceType

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Log.d("MainActivity", "Running on ${deviceType} device")
        
        if (deviceType == DeviceType.TV) {
            Toast.makeText(this, R.string.tv_mode_detected, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (deviceType != DeviceType.TV) {
            menuInflater.inflate(R.menu.menu_main, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                webView.reload()
                true
            }
            R.id.action_fullscreen -> {
                DeviceUtils.enableImmersiveMode(window)
                true
            }
            R.id.action_settings -> {
                showSettingsDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showSettingsDialog() {
        val options = arrayOf(
            getString(R.string.clear_cache),
            getString(R.string.clear_cookies),
            getString(R.string.reset_url)
        )
        
        AlertDialog.Builder(this)
            .setTitle(R.string.settings)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        webView.clearCache(true)
                        Toast.makeText(this, R.string.cache_cleared, Toast.LENGTH_SHORT).show()
                    }
                    1 -> {
                        webView.clearCookies()
                        Toast.makeText(this, R.string.cookies_cleared, Toast.LENGTH_SHORT).show()
                    }
                    2 -> {
                        val prefs = getSharedPreferences(AppConstants.PREF_NAME, MODE_PRIVATE)
                        prefs.edit().remove(AppConstants.PREF_KEY_URL).apply()
                        currentUrl = AppConstants.DEFAULT_URL
                        webView.loadUrl(currentUrl)
                        Toast.makeText(this, R.string.url_reset, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .show()
    }
}
