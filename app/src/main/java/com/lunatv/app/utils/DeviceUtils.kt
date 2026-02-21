package com.lunatv.app.utils

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import android.view.View

object DeviceUtils {
    private const val TAG = "DeviceUtils"

    fun isTvDevice(context: Context): Boolean {
        val uiMode = context.resources.configuration.uiMode
        val isUiModeTv = (uiMode and Configuration.UI_MODE_TYPE_MASK) == Configuration.UI_MODE_TYPE_TELEVISION
        
        val isLeanback = context.packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK)
        
        val isTelevision = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.packageManager.hasSystemFeature(PackageManager.FEATURE_TELEVISION)
        } else {
            false
        }
        
        val isAndroidTv = context.packageManager.hasSystemFeature("android.software.leanback")
        
        val isNoTouchScreen = context.packageManager.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN).not()
        
        val hasNoTouch = context.resources.configuration.touchscreen == Configuration.TOUCHSCREEN_NOTOUCH
        
        val isTv = isUiModeTv || isLeanback || isTelevision || isAndroidTv || (isNoTouchScreen && hasNoTouch)
        
        Log.d(TAG, "Device detection: uiModeTv=$isUiModeTv, leanback=$isLeanback, " +
                "television=$isTelevision, androidTv=$isAndroidTv, " +
                "noTouchScreen=$isNoTouchScreen, noTouch=$hasNoTouch, isTv=$isTv")
        
        return isTv
    }

    fun isPhoneDevice(context: Context): Boolean {
        val isMobile = context.packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)
        val isSmallScreen = context.resources.configuration.screenLayout and 
                Configuration.SCREENLAYOUT_SIZE_MASK < Configuration.SCREENLAYOUT_SIZE_LARGE
        return isMobile && !isTvDevice(context) && isSmallScreen
    }

    fun isTabletDevice(context: Context): Boolean {
        val isLargeScreen = context.resources.configuration.screenLayout and 
                Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
        
        val isXlargeScreen = context.resources.configuration.screenLayout and 
                Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_XLARGE
        
        val isNotTv = !isTvDevice(context)
        val isNotPhone = !context.packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)
        
        return (isLargeScreen || isXlargeScreen) && isNotTv && isNotPhone
    }

    fun getDeviceType(context: Context): DeviceType {
        return when {
            isTvDevice(context) -> DeviceType.TV
            isTabletDevice(context) -> DeviceType.TABLET
            isPhoneDevice(context) -> DeviceType.PHONE
            else -> DeviceType.PHONE
        }
    }

    fun enableImmersiveMode(window: android.view.Window) {
        val decorView = window.decorView
        val flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        
        decorView.systemUiVisibility = flags
        
        decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                decorView.systemUiVisibility = flags
            }
        }
    }

    fun getScreenDensity(context: Context): Float {
        return context.resources.displayMetrics.density
    }

    fun getScreenWidth(context: Context): Int {
        return context.resources.displayMetrics.widthPixels
    }

    fun getScreenHeight(context: Context): Int {
        return context.resources.displayMetrics.heightPixels
    }
}

enum class DeviceType {
    PHONE,
    TABLET,
    TV
}
