package com.example.sudribet

import android.app.Activity
import android.content.Intent
import android.os.Build

object ActivityTransitions {

    fun navigate(activity: Activity, intent: Intent) {
        activity.startActivity(intent)
        applyTransition(activity, R.anim.slide_in_right, R.anim.slide_out_left)
    }

    fun navigateBack(activity: Activity) {
        activity.finish()
        applyTransition(activity, R.anim.slide_in_left, R.anim.slide_out_right)
    }

    fun navigateAndClear(activity: Activity, intent: Intent) {
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.startActivity(intent)
        applyTransition(activity, R.anim.slide_in_right, R.anim.slide_out_left)
    }

    fun navigateTab(activity: Activity, intent: Intent) {
        activity.startActivity(intent)
        applyTransition(activity, android.R.anim.fade_in, android.R.anim.fade_out)
    }

    @Suppress("DEPRECATION")
    private fun applyTransition(activity: Activity, enter: Int, exit: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            activity.overrideActivityTransition(Activity.OVERRIDE_TRANSITION_OPEN, enter, exit)
        } else {
            activity.overridePendingTransition(enter, exit)
        }
    }
}
