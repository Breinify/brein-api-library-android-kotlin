package com.brein.api

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.brein.domain.BreinActivityType
import com.brein.domain.BreinNotificationAction

class BreinNotificationListener : BroadcastReceiver() {


    override fun onReceive(context: Context?, intent: Intent?) {

        try {
            // get notificationId from intent extra
            val notificationId = intent?.getIntExtra("notificationId", 0)

            // depends on which action was sent from notification service
            when (intent?.action.toString()) {
                BreinNotificationAction.OPENED_FIRST -> {
                    Log.d(TAG, "openedPushNotification (OPENED_FIRST) is:" + intent.toString())

                    val breinActivity = Breinify.getBreinActivity()
                        .setActivityType(BreinActivityType.OPENED_PUSH_NOTIFICATION)
                    Breinify.sendActivity(breinActivity)

                    val mainActivity = BreinifyManager.getMainActivity()
                    val clazz = mainActivity?.javaClass
                    // for closing the notification drawer
                    val closeIntent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
                    context?.sendBroadcast(closeIntent)
                    val mainIntent = Intent(context, clazz)
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    context?.startActivity(mainIntent)
                }

                BreinNotificationAction.OPENED_SECOND -> {
                    Log.d(TAG, "openedPushNotification (OPENED_SECOND)is:" + intent.toString())

                    val breinActivity = Breinify.getBreinActivity()
                        .setActivityType(BreinActivityType.OPENED_PUSH_NOTIFICATION)

                    Breinify.sendActivity(breinActivity)
                }

                BreinNotificationAction.IGNORE -> {
                    val manager =
                        context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    manager.cancel(notificationId!!)
                    // for closing the notification drawer
                    val closeIntent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
                    context.sendBroadcast(closeIntent)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "could not handle onReceive due to exception $e")
        }
    }

    companion object {
        private const val TAG = "BreinNotiListener"
    }

}
