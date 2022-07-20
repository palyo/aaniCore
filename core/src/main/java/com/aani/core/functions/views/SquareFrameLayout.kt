package com.aani.core.functions.views

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

class SquareFrameLayout : FrameLayout {
    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val measureSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        setMeasuredDimension(measureSpecSize, measureSpecSize)
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}