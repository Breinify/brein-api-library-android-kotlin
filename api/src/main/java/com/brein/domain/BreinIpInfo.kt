package com.brein.domain

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.net.URL

/**
 * request: http://www.ip-api.com/json
 *
 *
 * response:
 * {
 * as: "AS3320 Deutsche Telekom AG",
 * city: "Roetgen",
 * country: "Germany",
 * countryCode: "DE",
 * isp: "Deutsche Telekom AG",
 * lat: 50.65,
 * lon: 6.2,
 * org: "Deutsche Telekom AG",
 * query: "217.247.43.188",
 * region: "NW",
 * regionName: "North Rhine-Westphalia",
 * status: "success",
 * timezone: "Europe/Berlin",
 * zip: "52159"
 * }
 */
object BreinIpInfo {

    var infoMap = mutableMapOf<String, Any?>()

    val externalIp: String?
        get() = this.infoMap[IP_FIELD] as String?

    val timezone: String?
        get() = this.infoMap[TIMEZONE_FIELD] as String?

    init {
    }

    private fun refreshData() = Thread {

        try {
            val ipJson = invokeRequest()
            if (ipJson != null) {
                this.infoMap = Gson().fromJson(ipJson, object : TypeToken<Map<String, Any?>>() {}.type)
            }
        } catch (e: Exception) {
            Log.e("BreinIpInfo", "Breinify - exception occurred calling refreshData", e)
        }
    }.start()

    private fun invokeRequest(): String? {
        try {
            return URL("http://www.ip-api.com/json").readText()
        } catch (e: Exception) {
            Log.e("BreinIpInfo", "Breinify - exception occurred calling invokeRequest", e)
        }
        return null
    }

    const val IP_FIELD = "query"
    const val TIMEZONE_FIELD = "timezone"

}