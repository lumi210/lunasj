package com.lunatv.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.lunatv.app.ui.MainActivity
import com.lunatv.app.ui.TVMainActivity
import com.lunatv.app.utils.DeviceType
import com.lunatv.app.utils.DeviceUtils

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val deviceType = DeviceUtils.getDeviceType(this)
        Log.d("SplashActivity", "Device type detected: $deviceType")
        
        val intent = when (deviceType) {
            DeviceType.TV -> Intent(this, TVMainActivity::class.java)
            else -> Intent(this, MainActivity::class.java)
        }
        
        startActivity(intent)
        finish()
    }
}
