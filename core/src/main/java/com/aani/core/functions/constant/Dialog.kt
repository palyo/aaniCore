package com.aani.core.functions.constant

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.google.android.material.bottomsheet.BottomSheetDialog

@SuppressLint("ClickableViewAccessibility")
fun configDialog(activity: Activity, dialog: BottomSheetDialog){
    val params = dialog.window!!.attributes
    params.width = WindowManager.LayoutParams.MATCH_PARENT
    params.height = WindowManager.LayoutParams.MATCH_PARENT
    params.gravity = Gravity.CENTER
    dialog.window!!.attributes = params
    dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    dialog.findViewById<View>(com.google.android.material.R.id.touch_outside)?.apply {
        setOnTouchListener { v, event ->
            event.setLocation(event.rawX - v.x, event.rawY - v.y)

            activity.dispatchTouchEvent(event)
            false
        }
    }

    dialog.setCanceledOnTouchOutside(false)
    dialog.window!!.setDimAmount(.0f)

    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
}