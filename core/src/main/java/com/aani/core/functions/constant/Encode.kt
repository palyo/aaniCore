package com.aani.core.functions.constant

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.Html
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.net.URLEncoder

fun Bitmap.getBase64FromBitmap(): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}

fun String.getBitmapFromEncodedString(): Bitmap {
    val decodedString = Base64.decode(this, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
}

fun String.urlEncode(charsetName: String? = "UTF-8"): String {
    return if (isEmpty()) "" else try {
        URLEncoder.encode(this, charsetName)
    } catch (e: UnsupportedEncodingException) {
        throw AssertionError(e)
    }
}

fun String.urlDecode(charsetName: String? = "UTF-8"): String {
    return if (isEmpty()) "" else try {
        URLDecoder.decode(this, charsetName)
    } catch (e: UnsupportedEncodingException) {
        throw AssertionError(e)
    }
}

fun String.base64Encode(): ByteArray {
    return toByteArray().base64Encode()
}

fun ByteArray.base64Encode(): ByteArray {
    return if (isEmpty()) ByteArray(0) else Base64.encode(this,
        Base64.NO_WRAP)
}

fun ByteArray.base64Encode2String(): String {
    return if (isEmpty()) "" else Base64.encodeToString(this,
        Base64.NO_WRAP)
}

fun String.base64Decode(): ByteArray {
    return if (isEmpty()) ByteArray(0) else Base64.decode(this,
        Base64.NO_WRAP)
}

fun ByteArray.base64Decode(): ByteArray {
    return if (size == 0) ByteArray(0) else Base64.decode(this, Base64.NO_WRAP)
}

fun CharSequence.htmlEncode(): String {
    if (length == 0) return ""
    val sb = StringBuilder()
    var c: Char
    var i = 0
    val len = length
    while (i < len) {
        c = this[i]
        when (c) {
            '<' -> sb.append("&lt;") //$NON-NLS-1$
            '>' -> sb.append("&gt;") //$NON-NLS-1$
            '&' -> sb.append("&amp;") //$NON-NLS-1$
            '\'' -> sb.append("&#39;") //$NON-NLS-1$
            '"' -> sb.append("&quot;") //$NON-NLS-1$
            else -> sb.append(c)
        }
        i++
    }
    return sb.toString()
}

fun String.htmlDecode(): CharSequence {
    if (length == 0) return ""
    return Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
}

fun String.binEncode(): String {
    val stringBuilder = StringBuilder()
    for (i in toCharArray()) {
        stringBuilder.append(Integer.toBinaryString(i.code))
        stringBuilder.append(' ')
    }
    return stringBuilder.toString()
}

fun String.binDecode(): String {
    val splatted = split(" ".toRegex()).toTypedArray()
    val sb = StringBuilder()
    for (i in splatted) {
        sb.append(i.replace(" ", "").toInt(2).toChar())
    }
    return sb.toString()
}
