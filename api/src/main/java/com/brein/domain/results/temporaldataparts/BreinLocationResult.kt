package com.brein.domain.results.temporaldataparts

import com.brein.util.BreinMapUtil

class BreinLocationResult(private val json: Map<String, Any?>?) {

    var country: String? = null
    var state: String? = null
    var city: String? = null
    var granularity: String? = null
    var geojsons: MutableMap<String, Map<String, Any?>>? = null
    var lat = 0.0
    var lon = 0.0

    fun getGeoJson(type: String): Map<String, Any?>? {
        return this.geojsons!![type]
    }

    companion object {
        private const val COUNTRY_KEY = "country"
        private const val STATE_KEY = "state"
        private const val CITY_KEY = "city"
        private const val GRANULARITY_KEY = "granularity"
        private const val GEOJSON_KEY = "geojson"
        private const val LAT_KEY = "lat"
        private const val LON_KEY = "lon"
    }

    init {
        if (json == null) {
            this.country = null
            this.state = null
            this.city = null
            this.granularity = null
            this.geojsons = HashMap()

            //you're on null island now
            this.lat = 0.0
            this.lon = 0.0
        } else {
            this.country = BreinMapUtil.getNestedValue(json, arrayOf(COUNTRY_KEY))
            this.state = BreinMapUtil.getNestedValue(json, arrayOf(STATE_KEY))
            this.city = BreinMapUtil.getNestedValue(json, arrayOf(CITY_KEY))
            this.granularity = BreinMapUtil.getNestedValue(json, arrayOf(GRANULARITY_KEY))
            this.geojsons = BreinMapUtil.getNestedValue(json, arrayOf(GEOJSON_KEY))
            this.lat = if (json.containsKey(LAT_KEY)) {
                json[LAT_KEY] as Double
            } else {
                0.0
            }
            this.lon = if (json.containsKey(LON_KEY)) {
                json[LON_KEY] as Double
            } else {
                0.0
            }
        }
    }
}