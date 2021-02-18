package com.brein.api

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

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
            val generalChannel = createChannel(notificationChannelInfo)
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(generalChannel)
            notificationChannels.add(generalChannel)
        }
    }

    fun init(
        context: Context,
        remoteMessage: RemoteMessage
    ): BreinNotificationChannelInfo {
        val notificationChannelInfo = createNotificationChannelInfo(remoteMessage)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val generalChannel = createChannel(notificationChannelInfo)
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(generalChannel)
            notificationChannels.add(generalChannel)
        }

        return notificationChannelInfo
    }


    fun createNotificationChannelInfo(remoteMessage: RemoteMessage): BreinNotificationChannelInfo {

        val gson = GsonBuilder().setPrettyPrinting().create()
        val dataMap: Map<String, Any> =
            gson.fromJson(
                remoteMessage.data["breinify"],
                object : TypeToken<Map<String, Any>>() {}.type
            )

        var channelId = ""
        var channel = ""
        var channelDescription = ""
        var importance  = 1
        var notificationId  = 1
        var extraText  = ""
        var product  = ""
        var viewsMap: HashMap<String, Any> = HashMap()

        dataMap["channelId"]?.let { channelId = dataMap["channelId"].toString() }
        dataMap["channel"]?.let { channel = dataMap["channel"].toString() }
        dataMap["channelDescription"]?.let {
            channelDescription = dataMap["channelDescription"].toString()
        }
        dataMap["importance"]?.let { importance = dataMap["importance"].toString().toInt() }
        dataMap["notificationId"]?.let {
            notificationId = dataMap["notificationId"].toString().toInt()
        }
        dataMap["extraText"]?.let { extraText = dataMap["extraText"].toString() }
        dataMap["product"]?.let { product = dataMap["product"].toString() }

        dataMap["view"]?.let { viewsMap = dataMap["view"] as HashMap<String, Any> }

        val breinNotfictionChannelInfo = BreinNotificationChannelInfo(
            channelId,
            channel,
            channelDescription,
            importance,
            notificationId,
            extraText,
            product,
            viewsMap
        )

        return breinNotfictionChannelInfo
    }

    data class BreinNotificationChannelInfo(
        val id: String,  // channelId
        val name: String,  // channel
        val description: String, // channelDescription
        val priority: Int, // notification priority
        val notificationId: Int,
        val extraText: String?,
        val product: String?,
        val view: Map<String, Any>?
    )

    companion object {
        private const val TAG = "BreinNotificationChannelFactory"
    }
}