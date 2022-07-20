package com.aani.core.functions.constant

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager

fun Context.enableWidget(widgetClass: Class<*>) {
    packageManager.setComponentEnabledSetting(ComponentName(applicationContext,
        widgetClass),
        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
        PackageManager.DONT_KILL_APP)
}

fun Context.disableWidget(widgetClass: Class<*>) {
    packageManager.setComponentEnabledSetting(ComponentName(applicationContext,
        widgetClass),
        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
        PackageManager.DONT_KILL_APP)
}