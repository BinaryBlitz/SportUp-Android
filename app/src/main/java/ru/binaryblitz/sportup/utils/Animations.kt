package ru.binaryblitz.sportup.utils

import android.animation.Animator
import android.app.Activity
import android.graphics.Point
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import io.codetail.animation.ViewAnimationUtils

object Animations {
    private val ANIMATION_DURATION = 200

    fun animateRevealShow(v: View, activity: Activity) {
        val animator = ViewAnimationUtils.createCircularReveal(v, 0, 0, 0f, getRadius(activity).toFloat())
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.duration = ANIMATION_DURATION.toLong()
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {
                v.visibility = View.VISIBLE
                v.bringToFront()
            }

            override fun onAnimationEnd(animator: Animator) {}

            override fun onAnimationCancel(animator: Animator) {}

            override fun onAnimationRepeat(animator: Animator) {}
        })
        animator.start()
    }

    private fun getRadius(activity: Activity): Int {
        val display = activity.windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x
        val height = size.y

        return Math.max(width, height)
    }

    fun animateRevealHide(v: View) {
        val animator = ViewAnimationUtils.createCircularReveal(v, 0, 0, v.width.toFloat(), 0f)
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.duration = ANIMATION_DURATION.toLong()
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {}

            override fun onAnimationEnd(animator: Animator) {
                v.visibility = View.GONE
            }

            override fun onAnimationCancel(animator: Animator) {}

            override fun onAnimationRepeat(animator: Animator) {}
        })
        animator.start()
    }
}
