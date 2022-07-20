package com.aani.core.functions

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import java.io.*
import java.util.*

    fun Context.installAPK(apkFile: File?) {
        val apkUri =
            FileProvider.getUriForFile(applicationContext, packageName + ".provider", apkFile!!)
        val intent: Intent = Intent(Intent.ACTION_INSTALL_PACKAGE)
        intent.data = apkUri
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivity(intent)
    }

    fun installAPKUsb(context: Context, apkFile: DocumentFile) {
        val apkUri = apkFile.uri
        val intent: Intent = Intent(Intent.ACTION_INSTALL_PACKAGE)
        intent.data = apkUri
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        context.startActivity(intent)
    }

    fun uninstallAPK(context: Context, apkPackageName: String) {
        val intent = Intent("android.intent.action.DELETE")
        intent.data = Uri.parse("package:$apkPackageName")
        context.startActivity(intent)
    }

    fun infoScreen(context: Context, apkPackageName: String) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:$apkPackageName")
        context.startActivity(intent)
    }

    fun playStoreIntent(context: Context, apkPackageName: String) {
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$apkPackageName")
                )
            )
        } catch (e: ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$apkPackageName")
                )
            )
        }
    }

    fun Activity.getAppName( packageName: String?): String {
        return try {
            val packageManager = packageManager
            val info =
                packageManager.getApplicationInfo(packageName!!, PackageManager.GET_META_DATA)
            packageManager.getApplicationLabel(info) as String
        } catch (e: NameNotFoundException) {
            e.printStackTrace()
            ""
        }
    }

    fun Activity.saveAPK(packageName: String?,destination:File): File? {
        try {
            val file = getApkFile(packageName)
            createOrExistsDir(destination)
            val original =
                File(destination.absolutePath + "/" + getAppName(packageName) + ".apk")
            if (!original.exists()) original.createNewFile()
            val `in`: InputStream = FileInputStream(file)
            val out: OutputStream = FileOutputStream(original)
            val buf = ByteArray(1024)
            var len: Int
            while (`in`.read(buf).also { len = it } > 0) {
                out.write(buf, 0, len)
            }
            `in`.close()
            out.close()
            println("File copied.")
            return original
        } catch (ex: FileNotFoundException) {
            println(ex.message + " in the specified directory.")
        } catch (e: IOException) {
            println(e.message)
        }
        return null
    }

    private fun isValid(packageInfos: List<PackageInfo>?): Boolean {
        return packageInfos != null && packageInfos.isNotEmpty()
    }

    private fun Context.getAllInstalledApkFiles(): HashMap<String?, String> {
        val installedApkFilePaths = HashMap<String?, String>()
        val packageManager = packageManager
        val packageInfoList = packageManager.getInstalledPackages(PackageManager.SIGNATURE_MATCH)
        if (isValid(packageInfoList)) {
            for (packageInfo in packageInfoList) {
                var applicationInfo: ApplicationInfo = packageInfo.applicationInfo
                val packageName = applicationInfo.packageName
                val apkFile = File(applicationInfo.publicSourceDir)
                if (apkFile.exists()) {
                    installedApkFilePaths[packageName] = apkFile.absolutePath
                }
            }
        }
        return installedApkFilePaths
    }

    fun Context.getApkFile( packageName: String?): File {
        val installedApkFilePaths = getAllInstalledApkFiles()
        val apkFile = File(installedApkFilePaths[packageName]!!)
        return if (apkFile.exists()) {
            apkFile
        } else null!!
    }

    fun Activity.openApp( packageName: String?) {
        if (isAppInstalled( packageName)) {
            if (packageManager.getLaunchIntentForPackage(packageName!!) != null) {
                startActivity(packageManager.getLaunchIntentForPackage(packageName))
            } else {
                Toast.makeText(applicationContext, "Couldn't open", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(applicationContext, "App not installed", Toast.LENGTH_SHORT).show()
        }
    }

    fun Activity.isAppInstalled( packageName: String?): Boolean {
        val pm = packageManager
        try {
            pm.getPackageInfo(packageName!!, PackageManager.GET_ACTIVITIES)
            return true
        } catch (e: NameNotFoundException) {
        }
        return false
    }
