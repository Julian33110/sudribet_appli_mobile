package com.example.sudribet

import android.app.Activity
import android.content.Intent

object ActivityTransitions {

    /** Naviguer vers une nouvelle activité avec slide vers la gauche */
    fun navigate(activity: Activity, intent: Intent) {
        activity.startActivity(intent)
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    /** Revenir en arrière avec slide vers la droite */
    fun navigateBack(activity: Activity) {
        activity.finish()
        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    /** Naviguer en remplaçant le back stack (ex: Login → Home) */
    fun navigateAndClear(activity: Activity, intent: Intent) {
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.startActivity(intent)
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    /** Navigation bottom bar (fondu léger, sans slide) */
    fun navigateTab(activity: Activity, intent: Intent) {
        activity.startActivity(intent)
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}
