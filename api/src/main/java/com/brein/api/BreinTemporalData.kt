package com.brein.api

import com.brein.domain.BreinConfig
import com.brein.domain.BreinResult
import com.brein.util.BreinUtil
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

/**
 * Sends an activity to the Brein engine utilizing the API.
 *
 * The call is done asynchronously as a POST request. It is important
 * that a valid API-key & secret is configured prior before using this class.
 */
class BreinTemporalData : BreinBase(), ISecretStrategy, IAsyncExecutable<BreinResult?> {
    /**
     * retrieves the configured temporalData endpoint (e.g. \temporalData)
     *
     * @return  String endpoint
     */
    override fun getEndPoint(config: BreinConfig?): String? {
        return config?.temporalDataEndpoint
    }

    override fun prepareRequestData(config: BreinConfig?, requestData: MutableMap<String, Any?>) {
        // nothing additionally to be added
    }

    /**
     * Sets the timezone within the request, i.e.:
     *
     *
     * <pre>
     * {
     * user: {
     * additional: {
     * 'timezone': timezone
     * }
     * }
     * }
    </pre> *
     *
     * @param timezone TimeZone the value to be set
     *
     * @return `this`
     */
    fun setTimezone(timezone: TimeZone?): BreinTemporalData {
        return setTimezone(timezone?.id)
    }

    /**
     * Sets the timezone within the request, i.e.:
     *
     *
     * <pre>
     * {
     * user: {
     * additional: {
     * 'timezone': timezone
     * }
     * }
     * }
    </pre> *
     *
     * @param timezone String the value to be set
     *
     * @return `this`
     */
    fun setTimezone(timezone: String?): BreinTemporalData {
        setAdditional(TIMEZONE_FIELD, timezone)
        return this
    }

    /**
     * Sets the ipAddress used to look-up the temporal information, i.e.:
     *
     *
     * <pre>
     * {
     * user: {
     * additional: {
     * 'ipAddress': ipAddress
     * }
     * }
     * }
    </pre> *
     *
     * @param ipAddress String the value to be set
     *
     * @return `this`
     */
    fun setLookUpIpAddress(ipAddress: String?): BreinTemporalData {
        setAdditional(IP_ADDRESS_FIELD, ipAddress)
        return this
    }

    /**
     * Sets the longitude used to look-up the temporal information, i.e.:
     *
     *
     * <pre>
     * {
     * user: {
     * additional: {
     * location: {
     * 'longitude': longitude
     * }
     * }
     * }
     * }
    </pre> *
     *
     * @param longitude double the longitude to be used
     *
     * @return `this`
     */
    fun setLongitude(longitude: Double): BreinTemporalData {
        setLocation(LONGITUDE_FIELD, longitude)
        return this
    }

    /**
     * Sets the latitude used to look-up the temporal information, i.e.:
     *
     *
     * <pre>
     * {
     * user: {
     * additional: {
     * location: {
     * 'latitude': latitude
     * }
     * }
     * }
     * }
    </pre> *
     *
     * @param latitude double the latitude to be used
     *
     * @return `this`
     */
    fun setLatitude(latitude: Double): BreinTemporalData {
        setLocation(LATITUDE_FIELD, latitude)
        return this
    }

    /**
     * Sets the location data using free text, i.e.:
     *
     *
     * <pre>
     * {
     * user: {
     * additional: {
     * location: {
     * 'text': freeText
     * }
     * }
     * }
     * }
    </pre> *
     *
     * @param freeText String the text describing the location
     *
     * @return `this`
     */
    fun setLocation(freeText: String?): BreinTemporalData {
        setLocation(TEXT_FIELD, freeText)
        return this
    }

    /**
     * Sets the localDateTime based on the system's time, i.e.:
     *
     *
     * <pre>
     * {
     * user: {
     * additional: {
     * 'localDateTime': now
     * }
     * }
     * }
    </pre> *
     *
     * @return `this`
     */
    fun setLocalDateTime(): BreinTemporalData {
        val defTimeZone = TimeZone.getDefault()
        val c = Calendar.getInstance()
        val date = c.time
        val df = SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss ZZZZ (zz)", Locale.US)
        df.timeZone = defTimeZone
        val strLocalDateTimeValue = df.format(date)
        return setLocalDateTime(strLocalDateTimeValue)
    }

