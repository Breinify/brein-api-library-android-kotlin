package com.brein.api

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class BreinNotificationService : FirebaseMessagingService() {

    /**
     * Invoked in case of ...
     * @param remoteMessage  RemoteMessage
     */
    @SuppressLint("LongLogTag")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        // super.onMessageReceived(remoteMessage);
        val dataMap: MutableMap<String, String> = remoteMessage.getData()
        for ((key, value) in dataMap) {
            Log.d(TAG, "Key : $key Value : $value")
        }

        handleNow(remoteMessage)
        sendNotification(remoteMessage)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Breinify.configureDeviceToken(token)
    }


    @SuppressLint("LongLogTag")
    override fun onDeletedMessages() {
        Log.d(TAG, "Breinify - onDeleteMessage invoked")
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param remoteMessage RemoteMessage FCM message
     */
    @SuppressLint("LongLogTag")
    private fun sendNotification(remoteMessage: RemoteMessage?) {

        if (remoteMessage == null) {
            Log.d(
                TAG,
                "Breinify - remote notification: message not set!  -> no notification shown."
            )
            return
        }

        val title: String = remoteMessage.data["title"].toString()
        val message: String = remoteMessage.data["message"].toString()

        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

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

        notificationManager?.notify(0, notification)
    }

    private fun handleNow(remoteMessage: RemoteMessage) {
        val handler = Handler(Looper.getMainLooper())

        handler.post {
            Toast.makeText(baseContext, "Breinify", Toast.LENGTH_LONG).show()

            remoteMessage.notification?.let {
                val intent = Intent("MyData")
                intent.putExtra("message", remoteMessage.data["text"])
            }
        }
    }

    companion object {
        private const val TAG = "BreinNotificationService"
    }
}