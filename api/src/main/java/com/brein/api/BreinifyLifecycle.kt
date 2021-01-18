package com.brein.api

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log

class BreinifyLifecycle : Application.ActivityLifecycleCallbacks {
    private var numStarted = 0
    private var userEnteredTime: Long = 0

    companion object {
        private const val TAG = "BreinifyLifecycle"
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        Log.d(TAG, "Breinfy - onActivityCreated invoked")
    }

    override fun onActivityStarted(activity: Activity) {
        Log.d(TAG, "Breinify - onActivityStarted invoked")
        if (numStarted == 0) {
            userEnteredTime = System.currentTimeMillis()

            // app is now in foreground
            BreinifyManager.appIsInForeground()
        }
        numStarted++
    }

    override fun onActivityResumed(activity: Activity) {
        Log.d(TAG, "Breinify - onActivityResumed invoked")
    }

    override fun onActivityPaused(activity: Activity) {
        Log.d(TAG, "Breinify - onActivityPaused invoked")
    }

    override fun onActivityStopped(activity: Activity) {
        Log.d(TAG, "Breinify - onActivityStopped invoked")
        numStarted--
        if (numStarted == 0) {
            val timeInApp = System.currentTimeMillis() - userEnteredTime

            // app is now in background
            Log.d(TAG, "Breinfiy - app is now in background. Time in App was: $timeInApp")
            BreinifyManager.appIsInBackground()
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        Log.d(TAG, "Breinify - onActivitySaveInstanceState invoked")
    }

    override fun onActivityDestroyed(activity: Activity) {
        Log.d(TAG, "Breinify - onActivityDestroyed invoked")
    }
}