package com.brein.api

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class BreinNotficationService : FirebaseMessagingService() {

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)

        // todo

    }

    /**
     * Invoked in case of ...
     * @param remoteMessage  RemoteMessage
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        // super.onMessageReceived(remoteMessage);
        val dataMap: MutableMap<String, String> = remoteMessage.getData()
        for ((key, value) in dataMap) {
            Log.d(TAG, "Key : $key Value : $value")
        }
        sendNotification(remoteMessage)
    }

    override fun onDeletedMessages() {
        Log.d(TAG, "onDeleteMessage invoked")
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param remoteMessage RemoteMessage FCM message
     */
    private fun sendNotification(remoteMessage: RemoteMessage?) {

        if (remoteMessage == null) {
            Log.d(TAG, "remote notification: message not set!  -> no notification shown.")
            return
        }

        val title: String = remoteMessage.data["title"].toString()
        val message: String = remoteMessage.data["message"].toString()

        val intent = Intent(this, BreinifyManager.getMainActivity()?.javaClass)

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        // todo check notification
        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notification = NotificationCompat.Builder(this, "BreinifyChannel")
            .setSmallIcon(android.R.color.transparent)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager: NotificationManager? =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        if (notificationManager != null) {
            notificationManager.notify(0 , notification)
        }
    }

    companion object {
        private const val TAG = "BreinNotficationService"
    }
}