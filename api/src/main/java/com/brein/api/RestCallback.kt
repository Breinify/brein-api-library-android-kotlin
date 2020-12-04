package com.brein.api

import android.util.Log
import com.brein.domain.BreinResult

class RestCallback : ICallback<BreinResult?> {

    override fun callback(data: BreinResult?) {
        if (data != null) {
            Log.d(TAG, "callback data is: $data")
        }
    }

    companion object {
        private const val TAG = "RestCallback"
    }
}