    /**
     * Sets the localDateTime, i.e.:
     *
     *
     * <pre>
     * {
     * user: {
     * additional: {
     * 'localDateTime': zonedDateTime
     * }
     * }
     * }
    </pre> *
     *
     * @return `this`
     */
    fun setLocalDateTime(localDateTime: String?): BreinTemporalData {
        setAdditional(LOCAL_DATE_TIME_FIELD, localDateTime)
        return this
    }

    /**
     * Sets the location data using structured data (city, state, country), i.e.:
     *
     *
     * <pre>
     * {
     * user: {
     * additional: {
     * location: {
     * 'city': city,
     * 'state': state,
     * 'country': country
     * }
     * }
     * }
     * }
    </pre> *
     *
     * @param city    String the city to look up
     * @param state   String the state to look up (can be `null`)
     * @param country String the country to look up (can be `null`)
     *
     * @return `this`
     */
    fun setLocation(city: String?, state: String?, country: String?): BreinTemporalData {
        setLocation(CITY_TEXT_FIELD, city)
        setLocation(STATE_TEXT_FIELD, state)
        setLocation(COUNTRY_TEXT_FIELD, country)
        return this
    }

    /**
     * Adds the specified `shapeTypes` to the currently defined shape-types to be returned with the response of
     * the request, i.e.:
     *
     *
     * <pre>
     * {
     * user: {
     * additional: {
     * location: {
     * 'shapeTypes': [...]
     * }
     * }
     * }
     * }
    </pre> *
     *
     * @param shapeTypes String the shapeTypes to be added
     *
     * @return `this`
     */

    fun setShapeTypes(vararg shapeTypes: String?): BreinTemporalData? {
        if (shapeTypes.isEmpty()) {
            setLocation(SHAPE_TYPES_FIELD, null)
        } else {
            setLocation(SHAPE_TYPES_FIELD, ArrayList(listOf(shapeTypes)))
        }
        return this
    }

    /**
     * Gets the current value specified within the location of the request.
     *
     * @param key String the value to retrieve (e.g., `"shapeTypes"`, `"city"`, or `"latitude"`)
     * @param <T> T the expected type of the returned value
     *
     * @return the associated value to the specified key
    </T> */
    @Suppress("UNCHECKED_CAST")
    fun getLocation(key: String): Any? {
        val location = getUser()?.getAdditional(LOCATION_FIELD) as MutableMap<String, Any>
        return location[key]
    }

    /**
     *
     * @param key    String, contains the key
     * @param value  Object, contains the value
     * @return BreinTemporalData
     */
    @Suppress("UNCHECKED_CAST")
    fun setLocation(key: String, value: Any?): BreinTemporalData {

        var lloc = getUser()?.getAdditional(LOCATION_FIELD)
        if (lloc != null) {
            val loc: MutableMap<String, Any?>? =
                getUser()?.getAdditional(LOCATION_FIELD) as MutableMap<String, Any?>
            if (loc == null) {
                val newLoc = HashMap<String, Any>()
                getUser()?.setAdditional(LOCATION_FIELD, newLoc)
            }

            loc?.set(key, value)
        }
        return this
    }

    /**
     * Creates the signature for temporaldata
     *
     * @param config      BreinConfig configuration
     * @return signature as String
     */

    override fun createSignature(config: BreinConfig): String {
        val localDateTime = getUser()?.getLocalDateTime()

        val paraLocalDateTime = localDateTime ?: ""
        val timezone = getUser()?.getTimezone()
        val paraTimezone = timezone ?: ""
        val message = String.format("%d-%s-%s", this.unixTimestamp, paraLocalDateTime, paraTimezone)

        val secret = config.secret
        val signature = BreinUtil.generateSignature(message, secret!!)

        return signature
    }

    companion object {
        // The following fields are used within the additional
        private const val LOCATION_FIELD = "location"
        private const val LOCAL_DATE_TIME_FIELD = "localDateTime"
        private const val TIMEZONE_FIELD = "timezone"
        private const val IP_ADDRESS_FIELD = "ipAddress"

        // The following fields are used within the location
        private const val LONGITUDE_FIELD = "longitude"
        private const val LATITUDE_FIELD = "latitude"
        private const val SHAPE_TYPES_FIELD = "shapeTypes"
        private const val TEXT_FIELD = "text"
        private const val CITY_TEXT_FIELD = "text"
        private const val STATE_TEXT_FIELD = "text"
        private const val COUNTRY_TEXT_FIELD = "text"
    }

    override fun execute(callback: ICallback<BreinResult?>?) {
        Breinify.temporalData(this, callback)
    }

}