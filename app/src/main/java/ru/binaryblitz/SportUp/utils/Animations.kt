package ru.binaryblitz.SportUp.utils

import android.animation.Animator
import android.app.Activity
import android.graphics.Point
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import io.codetail.animation.ViewAnimationUtils

object Animations {
    private val ANIMATION_DURATION = 200

    fun animateRevealShow(view: View?, activity: Activity) {
        if (view == null) {
            return
        }

        val animator = ViewAnimationUtils.createCircularReveal(view, 0, 0, 0f, getRadius(activity).toFloat())
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.duration = ANIMATION_DURATION.toLong()
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {
                view.visibility = View.VISIBLE
                view.bringToFront()
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

    fun animateRevealHide(view: View?) {
        if (view == null) {
            return
        }

        val animator = ViewAnimationUtils.createCircularReveal(view, 0, 0, view.width.toFloat(), 0f)
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.duration = ANIMATION_DURATION.toLong()
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {}

            override fun onAnimationEnd(animator: Animator) {
                view.visibility = View.GONE
            }

            override fun onAnimationCancel(animator: Animator) {}

            override fun onAnimationRepeat(animator: Animator) {}
        })
        animator.start()
    }
}
