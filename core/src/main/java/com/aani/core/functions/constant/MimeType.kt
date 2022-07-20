package com.aani.core.functions

import android.webkit.MimeTypeMap
import java.util.*

const val UNKNOWN_MIME_TYPE = "unknown/unknown"

fun String.getMimeType(): String {
    var index: Int
    if (this.lastIndexOf('.').also { index = it } == -1) return UNKNOWN_MIME_TYPE
    val mime =
        MimeTypeMap.getSingleton().getMimeTypeFromExtension(this.substring(index + 1).lowercase(
            Locale.getDefault()))
    return mime ?: UNKNOWN_MIME_TYPE
}

fun String.getGenericMIME(): String {
    return split("/".toRegex()).toTypedArray()[0] + "/*"
}

fun String.getTypeMime(): String {
    return split("/".toRegex()).toTypedArray()[0]
}