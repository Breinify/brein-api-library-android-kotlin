package com.brein.api

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 *
 * A declaration of GcmReceiver, which handles messages sent from GCM to your application.
 * Because this service needs permission to receive messages from GCM,
 * add com.google.android.c2dm.permission.SEND to the receiver.
 *
 */
class BreinPushNotificationReceiver : BroadcastReceiver()   {

    override fun onReceive(context: Context?, intent: Intent) {
        Log.d(TAG, "onReceive invoked")
        if (intent.getExtras() != null) {
            for (key in intent.getExtras()!!.keySet()) {
                val value: Any? = intent.getExtras()!!.get(key)
                Log.e(TAG, "Key: $key Value: $value")
            }
        }
    }

    companion object {
        private const val TAG = "BreinPushNotReceiver"
    }
}