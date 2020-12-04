package com.brein.domain.results


import com.brein.domain.BreinResult
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class BreinTemporalDataResult {

    fun getLocation(): Any? {
        return map[LOCATION_KEY]
    }

    private val WEATHER_KEY = "weather"
    private val TIME_KEY = "time"
    private val TIMEZONE_KEY = "timezone"
    private val LOCAL_TIME_KEY = "localFormatIso8601"
    private val EPOCH_TIME_KEY = "epochFormatIso8601"
    private val LOCATION_KEY = "location"
    private val HOLIDAY_LIST_KEY = "holidays"
    private val EVENT_LIST_KEY = "events"

    val map: MutableMap<String, Any?>

    constructor(jsonResponse: String?) {
        map = Gson().fromJson(jsonResponse, object : TypeToken<Map<String, Any?>>() {}.type)
    }

    constructor(json: MutableMap<String, Any?>) {
        map = json
    }

    constructor(breinResult: BreinResult) {
        map = breinResult.map
    }

}

