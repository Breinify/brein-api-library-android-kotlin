package com.brein.api

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.brein.domain.BreinActivityType
import com.brein.domain.BreinNotificationAction
import com.brein.domain.BreinifyNotificationConstant
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

class BreinNotificationListener : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        try {
            val notificationId = intent?.getIntExtra(BreinifyNotificationConstant.BREIN_NOTIFICATION_ID, 0)
            val breinPayload = intent?.getStringExtra(BreinifyNotificationConstant.BREIN_PAYLOAD)
            val campaign = extractCampaign(breinPayload)

            sendOpenedPushNotification(notificationId, campaign)

            // depends on which action was sent from notification service
            when (intent?.action.toString()) {
                BreinNotificationAction.OPENED_FIRST -> {
                    Log.d(TAG, "openedPushNotification (OPENED_FIRST) is:" + intent.toString())

                    // since it can be null
                    val breinActivity = campaign?.let {
                        Breinify.getBreinActivity()
                            .setActivityType(BreinActivityType.OPENED_PUSH_NOTIFICATION)
                            .setTagsDic(it) // use the campaign hashmap as tags
                    }
                    // send activity
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

                    val breinActivity = campaign?.let {
                        Breinify.getBreinActivity()
                            .setActivityType(BreinActivityType.OPENED_PUSH_NOTIFICATION)
                            .setTagsDic(it)
                    }
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
            Log.e(TAG, "Breinify - could not handle onReceive due to exception $e")
        }
    }

    private fun sendOpenedPushNotification(notificationId: Int?, campaign: HashMap<String, Any>?) {
        // send openedPushNotification
        val clonedActivity = Breinify.getBreinActivity().clone()
        clonedActivity.let {
            clonedActivity?.setActivityType(BreinActivityType.OPENED_PUSH_NOTIFICATION)
            if (campaign != null) {
                clonedActivity?.setTagsDic(campaign)
            }
            Breinify.sendActivity(clonedActivity)
        }
    }

    private fun extractCampaign(breinPayload: String?): HashMap<String, Any>? {
        val type = object : TypeToken<HashMap<String, Any>>() {}.type
        val gson = GsonBuilder().setPrettyPrinting().create()

        if (breinPayload != null) {
            // from json -> Hashmap
            val breinifyMap: Map<String, Any> =
                gson.fromJson(
                    breinPayload,
                    type
                )

            return gson.fromJson(
                gson.toJson(breinifyMap[BreinifyNotificationConstant.BREIN_CAMPAIGN_SEGMENT]),
                type
            )
        }

        return null
    }

    companion object {
        private const val TAG = "BreinNotiListener"
    }

}
