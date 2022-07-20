package com.aani.core.functions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import androidx.core.view.ViewCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

fun View.hideView() {
    if (visibility == View.VISIBLE) {
        if (isLaidOut && !isInEditMode) {
            animate().scaleX(0.0f).scaleY(0.0f).alpha(0.0f).setDuration(20L)
                .setInterpolator(FastOutSlowInInterpolator())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        visibility = View.VISIBLE
                    }

                    override fun onAnimationCancel(animation: Animator) {}
                    override fun onAnimationEnd(animation: Animator) {
                        visibility = View.GONE
                    }
                })
        } else {
            visibility = View.GONE
        }
    }
}

fun View.showView() {
    if (visibility != View.VISIBLE) {
        if (isLaidOut && !isInEditMode) {
            alpha = 0.0f
            scaleY = 0.0f
            scaleX = 0.0f
            animate().scaleX(1.0f).scaleY(1.0f).alpha(1.0f).setDuration(20L)
                .setInterpolator(FastOutSlowInInterpolator())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        visibility = View.VISIBLE
                    }
                })
        } else {
            visibility = View.VISIBLE
            alpha = 1.0f
            scaleY = 1.0f
            scaleX = 1.0f
        }
    }
}

fun expand(v: View) {
    val matchParentMeasureSpec =
        View.MeasureSpec.makeMeasureSpec((v.parent as View).width, View.MeasureSpec.EXACTLY)
    val wrapContentMeasureSpec =
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    v.measure(matchParentMeasureSpec, wrapContentMeasureSpec)
    val targetHeight = v.measuredHeight
    // Older versions of android (pre API 21) cancel animations for views with a height of 0.
    v.layoutParams.height = 1
    v.visibility = View.VISIBLE
    val a: Animation = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            v.layoutParams.height =
                if (interpolatedTime == 1f) ViewGroup.LayoutParams.MATCH_PARENT else (targetHeight * interpolatedTime).toInt()
            v.requestLayout()
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }

    // Expansion speed of 1dp/ms
    a.duration = 250
    v.startAnimation(a)
}

fun expandWrap(v: View) {
    val matchParentMeasureSpec =
        View.MeasureSpec.makeMeasureSpec((v.parent as View).width, View.MeasureSpec.EXACTLY)
    val wrapContentMeasureSpec =
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    v.measure(matchParentMeasureSpec, wrapContentMeasureSpec)
    val targetHeight = v.measuredHeight
    // Older versions of android (pre API 21) cancel animations for views with a height of 0.
    v.layoutParams.height = 1
    v.visibility = View.VISIBLE
    val a: Animation = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            v.layoutParams.height =
                if (interpolatedTime == 1f) ViewGroup.LayoutParams.WRAP_CONTENT else (targetHeight * interpolatedTime).toInt()
            v.requestLayout()
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }

    // Expansion speed of 1dp/ms
    a.duration = 250
    v.startAnimation(a)
}

fun collapse(v: View) {
    val initialHeight = v.measuredHeight
    val a: Animation = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            if (interpolatedTime == 1f) {
                v.visibility = View.GONE
            } else {
                v.layoutParams.height =
                    initialHeight - (initialHeight * interpolatedTime).toInt()
                v.requestLayout()
            }
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }

    // Collapse speed of 1dp/ms
    a.duration = 250
    v.startAnimation(a)
}

fun View.colorTransition(endColor: Int, duration: Long = 250L){
    var colorFrom = Color.TRANSPARENT
    if (background is ColorDrawable)
        colorFrom = (background as ColorDrawable).color

    val colorAnimation: ValueAnimator = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, endColor)
    colorAnimation.duration = duration

    colorAnimation.addUpdateListener {
        if (it.animatedValue is Int) {
            val color=it.animatedValue as Int
            setBackgroundColor(color)
        }
    }
    colorAnimation.start()
}
