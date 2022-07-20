package com.aani.core.functions

import android.content.Context
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import java.util.ArrayList

fun hasOTGConnected(context: Context): Boolean {
    var isOTG = false
    val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
    val deviceList = usbManager.deviceList
    var devices: MutableList<UsbDevice> = ArrayList()
    if (deviceList.size > 0) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            devices = ArrayList(deviceList.values)
        } else {
            devices = ArrayList()
            devices.addAll(deviceList.values)
        }
    }
    for (usbDevice in devices) {
        if (usbDevice.getInterface(0).interfaceClass == UsbConstants.USB_CLASS_MASS_STORAGE) {
            isOTG = true
            break
        }
    }
    return isOTG
}