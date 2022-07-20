package com.aani.core.functions.constant

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.FloatRange
import androidx.core.content.ContextCompat


fun Context.getColor(@ColorRes id: Int): Int {
    return ContextCompat.getColor(applicationContext, id)
}

fun Int.setAlphaComponent(
    @androidx.annotation.IntRange(from = 0x0, to = 0xFF) alpha: Int,
): Int {
    return this and 0x00ffffff or (alpha shl 24)
}

fun Int.setAlphaComponent(
    @FloatRange(from = 0.0, to = 1.0) alpha: Float,
): Int {
    return this and 0x00ffffff or ((alpha * 255.0f + 0.5f).toInt() shl 24)
}

fun Int.setRedComponent(
    @androidx.annotation.IntRange(from = 0x0, to = 0xFF) red: Int,
): Int {
    return this and -0xff0001 or (red shl 16)
}

fun Int.setRedComponent(
    @FloatRange(from = 0.0, to = 1.0) red: Float,
): Int {
    return this and -0xff0001 or ((red * 255.0f + 0.5f).toInt() shl 16)
}

fun Int.setGreenComponent(
    @androidx.annotation.IntRange(from = 0x0, to = 0xFF) green: Int,
): Int {
    return this and -0xff01 or (green shl 8)
}

fun Int.setGreenComponent(
    @FloatRange(from = 0.0, to = 1.0) green: Float,
): Int {
    return this and -0xff01 or ((green * 255.0f + 0.5f).toInt() shl 8)
}

fun Int.setBlueComponent(
    @androidx.annotation.IntRange(from = 0x0, to = 0xFF) blue: Int,
): Int {
    return this and -0x100 or blue
}

fun Int.setBlueComponent(
    @FloatRange(from = 0.0, to = 1.0) blue: Float,
): Int {
    return this and -0x100 or (blue * 255.0f + 0.5f).toInt()
}


fun String.string2Int(): Int {
    return Color.parseColor(this)
}

fun Int.int2RgbString(): String {
    var colorInt = this
    colorInt = colorInt and 0x00ffffff
    var color = Integer.toHexString(colorInt)
    while (color.length < 6) {
        color = "0$color"
    }
    return "#$color"
}

fun Int.int2ArgbString(): String {
    var color = Integer.toHexString(this)
    while (color.length < 6) {
        color = "0$color"
    }
    while (color.length < 8) {
        color = "f$color"
    }
    return "#$color"
}

fun getRandomColor(): Int {
    return true.getRandomColor()
}

fun Boolean.getRandomColor(): Int {
    val high = if (this) (Math.random() * 0x100).toInt() shl 24 else -0x1000000
    return high or (Math.random() * 0x1000000).toInt()
}