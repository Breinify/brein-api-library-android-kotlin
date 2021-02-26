package com.brein.api

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.brein.domain.BreinActivityType
import com.brein.domain.BreinCategoryType
import com.brein.domain.BreinNotificationAction

class NotificationListener : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        // get notificationId from intent extra
        val notificationId = intent?.getIntExtra("notificationId", 0)

        // depends on which action was sent from notification service
        when (intent?.action.toString()) {
            BreinNotificationAction.OPENED_FIRST -> {
                // todo add more code

                val breinActivity = Breinify.getBreinActivity()
                    .setCategory(BreinCategoryType.HOME)
                    .setActivityType(BreinActivityType.OPENED_PUSH_NOTIFICATION)

                Breinify.sendActivity(breinActivity)
            }
            BreinNotificationAction.OPENED_SECOND -> {
                // todo add more code

                val breinActivity = Breinify.getBreinActivity()
                    .setCategory(BreinCategoryType.HOME)
                    .setActivityType(BreinActivityType.OPENED_PUSH_NOTIFICATION)

                Breinify.sendActivity(breinActivity)
            }
            BreinNotificationAction.IGNORE -> {
                val manager =
                    context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.cancel(notificationId!!)
            }
        }
    }
}