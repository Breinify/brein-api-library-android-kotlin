package com.brein.api

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.brein.domain.*
import com.example.brein.R
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
        val title = remoteMessage.data["title"]!!
        val body = remoteMessage.data["body"]!!
        val extraText: String? = notificationData.extraText

        // notificationData contains `view`
        if (notificationData.view.isNullOrEmpty()) {
            return BasicNotification(
                notificationData.id,
                title,
                body,
                notificationData.priority
            )
        } else {
            // Getting and changing the type of view payload to Map
            val gson = GsonBuilder().setPrettyPrinting().create()

            // url for further use
            val imageUrl = notificationData.view["imageUrl"] as String?

            // Getting the actions payload if exists
            val actionsJson= gson.toJson(notificationData.view["actions"])

            var bigContentTitle = ""
            notificationData.view["bigContentTitle"]?.let {
                bigContentTitle = notificationData.view["bigContentTitle"].toString()
            }

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
                            val intent = Intent()
                            intent.action = Intent.ACTION_VIEW
                            intent.data = Uri.parse(deepLink)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            intent.putExtra("extra", extraText)
                            pendingIntent = PendingIntent.getActivity(
                                context,
                                0,
                                intent,
                                PendingIntent.FLAG_ONE_SHOT
                            )
                        }
                        "open_second" -> {
                            // when action: open -> deep link to the app
                            val intent = Intent()
                            intent.action = Intent.ACTION_VIEW
                            intent.data = Uri.parse(deepLink)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                            intent.putExtra("test", "second action clicked")
                            pendingIntent = PendingIntent.getActivity(
                                context,
                                0,
                                intent,
                                PendingIntent.FLAG_ONE_SHOT
                            )
                        }
                        else -> {
                            val intent = Intent()
                            intent.action = currentAction["action"]
                            intent.putExtra("id", notificationData.notificationId)
                            pendingIntent = PendingIntent.getActivity(
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
                    title,
                    body,
                    notificationData.priority,
                    bigContentTitle,
                    imageUrl,
                    actions
                )
            } else {
                // actions is an empty array
                // -> notification will be sent without actions
                return PictureExpandableNotification(
                    notificationData.id,
                    title,
                    body,
                    notificationData.priority,
                    bigContentTitle,
                    imageUrl
                )
            }
        }
    }

    private fun createNotification(context: Context, model: BreinNotificationModel): Notification {
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        return NotificationCompat.Builder(context, model.channelId)
            .setSmallIcon(R.drawable.icon_notification_fallback_white)
            .setContentTitle(model.title)
            .setContentText(model.content)
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setPriority(model.priority)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setOnlyAlertOnce(true)
            .apply {
                when (model) {
                    is PictureExpandableNotification -> {
                        applyImageUrl(this, model.picture)
//                        setStyle(
//                            NotificationCompat.BigPictureStyle()
//                                .bigPicture(myBitmap)
//                                .bigLargeIcon(null)
//                        )
                    }
                    is PictureActionExpandableNotification -> {
                        applyImageUrl(this, model.picture)
//                        setStyle(
//                            NotificationCompat.BigPictureStyle()
//                                .bigPicture(myBitmap)
//                                .bigLargeIcon(null)
//                        )
                    }
                }
                model.actions.forEach { (iconId, title, actionIntent) ->
                    addAction(iconId, title, actionIntent)
                }
            }
            .build()
    }


    // using coroutines to read a picture from URL
    // decode the picture to a `Bitmap`
    // and appending it to the notification
    fun applyImageUrl(
        builder: NotificationCompat.Builder,
        imageUrl: String?
    ) = runBlocking {
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
            builder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
        }
    }

    private const val TAG = "BreinPushNotification"

}