package com.brein.domain

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Contains the result of an Brein Request when invoking a
 * request
 */
class BreinResult {

    /**
     * provides the map containing the results
     *
     * @return Map of String, Object
     */
    var map = mutableMapOf<String, Any?>()

    /**
     * creates a brein result object
     * @param jsonResponse as json string
     */
    constructor(jsonResponse: String?) {
        try {
            this.map =
                Gson().fromJson(jsonResponse, object : TypeToken<Map<String, Any?>>() {}.type)
        } catch (e: Exception) {
            Log.e(TAG, "Exception is: $e")
        }
    }

    constructor(json: MutableMap<String, Any?>) {
        this.map = json
    }

    /**
     * retrieves the object according to the requested key
     * @param key   String contains the key
     * @param <T>   Object the value
     * @return      Object retrieved
    </T> */
    operator fun <T> get(key: String): Any? {
        return this.map[key]
    }

    /**
     * checks if key exists in map
     * @param key to check
     * @return true or false
     */
    fun has(key: String): Boolean {
        return get<Any?>(key) != null
    }

    fun <T> getValue(key: String): Any? {
        return this.map[key]
    }

    companion object {
        private const val TAG = "BreinResult"
    }
}