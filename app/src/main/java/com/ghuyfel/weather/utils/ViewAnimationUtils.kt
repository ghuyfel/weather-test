package com.ghuyfel.weather.utils

import android.content.Context
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.ghuyfel.weather.R

object ViewAnimationUtils {

    fun fadeIn(context: Context): Animation = AnimationUtils.loadAnimation(
        context.applicationContext,
        R.anim.fade_in
    )

    fun fadeOut(context: Context): Animation =  AnimationUtils.loadAnimation(
        context.applicationContext,
        android.R.anim.fade_out
    )

    fun fadeIn(view: View) {
        val aniFade = fadeIn(view.context)
        aniFade.setAnimationListener(OnAnimationCompletedListener(view, true))
        view.startAnimation(aniFade)
    }

    fun fadeOut(view: View) {
        val aniFade = fadeOut(view.context)
        aniFade.setAnimationListener(OnAnimationCompletedListener(view, false))
        view.startAnimation(aniFade)
    }

    class OnAnimationCompletedListener(
        private val view: View,
        private val shouldBeVisible: Boolean
    ) : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation?) {

        }

        override fun onAnimationEnd(animation: Animation?) {
            view.visibility = if (shouldBeVisible) VISIBLE else GONE
        }

        override fun onAnimationRepeat(animation: Animation?) {

        }

    }
}