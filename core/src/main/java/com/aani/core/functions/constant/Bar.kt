package com.aani.core.functions

import android.R
import android.app.Activity
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.view.WindowManager


fun Activity.getActionBarHeight(): Int {
    val tv = TypedValue()
    return if (theme.resolveAttribute(R.attr.actionBarSize, tv, true)) {
        TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
    } else 0
}

fun Activity.isStatusBarVisible(): Boolean {
    val flags = window.attributes.flags
    return flags and WindowManager.LayoutParams.FLAG_FULLSCREEN == 0
}

fun Activity.setStatusBarLightMode(
    isLightMode: Boolean,
) {
    window.setStatusBarLightMode(isLightMode)
}

fun Window.setStatusBarLightMode(isLightMode: Boolean) {
    val decorView = decorView
    var vis = decorView.systemUiVisibility
    vis = if (isLightMode) {
        vis or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    } else {
        vis and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
    }
    decorView.systemUiVisibility = vis
}

fun Activity.isStatusBarLightMode(): Boolean {
    return window.isStatusBarLightMode()
}

fun Window.isStatusBarLightMode(): Boolean {
    val decorView = decorView
    val vis = decorView.systemUiVisibility
    return vis and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR != 0
    return false
}

fun Activity.getStatusBarHeight(): Int {
    var result = 0
    val resourceId =
        resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        result = resources.getDimensionPixelSize(resourceId)
    }
    return result
}

fun Activity.updateStatusBarColor(color: Int) {
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.statusBarColor = color
}

fun Activity.hasNavBar(): Boolean {
    val id = resources.getIdentifier("config_showNavigationBar", "bool", "android")
    return id > 0 && resources.getBoolean(id)
}

fun Activity.getNavigationBarHeight(): Int {
    val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
    return if (resourceId > 0) {
        resources.getDimensionPixelSize(resourceId)
    } else 0
}

fun Activity.setNavBarLightMode(isLightMode: Boolean) {
    window.setNavBarLightMode(isLightMode)
}

fun Window.setNavBarLightMode(
    isLightMode: Boolean,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val decorView = decorView
        var vis = decorView.systemUiVisibility
        vis = if (isLightMode) {
            vis or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        } else {
            vis and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
        }
        decorView.systemUiVisibility = vis
    }
}

fun Activity.isNavBarLightMode(): Boolean {
    return window.isNavBarLightMode()
}

fun Window.isNavBarLightMode(): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val decorView = decorView
        val vis = decorView.systemUiVisibility
        return vis and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR != 0
    }
    return false
}

fun Activity.updateNavBarColor(color: Int) {
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.navigationBarColor = color
}