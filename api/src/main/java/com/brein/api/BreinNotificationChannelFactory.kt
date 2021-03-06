package com.brein.api

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.brein.domain.BreinifyNotificationConstant
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.util.Collections

import javax.inject.Inject

class BreinNotificationChannelFactory @Inject constructor() {
    private val notificationChannels: MutableList<NotificationChannel> = arrayListOf()

    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannel(breinNotificationChannelInfo: BreinNotificationChannelInfo): NotificationChannel =
        NotificationChannel(
            breinNotificationChannelInfo.id,
            breinNotificationChannelInfo.name,
            breinNotificationChannelInfo.priority
        ).apply { this.description = breinNotificationChannelInfo.description }

    fun init(
        context: Context,
        notificationChannelInfo: BreinNotificationChannelInfo
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = createChannel(notificationChannelInfo)
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (notificationManager.getNotificationChannel(notificationChannel.id) == null) {
                notificationChannel.enableVibration(true)
                notificationChannel.vibrationPattern = longArrayOf(100, 200, 300)

                if (notificationChannelInfo.lights) {
                    notificationChannel.enableLights(true)
                }

                notificationChannel.importance = notificationChannelInfo.priority

                if (notificationChannelInfo.lockScreenVisibility) {
                    notificationChannel.lockscreenVisibility =
                        Notification.VISIBILITY_PUBLIC
                }

                notificationManager.createNotificationChannel(notificationChannel)
                notificationChannels.add(notificationChannel)
            }
        }
    }

    fun init(
        context: Context,
        remoteMessage: RemoteMessage
    ): BreinNotificationChannelInfo {
        val notificationChannelInfo = createNotificationChannelInfo(remoteMessage)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val breinifyChannel = createChannel(notificationChannelInfo)

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (notificationManager.getNotificationChannel(breinifyChannel.id) == null) {
                notificationManager.createNotificationChannel(breinifyChannel)
                notificationChannels.add(breinifyChannel)
            }
        }

        return notificationChannelInfo
    }

    fun createNotificationChannelInfo(remoteMessage: RemoteMessage): BreinNotificationChannelInfo {

        try {
            val gson = GsonBuilder().setPrettyPrinting().create()
            val dataMap: Map<String, Any> =
                gson.fromJson(
                    remoteMessage.data[BreinifyNotificationConstant.BREIN_SEGEMENT],
                    object : TypeToken<Map<String, Any>>() {}.type
                )

            var channelId = "NotificationChannelId"
            var channel = "Notification Channel"
            var channelDescription = "Notifications by Breinify AI Engine"
            var importance = 4.0
            var notificationId = 1
            var notificationIcon = "icon_notification_fallback_white"
            var extraText = ""
            var product = ""
            val viewsMap = dataMap["view"] as Map<String, Any>?
            val lockScreen = true
            val vibration = true
            val lights = false

            dataMap["channelId"]?.let {
                val tempChannelId = dataMap["channelId"]
                channelId = tempChannelId as? String ?: "NotificationChannelId"
            }

            dataMap["channel"]?.let {
                val tempChannel = dataMap["channel"]
                channel = tempChannel as? String ?: "Notification Channel"
            }

            dataMap["channelDescription"]?.let {
                val tempChannelDesc = dataMap["channelDescription"]
                channelDescription = tempChannelDesc as? String ?: "Notification Channel"
            }

            dataMap["importance"]?.let {
                val priority = dataMap["importance"]
                importance = priority as? Double ?: 4.0
            }

            dataMap["notificationId"]?.let {
                val noti = dataMap["notificationId"]
                notificationId = noti as? Int ?: 1
            }

            dataMap["notificationIcon"]?.let {
                val notifIcon = dataMap["notificationIcon"]
                // check id exists
                notificationIcon = notifIcon as? String ?: "icon_notification_fallback_white"
            }

            dataMap["extraText"]?.let {
                val extra = dataMap["extraText"]
                extraText = extra as? String ?: ""
            }

            dataMap["product"]?.let {
                val pro = dataMap["product"]
                product = pro as? String ?: ""
            }

            return BreinNotificationChannelInfo(
                channelId,
                channel,
                channelDescription,
                importance.toInt(),
                notificationId,
                notificationIcon,
                extraText,
                product,
                viewsMap,
                lockScreen,
                vibration,
                lights
            )

        } catch (e: Exception) {
            Log.e(TAG, "Exception within createNotificationChannelInfo is: $e")

            return BreinNotificationChannelInfo(
                "",
                "",
                "",
                0,
                0,
                "",
                "",
                "",
                Collections.emptyMap(),
                false,
                false,
                false
            )
        }
    }

    data class BreinNotificationChannelInfo(
        val id: String,  // channelId
        val name: String,  // channel
        val description: String, // channelDescription
        val priority: Int, // notification priority
        val notificationId: Int,
        val notificationIcon: String?,
        val extraText: String?,
        val product: String?,
        val view: Map<String, Any>?,
        val lockScreenVisibility: Boolean,
        val vibration: Boolean,
        val lights: Boolean
    )

    companion object {
        private const val TAG = "BreinNotiChannelFactory"
    }
}