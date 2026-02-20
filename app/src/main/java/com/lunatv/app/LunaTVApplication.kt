package com.lunatv.app

import android.app.Application
import android.util.Log
import android.webkit.WebView

class LunaTVApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        Log.d("LunaTV", "Application starting...")
        
        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
    }
}
