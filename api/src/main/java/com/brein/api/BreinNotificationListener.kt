package com.brein.api

import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.brein.domain.BreinActivityType
import com.brein.domain.BreinNotificationAction
import com.brein.domain.BreinifyNotificationConstant
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import org.json.JSONTokener
import java.lang.reflect.Type


class BreinNotificationListener : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        try {
            val notificationId =
                intent?.getIntExtra(BreinifyNotificationConstant.BREIN_NOTIFICATION_ID, 0)
            val breinPayload = intent?.getStringExtra(BreinifyNotificationConstant.BREIN_PAYLOAD)
            val campaign = extractCampaign(breinPayload)

            sendOpenedPushNotification(notificationId, campaign)

            handleActionPayload(breinPayload, context)

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

    private fun handleActionPayload(breinPayload: String?, context: Context?) {

        var invokeApp = false

        val action = try {
            val jsonObject = JSONTokener(breinPayload).nextValue() as JSONObject
            val action = jsonObject.getString("action")

            if (action.isNotEmpty()) {
                val actionJson = JSONTokener(action).nextValue() as JSONObject

                // handle sendActivity
                handleSendActivityFromPushNotification(actionJson, context)

                // handle openUrl
                val couldInvoke = handleOpenUrlFromPushNotification(actionJson, context)
                invokeApp = !couldInvoke
            } else {
                Log.d(TAG, "Breinify - empty action element detected")
                invokeApp = true
            }

        } catch (e: Exception) {
            invokeApp = true
            Log.d(TAG, "no action payload provided - info: $e")
        }

        if (invokeApp) {
            Log.d(TAG, "Breinify launching the app")
            val application = BreinifyManager.getApplication()
            val packageManager = application?.packageManager
            val packageInfo = packageManager?.getPackageInfo(application.packageName, 0)
            val launchIntentForPackage =
                packageManager?.getLaunchIntentForPackage(application.packageName)
            context?.startActivity(launchIntentForPackage)
        }

    }

    private fun handleOpenUrlFromPushNotificationSAVE(jsonObject: JSONObject, context: Context?) {

        val storeId = "com.babbel.mobile.android.en"

        try {
            Log.d(TAG, "starting market playstore with storeId = $storeId")
            val playStoreIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$storeId"))
            playStoreIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context?.startActivity(playStoreIntent)
        } catch (e: ActivityNotFoundException) {
            Log.d(TAG, "starting https playstore with storeId = $storeId")

            val playStoreIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?$storeId")
            )
            playStoreIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context?.startActivity(playStoreIntent)
        }

    }

    private fun handleOpenUrlFromPushNotification(
        jsonObject: JSONObject,
        context: Context?
    ): Boolean {
        try {
            val openUrlCore = jsonObject.getString("openUrl")
            if (openUrlCore.isNotEmpty()) {
                val hashMapType: Type = object : TypeToken<HashMap<String, Any>>() {}.type
                val map: HashMap<String, Any> = Gson().fromJson(openUrlCore, hashMapType)
                val openUrlElement = map.get("url") as String
                val openUrl = openUrlElement.replace("\\", "")

                try {
                    Log.d(TAG, "starting with url = $openUrl")
                    val playStoreIntent = Intent(Intent.ACTION_VIEW, Uri.parse(openUrl))
                    playStoreIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

                    val application = BreinifyManager.getApplication()
                    application?.startActivity(playStoreIntent)

                    return true

                } catch (e: ActivityNotFoundException) {
                    Log.d(
                        TAG,
                        "Breinify - could not invoke activity url = $openUrl - exception is $e"
                    )

                }
            } else {
                Log.d(TAG, "Breinify - empty openUrl value")

            }
        } catch (e: Exception) {
            Log.d(
                TAG,
                "Breinify response in method handleOpenUrlFromPushNotification is: $e"
            )
        }

        return false
    }

    private fun handleSendActivityFromPushNotification(jsonObject: JSONObject, context: Context?) {

        try {
            val sendActivity: String = jsonObject.getString("sendActivity")
            if (sendActivity.isNotEmpty()) {

                val hashMapType: Type = object : TypeToken<HashMap<String, Any>>() {}.type
                val map: HashMap<String, Any> = Gson().fromJson(sendActivity, hashMapType)

                val activityType = map.get("activity")
                val tagsDic = map.get("tags")

                if (activityType != null) {
                    val clonedActivity = Breinify.getBreinActivity().clone()
                    clonedActivity.let {
                        clonedActivity?.setActivityType(activityType as String)
                        if (tagsDic != null) {
                            val toJson = Gson().toJson(tagsDic)
                            val innerMap: HashMap<String, Any> =
                                Gson().fromJson(toJson, hashMapType)
                            clonedActivity?.setTagsDic(innerMap)
                        }
                        Breinify.sendActivity(clonedActivity)
                    }
                }
            }
        } catch (e: Exception) {
            Log.d(
                TAG,
                "Breinify exception in method handleSendActivityFromPushNotification - exception is: $e"
            )
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
