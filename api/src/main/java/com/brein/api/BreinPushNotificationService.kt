package com.brein.api

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.webkit.URLUtil
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.brein.domain.*
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL


object BreinPushNotificationService {

    @RequiresApi(Build.VERSION_CODES.O)
    fun onMessageReceived(context: Context, remoteMessage: RemoteMessage) {

        val notificationData = BreinNotificationChannelFactory().init(context, remoteMessage)
        handleOnMessage(context, remoteMessage, notificationData)
    }


    fun onMessageReceivedLegacy(context: Context, remoteMessage: RemoteMessage) {
        val notificationData =
            BreinNotificationChannelFactory().createNotificationChannelInfo(remoteMessage)

        handleOnMessage(context, remoteMessage, notificationData)
    }

    private fun handleOnMessage(
        context: Context, remoteMessage: RemoteMessage,
        notificationData: BreinNotificationChannelFactory.BreinNotificationChannelInfo
    ) {
        lateinit var notification: BreinNotificationModel
        if (remoteMessage.data.isNotEmpty()) {
            notification =
                buildNotification(context, remoteMessage, notificationData)
        }

        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${remoteMessage.notification.toString()}")
        }

        showNotification(context, notificationData, notification)
    }

    private fun showNotification(
        context: Context,
        channelInfo: BreinNotificationChannelFactory.BreinNotificationChannelInfo,
        notification: BreinNotificationModel
    ) {

        val id = channelInfo.notificationId

        NotificationManagerCompat.from(context)
            .notify(id, createNotification(context, notification))
    }

    /**
     * Function to build a notification based on the payload
     */
    private fun buildNotification(
        context: Context,
        remoteMessage: RemoteMessage,
        notificationData: BreinNotificationChannelFactory.BreinNotificationChannelInfo
    ): BreinNotificationModel {

        try {
            val title = remoteMessage.data["title"]!!
            val body = remoteMessage.data["body"]!!
            val extraText: String? = notificationData.extraText
            val notificationId = notificationData.notificationId
            val notificationIcon = notificationData.notificationIcon

            val largeContent = remoteMessage.data["largeContent"]

            // notificationData contains `view`
            if (notificationData.view.isNullOrEmpty()) {
                return BasicNotification(
                    notificationData.id,
                    notificationId,
                    title,
                    body,
                    notificationIcon,
                    notificationData.priority,
                    largeContent
                )
            } else {
                // Getting and changing the type of view payload to Map
                val gson = GsonBuilder().setPrettyPrinting().create()

                // url for further use
                val imageUrl = notificationData.view["imageUrl"] as String?

                // Getting the actions payload if exists
                val actionsJson = gson.toJson(notificationData.view["actions"])

                if (!actionsJson.isNullOrEmpty()) {
                    // Creating a MutableList that'll contain the different actions
                    val actions: MutableList<NotificationAction> = mutableListOf()
                    // from json -> MutableList
                    val actionList: MutableList<Any> =
                        gson.fromJson(actionsJson, object : TypeToken<MutableList<Any>>() {}.type)

                    // each action needs a different intent
                    actionList.forEach { action ->
                        val currentAction: Map<String, String> =
                            gson.fromJson(
                                gson.toJson(action),
                                object : TypeToken<Map<String, String>>() {}.type
                            )

                        val pendingIntent: PendingIntent
                        val deepLink = currentAction["deeplink"]

                        // different result for each action
                        when (currentAction["action"]) {
                            "open" -> {
                                // when action: open -> deep link to the app
                                val openIntent =
                                    Intent(context, BreinNotificationListener::class.java)
                                openIntent.action = BreinNotificationAction.OPENED_FIRST
                                openIntent.data = Uri.parse(deepLink)
                                openIntent.putExtra("notificationId", notificationId)

                                pendingIntent = PendingIntent.getBroadcast(
                                    context,
                                    0,
                                    openIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                                )
                            }
                            "open_second" -> {
                                // when action: open -> deep link to the app
                                val openSecondIntent =
                                    Intent(context, BreinNotificationListener::class.java)
                                openSecondIntent.action = BreinNotificationAction.OPENED_SECOND
                                openSecondIntent.data = Uri.parse(deepLink)
                                openSecondIntent.putExtra("notificationId", notificationId)
//                              intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

                                pendingIntent = PendingIntent.getBroadcast(
                                    context,
                                    0,
                                    openSecondIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                                )
                            }
                            else -> {
                                val intent = Intent(context, BreinNotificationListener::class.java)
                                intent.action = BreinNotificationAction.IGNORE
                                intent.putExtra("notificationId", notificationId)

                                pendingIntent = PendingIntent.getBroadcast(
                                    context,
                                    0 /*Request code*/,
                                    intent,
                                    PendingIntent.FLAG_CANCEL_CURRENT
                                )
                            }
                        }

                        // Creating a Notification Model
                        val notificationModel = NotificationAction(
                            notificationData.notificationId,
                            currentAction["content"].toString(),
                            pendingIntent
                        )
                        actions.add(notificationModel)
                    }

                    // creating an Action Expandable notification
                    return PictureActionExpandableNotification(
                        notificationData.id,
                        notificationId,
                        title,
                        body,
                        notificationIcon,
                        notificationData.priority,
                        imageUrl,
                        largeContent,
                        actions
                    )
                } else {
                    // actions is an empty array
                    // -> notification will be sent without actions
                    return PictureExpandableNotification(
                        notificationData.id,
                        notificationId,
                        title,
                        body,
                        notificationIcon,
                        notificationData.priority,
                        imageUrl,
                        largeContent
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception is: $e")

            return BasicNotification(
                notificationData.id,
                0,
                "",
                "",
                "",
                notificationData.priority,
                ""
            )
        }

    }

    // @SuppressLint("UseCompatLoadingForDrawables")
    private fun createNotification(context: Context, model: BreinNotificationModel): Notification {
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // get the id of custom notification icon for further use
        // when not found (name not specified in the payload / or false name)
        // a fallback icon would be used

        val resourceId: Int =
            if (checkNotificationIconExists(model.notificationIcon.toString(), context)) {
                context.resources.getIdentifier(
                    model.notificationIcon.toString(),
                    "drawable",
                    context.packageName
                )
            } else {
                context.resources.getIdentifier(
                    "icon_notification_fallback_white",
                    "drawable",
                    context.packageName
                )
            }

        val intent = Intent(context, BreinifyManager.getMainActivity()?.javaClass).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        return NotificationCompat.Builder(context, model.channelId)
            .setSmallIcon(resourceId)
            .setSmallIcon(context.applicationInfo.icon)
            .setContentTitle(model.title)
            .setContentText(model.content)
            .setPriority(model.priority)
            .setSound(defaultSoundUri)
            .setTicker(model.content)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .apply {
                if (model is PictureExpandableNotification) {
                    if (model.picture != null) {
                        applyImageUrl(this, model.picture, model.largeContent)
                    }
                    if (model.largeContent != null && !pictureApplied) {
                        this.setStyle(
                            NotificationCompat.BigTextStyle().bigText(model.largeContent)
                        )
                    }
                }
                else if (model is PictureActionExpandableNotification) {
                    if (model.picture != null) {
                        applyImageUrl(this, model.picture, model.largeContent)
                    }
                    if (model.largeContent != null && !pictureApplied) {
                        this.setStyle(
                            NotificationCompat.BigTextStyle().bigText(model.largeContent)
                        )
                    }

                }
                model.actions.forEach { (iconId, title, actionIntent) ->
                    addAction(iconId, title, actionIntent)
                }
            }
            .build()
    }

    private fun checkNotificationIconExists(
        notificationIcon: String,
        context: Context
    ): Boolean {
        if (context.resources.getIdentifier(
                notificationIcon,
                "drawable",
                context.packageName
            ) == 0
        ) {
            return false
        }
        return true
    }

    private fun applyImageUrl(
        builder: NotificationCompat.Builder,
        imageUrl: String?,
        message: String?
    ) = runBlocking {
        val isValidUrl = URLUtil.isValidUrl(imageUrl)
        if (isValidUrl) {
            val url = URL(imageUrl)
            withContext(Dispatchers.IO) {
                try {
                    val input = url.openStream()
                    BitmapFactory.decodeStream(input)
                } catch (e: IOException) {
                    null
                }
            }?.let { bitmap ->
                // setting the style to a big picture
                pictureApplied = false
                builder.setLargeIcon(bitmap)
                /*
                if (message != null) {
                    builder.setStyle(
                        NotificationCompat.BigPictureStyle().bigPicture(bitmap)
                            .setSummaryText(message)
                    )
                } else {
                    builder.setStyle(
                        NotificationCompat.BigPictureStyle().bigPicture(bitmap)
                    )
                }

                 */
            }
        }
    }

    private const val TAG = "BreinPushNotification"

    var pictureApplied = false

}