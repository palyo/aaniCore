package com.aani.core.functions.constant

import android.content.ContentResolver
import android.content.Context
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.view.Window
import android.view.WindowManager

fun Context.isAutoBrightnessEnabled(): Boolean {
    return try {
        val mode: Int = Settings.System.getInt(
            contentResolver,
            Settings.System.SCREEN_BRIGHTNESS_MODE
        )
        mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
    } catch (e: Settings.SettingNotFoundException) {
        e.printStackTrace()
        false
    }
}

fun Context.setAutoBrightnessEnabled(enabled: Boolean): Boolean {
    return Settings.System.putInt(
        contentResolver,
        Settings.System.SCREEN_BRIGHTNESS_MODE,
        if (enabled) Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC else Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
    )
}

fun Context.getBrightness(): Int {
    return try {
        Settings.System.getInt(
            contentResolver,
            Settings.System.SCREEN_BRIGHTNESS
        )
    } catch (e: SettingNotFoundException) {
        e.printStackTrace()
        0
    }
}

fun Context.setBrightness(
    @androidx.annotation.IntRange(from = 0,
        to = 255) brightness: Int,
): Boolean {
    val resolver: ContentResolver = contentResolver
    val b = Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, brightness)
    resolver.notifyChange(Settings.System.getUriFor("screen_brightness"), null)
    return b
}

fun Window.setWindowBrightness(
    @androidx.annotation.IntRange(from = 0,
        to = 255) brightness: Int,
) {
    val lp: WindowManager.LayoutParams = getAttributes()
    lp.screenBrightness = brightness / 255f
    attributes = lp
}

fun Context.getWindowBrightness(window: Window): Int {
    val lp: WindowManager.LayoutParams = window.attributes
    val brightness = lp.screenBrightness
    return if (brightness < 0) getBrightness() else (brightness * 255).toInt()
}