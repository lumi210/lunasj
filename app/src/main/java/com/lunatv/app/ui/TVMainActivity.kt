package com.lunatv.app.ui

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.RowPresenter
import com.lunatv.app.R

class TVMainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TVMainActivity", "TV Mode initialized")
        Toast.makeText(this, R.string.tv_mode_welcome, Toast.LENGTH_LONG).show()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                webView.evaluateJavascript(
                    "if(typeof togglePlay === 'function') { togglePlay(); }",
                    null
                )
                return true
            }
            KeyEvent.KEYCODE_MEDIA_REWIND -> {
                webView.evaluateJavascript(
                    "if(typeof seekBackward === 'function') { seekBackward(10); }",
                    null
                )
                return true
            }
            KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> {
                webView.evaluateJavascript(
                    "if(typeof seekForward === 'function') { seekForward(10); }",
                    null
                )
                return true
            }
            KeyEvent.KEYCODE_MEDIA_STOP -> {
                webView.evaluateJavascript(
                    "if(typeof stopVideo === 'function') { stopVideo(); }",
                    null
                )
                return true
            }
            KeyEvent.KEYCODE_MENU -> {
                showTvMenu()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun showTvMenu() {
        Toast.makeText(this, R.string.press_menu_again, Toast.LENGTH_SHORT).show()
    }
}
