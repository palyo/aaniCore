package com.aani.core.functions.constant

import android.os.Build
import android.telephony.PhoneNumberUtils
import android.text.Html
import android.text.Spanned
import java.text.Normalizer
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

fun String.toHtmlSpan(): Spanned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
    Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
} else {
    Html.fromHtml(this)
}

val randomUUIDString get() = UUID.randomUUID().toString()

val normalizeRegex = "\\p{InCombiningDiacriticalMarks}+".toRegex()

fun String.areDigitsOnly() = matches(Regex("[0-9]+"))
fun String.normalizePhoneNumber(): String = PhoneNumberUtils.normalizeNumber(this)
fun String.normalizeString() = Normalizer.normalize(this, Normalizer.Form.NFD).replace(normalizeRegex, "")

fun String.getExtractDigits() = extractDigits()
fun String.getFilenameExtension() = substring(lastIndexOf(".") + 1)

fun String.extractDigits(): String? {
    var str = this
    var p = Pattern.compile("(\\d{4})", Pattern.MULTILINE or Pattern.CASE_INSENSITIVE)
    var m: Matcher = p.matcher(str)
    var otp = if (m.find()) {
        if (m.groupCount() == 1) {
            m.group(0)
        } else ""
    } else ""

    if (otp.isEmpty()) {
        p = Pattern.compile("(\\d{6})", Pattern.MULTILINE or Pattern.CASE_INSENSITIVE)
        m = p.matcher(str)
        otp = if (m.find()) {
            if (m.groupCount() == 1) {
                m.group(0)
            } else ""
        } else ""
    }

    if (otp.isEmpty()) {
        p = Pattern.compile("(\\d{8})", Pattern.MULTILINE or Pattern.CASE_INSENSITIVE)
        m = p.matcher(str)
        otp = if (m.find()) {
            if (m.groupCount() == 1) {
                m.group(0)
            } else ""
        } else ""
    }

    return otp
}
