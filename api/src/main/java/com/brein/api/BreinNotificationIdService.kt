package com.brein.api

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService

class BreinNotificationIdService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        sendRegistrationToServer(token)
    }

    /**
     * Persist token to Breinify servers.
     *
     *
     * @param token The new token.
     */
    private fun sendRegistrationToServer(token: String) {
        Log.d(TAG, "Breinify - refreshed token is: $token")
        Breinify.configureDeviceToken(token)
    }

    companion object {
        private const val TAG = "BreinNotiIdService"
    }
}