package com.aani.core.functions

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.text.TextUtils
import android.text.format.DateFormat
import android.util.Log
import java.io.File
import java.io.IOException
import java.util.*
import java.util.regex.Pattern

val DIR_SEPARATOR = Pattern.compile("/")
val DEFAULT_FALLBACK_STORAGE_PATH = "/storage/sdcard0"

fun Context.freeRamMemorySize(): Long {
    val mi = ActivityManager.MemoryInfo()
    val activityManager =
        getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    activityManager.getMemoryInfo(mi)
    return mi.availMem / 1048576L
}

fun Context.totalRamMemorySize(): Long {
    val mi = ActivityManager.MemoryInfo()
    val activityManager =
        getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    activityManager.getMemoryInfo(mi)
    return mi.totalMem / 1048576L
}

val isSDPresent: Boolean
    get() {
        val isSDPresent = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
        val isSDSupportedDevice = Environment.isExternalStorageRemovable()
        return isSDSupportedDevice && isSDPresent
    }

fun Activity.storageDirectories(): List<String>{
    val rv: MutableList<String> = ArrayList()
    val rawExternalStorage = System.getenv("EXTERNAL_STORAGE")
    val rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE")
    val rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET")
    if (TextUtils.isEmpty(rawEmulatedStorageTarget)) {
        if (TextUtils.isEmpty(rawExternalStorage)) {
            if (File(DEFAULT_FALLBACK_STORAGE_PATH).exists()) {
                rv.add(DEFAULT_FALLBACK_STORAGE_PATH)
            } else {
                rv.add(Environment.getExternalStorageDirectory().absolutePath)
            }
        } else {
            rv.add(rawExternalStorage)
        }
    } else {
        val rawUserId: String
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            rawUserId = ""
        } else {
            val path = Environment.getExternalStorageDirectory().absolutePath
            val folders = DIR_SEPARATOR.split(path)
            val lastFolder = folders[folders.size - 1]
            var isDigit = false
            try {
                Integer.valueOf(lastFolder)
                isDigit = true
            } catch (ignored: NumberFormatException) {
            }
            rawUserId = if (isDigit) lastFolder else ""
        }
        if (TextUtils.isEmpty(rawUserId)) {
            rv.add(rawEmulatedStorageTarget)
        } else {
            rv.add(rawEmulatedStorageTarget + File.separator + rawUserId)
        }
    }
    if (!TextUtils.isEmpty(rawSecondaryStoragesStr)) {
        val rawSecondaryStorages =
            rawSecondaryStoragesStr.split(File.pathSeparator).toTypedArray()
        Collections.addAll(rv, *rawSecondaryStorages)
    }
    rv.clear()
    val strings = getExtSdCardPathsForActivity()
    for (s in strings) {
        val f = File(s)
        if (!rv.contains(s) && canListFiles(f)) rv.add(s)
    }
    val usb = usbDrive
    if (usb != null && !rv.contains(usb.path)) {
        rv.add(usb.path)
        Log.e("getUsbDrive", ": " + usb.path)
    }
//    if (SingletonUsbOtg.instance!!.isDeviceConnected) {
//        rv.add("otg:/" + "/")
//    }
    return rv
}

val usbDrive: File?
    get() {
        var parent = File("/storage")
        try {
            for (f in parent.listFiles()) if (f.exists() && f.name.lowercase(Locale.getDefault())
                    .contains("usb") && f.canExecute()
            ) return f
        } catch (e: Exception) {
        }
        parent = File("/mnt/sdcard/usbStorage")
        if (parent.exists() && parent.canExecute()) return parent
        parent = File("/mnt/sdcard/usb_storage")
        return if (parent.exists() && parent.canExecute()) parent else null
    }

fun canListFiles(f: File): Boolean {
    return f.canRead() && f.isDirectory
}

fun Activity.getExtSdCardPathsForActivity(): Array<String> {
    val paths: MutableList<String> = ArrayList()
    for (file in getExternalFilesDirs("external")) {
        if (file != null) {
            val index = file.absolutePath.lastIndexOf("/Android/data")
            if (index < 0) {
                Log.w("LOG", "Unexpected external file dir: " + file.absolutePath)
            } else {
                var path = file.absolutePath.substring(0, index)
                try {
                    path = File(path).canonicalPath
                } catch (e: IOException) {
                    // Keep non-canonical path.
                }
                paths.add(path)
            }
        }
    }
    if (paths.isEmpty()) paths.add("/storage/sdcard1")
    return paths.toTypedArray()
}

val availableInternalMemorySize: Long
    get() {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSize.toLong()
        val availableBlocks = stat.availableBlocks.toLong()
        return availableBlocks * blockSize
    }

fun getTotalInternalMemorySize(path: String?): String {
    val stat = StatFs(path)
    val blockSize = stat.blockSize.toLong()
    val totalBlocks = stat.blockCount.toLong()
    return formatSize((totalBlocks * blockSize).toDouble(), 2)
}

fun String.getTotalInternalMemorySizeLong(): Long {
    try {
        val stat = StatFs(this)
        val blockSize = stat.blockSize.toLong()
        val totalBlocks = stat.blockCount.toLong()
        return totalBlocks * blockSize
    } catch (e: Exception) {
        Log.e("TAG", "getTotalInternalMemorySizeLong: " + e.localizedMessage)
    }
    return 0
}

fun String.getAvailableExternalMemorySize(): String {
    return if (externalMemoryAvailable()) {
        val stat = StatFs(this)
        val bytesAvailable: Long =
            stat.blockSizeLong * stat.availableBlocksLong
        formatSize(bytesAvailable.toDouble(), 2)
    } else {
        ""
    }
}

fun String.getTotalExternalMemorySize(): String {
    return if (externalMemoryAvailable()) {
        val stat = StatFs(this)
        val blockSize = stat.blockSize.toLong()
        val totalBlocks = stat.blockCount.toLong()
        formatSize((totalBlocks * blockSize).toDouble(), 2)
    } else {
        ""
    }
}

fun formatSize(size: Double, digits: Int): String {
    var size = size
    val dictionary = arrayOf("bytes", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB")
    var index = 0
    index = 0
    while (index < dictionary.size) {
        if (size < 1024) {
            break
        }
        size = size / 1024
        index++
    }
    return String.format("%." + digits + "f", size) + " " + dictionary[index]
}

fun externalMemoryAvailable(): Boolean {
    return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
}