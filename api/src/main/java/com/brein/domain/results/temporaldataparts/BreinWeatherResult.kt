package com.brein.domain.results.temporaldataparts

import com.brein.util.JsonHelper
import java.util.Locale

class BreinWeatherResult(private val result: MutableMap<String, Any?>?) {

    private val description: String
    private val temperatureCelsius: Double
    private var precipitation: PrecipitationType? = null
    private var precipitationAmount: Double? = null
    private val windStrength: Double
    private val lastMeasured: Long
    private val cloudCover: Double

    private var lat: Double? = null
    private var lon: Double? = null

    val temperatureFahrenheit: Double?
        get() {
            val celsius = this.temperatureCelsius
            return celsius * 9 / 5 + 32
        }
    val temperatureKelvin: Double?
        get() {
            val celsius = this.temperatureCelsius
            return celsius + 273.15
        }
    val measuredAt: GeoCoordinates?
        get() = if (this.lat == null && this.lon == null) {
            null
        } else {
            GeoCoordinates(this.lat!!, this.lon!!)
        }

    override fun toString(): String {
        return "weather of " + this.description + " and a current temperature of " + this.temperatureCelsius + " with" +
                " precipitation of " + this.precipitation
    }

    companion object {
        private const val DESCRIPTION_KEY = "description"
        private const val TEMPERATURE_KEY = "temperature"
        private const val PRECIPITATION_KEY = "precipitation"
        private const val PRECIPITATION_TYPE_KEY = PRECIPITATION_KEY + "Type"
        private const val PRECIPITATION_AMOUNT_KEY = PRECIPITATION_KEY + "Amount"
        private const val WIND_STRENGTH_KEY = "windStrength"
        private const val LAST_MEASURED_KEY = "lastMeasured"
        private const val CLOUD_COVER_KEY = "cloudCover"
        private const val MEASURED_LOCATION_KEY = "measuredAt"
        private const val LATITUDE_KEY = "lat"
        private const val LONGITUDE_KEY = "lon"
    }

    init {
        this.description = JsonHelper.getOrString(result, DESCRIPTION_KEY)!!
        this.temperatureCelsius = JsonHelper.getOrDouble(result, TEMPERATURE_KEY)!!
        this.windStrength = JsonHelper.getOrDouble(result, WIND_STRENGTH_KEY)!!
        this.lastMeasured = JsonHelper.getOrLong(result, LAST_MEASURED_KEY)!!
        this.cloudCover = JsonHelper.getOrDouble(result, CLOUD_COVER_KEY)!!

        val measuredJson: MutableMap<String, Any?>? = JsonHelper.getOrMap(result, MEASURED_LOCATION_KEY)
        if (measuredJson == null) {
            this.lat = null
            this.lon = null
        } else {
            this.lat = JsonHelper.getOrDouble(measuredJson, LATITUDE_KEY)
            this.lon = JsonHelper.getOrDouble(measuredJson, LONGITUDE_KEY)
        }

        val preciValue: MutableMap<String, Any?>? = JsonHelper.getOrMap(result, PRECIPITATION_KEY)
        if (preciValue == null) {
            this.precipitation = PrecipitationType.UNKNOWN
            this.precipitationAmount = null
        } else {
            val type: MutableMap<String, Any?>? = JsonHelper.getOrMap(preciValue, PRECIPITATION_TYPE_KEY)
            if (type == null) {
                this.precipitation = PrecipitationType.UNKNOWN
            } else {
                when (type.toString().toLowerCase(Locale.getDefault())) {
                    "rain" -> this.precipitation = PrecipitationType.RAIN
                    "snow" -> this.precipitation = PrecipitationType.SNOW
                    "none" -> this.precipitation = PrecipitationType.NONE
                    else -> this.precipitation = PrecipitationType.UNKNOWN
                }
            }
            this.precipitationAmount = JsonHelper.getOrDouble(preciValue, PRECIPITATION_AMOUNT_KEY)
        }
    }
}