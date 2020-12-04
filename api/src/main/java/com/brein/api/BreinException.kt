package com.brein.api

import android.util.Log


/**
 * BreinException
 */
class BreinException : RuntimeException {
    /*
     * Exception methods...
     */
    constructor(e: Throwable) : super(e) {
        Log.d(TAG, EXCEPTION_IS + e.message)
    }

    constructor(msg: String) : super(msg) {
        Log.d(TAG, EXCEPTION_IS + msg)
    }

    constructor(msg: String, cause: Exception) : super(msg, cause) {
        Log.d(TAG, EXCEPTION_IS + msg + " with cause: " + cause.message)
    }

    companion object {
        private const val TAG = "BreinException"

        // Error Messages
        const val API_KEY_NOT_SET = "No apiKey set"
        const val URL_IS_NULL = "URL in request contains null"
        const val REQUEST_FAILED = "Failed request!"
        const val VALIDATE_ACTIVITY_OR_CONFIG_FAILED = "either activity or config not valid"
        const val BREIN_BASE_VALIDATION_FAILED = "activity or lookup object is null"
        const val CONFIG_VALIDATION_FAILED = "configuration object is null"
        const val REQUEST_BODY_FAILED = "request body is null or wrong"
        const val LOOKUP_EXCEPTION = "lookup exception has occurred"
        const val ENGINE_NOT_INITIALIZED =
            "Rest engine not initialized. You have to configure BreinConfig with a valid engine."
        const val USER_NOT_SET = "User not set."
        const val ACTIVITY_TYPE_NOT_SET = "ActivityType not set."
        const val CATEGORY_TYPE_NOT_SET = "CategoryType not set."
        private const val EXCEPTION_IS = "Exception is:"
    }
}