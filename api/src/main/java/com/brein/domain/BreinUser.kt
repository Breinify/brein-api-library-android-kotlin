package com.brein.domain

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.wifi.SupplicantState
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.brein.api.Breinify
import com.brein.api.BreinifyManager
import com.brein.util.BreinMapUtil
import com.brein.util.BreinUtil
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.math.BigInteger
import java.net.InetAddress
import java.net.UnknownHostException
import java.nio.ByteOrder

/**
 * A plain object specifying the user information the activity belongs to
 */

/**
 * create a brein user with field email.
 *
 * @param email of the user
 */
class BreinUser(private var email: String?) {

    enum class UserInfo {
        FIRST_NAME,
        LAST_NAME,
        PHONE_NUMBER,
        EMAIL
    }

    /**
     * contains further fields in the user section
     */
    private val userMap = HashMap<String, Any?>()

    /**
     * contains further fields in the user additional section
     */
    private val additionalMap = HashMap<String, Any?>()

    /**
     * contains the first name of the user
     *
     * @return   String the first name
     */
    private var firstName: String = ""

    /**
     * contains the last name of the user
     *
     * @return last name
     */
    private var lastName: String = ""

    /**
     * contains the sessionId (if set)
     *
     * @return sessionId
     */
    private var sessionId: String = ""

    /**
     * contains the date of birth
     *
     * @return String, date of birth
     */
    private var dateOfBirth: String = ""

    fun setDateOfBirth(dateOfBirth: String): BreinUser {
        this.dateOfBirth = dateOfBirth
        this.userMap["dateOfBirth"] = this.dateOfBirth
        return this
    }

    fun getDateOfBirth(): String {
        return this.dateOfBirth
    }

    /**
     * Set's the date of birth
     * There is no check if the month - day combination is valid, only
     * the range for day, month and year will be checked
     *
     * @param month  int month (1..12)
     * @param day    int day    (1..31)
     * @param year   int year  (1900..2100)
     * @return       BreinUser the object itself
     */
    fun setDateOfBirthValue(month: Int, day: Int, year: Int): BreinUser {
        if (month in 1..12 &&
            day in 1..31 &&
            year in 1900..2100
        ) {
            this.dateOfBirth = String.format("%s/%d/%d", month, day, year)
            this.userMap["dateOfBirth"] = this.dateOfBirth
        } else {
            this.dateOfBirth = ""
            this.userMap["dateOfBirth"] = this.dateOfBirth
        }
        return this
    }

    /**
     * contains imei (International Mobile Equipment Identity)
     *
     * @return String serial number as string
     */
    private var imei: String = ""

    /**
     * contains the deviceid
     *
     * @return String device id
     */
    private var deviceId: String = ""

    /**
     * contains the userId
     *
     * @return String userId String
     */
    private var userId: String = ""

    fun setUserId(s: String): BreinUser {
        this.userId = s
        this.userMap["userId"] = this.userId
        return this
    }

    /**
     * contains the phone number
     *
     * @return String phone number
     */
    private var phone: String = ""

    /**
     * retrieves the additional userAgent value
     *
     * @return  String user agent
     */
    private var userAgent: String = ""

    /**
     * contains the ipAddress (additional part)
     *
     * @return  String ipAddress
     */
    private var ipAddress: String = ""
    fun getIpAddress(): String {
        return this.ipAddress
    }

    /**
     * contains the additional referrer value
     *
     * @return String the referrer
     */
    private var referrer: String = ""

    /**
     * contains the timezone
     *
     * @return  String contains the timezone
     */
    private var timezone: String = ""
    fun getTimezone(): String {
        return this.timezone
    }

    /**
     * contains the additional url
     *
     * @return  String the url
     */
    private var url: String = ""
    fun getUrl(): String {
        return this.url
    }

    /**
     * contains the localDateTime
     *
     * @return  String the local date time
     */
    private var localDateTime: String = ""
    fun getLocalDateTime(): String {
        return this.localDateTime
    }

    /**
     * retrieves the pushDeviceToken
     *
     * @return    String the deviceRegistration token
     */
    private var pushDeviceRegistration: String = ""

    fun getPushDeviceRegistration(): String {
        return this.pushDeviceRegistration
    }

    fun setEmail(e: String): BreinUser {
        this.email = e
        this.userMap["email"] = this.email
        return this
    }

    fun setIpAddress(s: String): BreinUser {
        this.ipAddress = s
        this.additionalMap["ipAddress"] = this.ipAddress
        return this
    }

    fun setUserAgent(s: String): BreinUser {
        this.userAgent = s
        this.additionalMap["userAgent"] = this.userAgent
        return this
    }

    fun setDeviceId(s: String): BreinUser {
        this.deviceId = s
        this.userMap["deviceId"] = this.deviceId
        return this
    }

    fun getDeviceId(): String {
        return this.deviceId
    }

    fun setImei(s: String): BreinUser {
        this.imei = s
        this.userMap["imei"] = this.imei
        return this
    }

    fun setSessionId(s: String): BreinUser {
        this.sessionId = s
        this.userMap["sessionId"] = this.sessionId
        return this
    }

    fun getSessionId(): String? {
        return this.sessionId
    }

    fun setUrl(s: String): BreinUser {
        this.url = s
        this.additionalMap["url"] = this.url
        return this
    }

    fun setReferrer(s: String): BreinUser {
        this.referrer = s
        this.additionalMap["referrer"] = this.referrer
        return this
    }

    fun getReferrer(): String {
        return this.referrer
    }

    fun setLastName(s: String): BreinUser {
        this.lastName = s
        this.userMap["lastName"] = this.lastName
        return this
    }

    fun getLastName(): String {
        return this.lastName
    }

    fun setFirstName(s: String): BreinUser {
        this.firstName = s
        this.userMap["firstName"] = this.firstName
        return this
    }

    fun getFirstName(): String {
        return this.firstName
    }

    fun setTimezone(timezone: String): BreinUser {
        this.timezone = timezone
        this.additionalMap["timezone"] = this.timezone
        return this
    }

    fun setLocalDateTime(localDateTime: String): BreinUser {
        this.localDateTime = localDateTime
        this.additionalMap["localDateTime"] = this.localDateTime
        return this
    }

    fun setPushDeviceRegistration(deviceToken: String?): BreinUser {
        if (deviceToken != null) {
            this.pushDeviceRegistration = deviceToken
            val identifierMap = HashMap<String, Any?>()

            identifierMap["androidPushDeviceRegistration"] = this.pushDeviceRegistration
            this.additionalMap["identifiers"] = identifierMap
        }

        return this
    }

    fun resetDateOfBirth(): BreinUser {
        this.dateOfBirth = ""
        return this
    }

    fun setPhone(phone: String): BreinUser {
        this.phone = phone
        this.userMap["phone"] = this.phone
        return this
    }

    constructor() : this("") {
    }

    /**
     * Creates the userAgent String in Android standard format and adds the
     * app name.
     *
     * @return String userAgent
     */
    private fun createUserAgent(): String {
        val appCtx: Application? = BreinifyManager.getApplication()
        var appName = ""
        if (appCtx != null) {
            appName = appCtx.applicationInfo.loadLabel(appCtx.packageManager).toString()
        }

        // add the app
        val httpAgent = System.getProperty("http.agent")
        return if (httpAgent != null) {
            String.format("%s/(%s)", httpAgent, appName)
        } else {
            ""
        }
    }

    /**
     * detects the GPS coordinates and adds this to the user.additional.location section
     */
    private fun detectGpsCoordinates() {

        // firstly get the context
        val applicationContext: Application = Breinify.config?.getApplication() ?: return
        val locationManager: LocationManager =
            applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers: List<String> = locationManager.allProviders // getProviders(true);

        // Loop over the array backwards, and if you get an accurate location, then break out the loop
        val location: Location? = null
        for (index in providers.indices.reversed()) {
            val accessFineLocationPermission: Int = ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            val accessCoarseLocationPermission: Int = ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            if (accessCoarseLocationPermission != PackageManager.PERMISSION_GRANTED ||
                accessFineLocationPermission != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

        }
        if (location != null) {
            val locationData = JsonObject()
            locationData.addProperty("accuracy", location.accuracy)
            locationData.addProperty("speed", location.speed)
            locationData.addProperty("latitude", location.latitude)
            locationData.addProperty("longitude", location.longitude)
            this.additionalMap["location"] = locationData
        }
    }

    /**
     * Provides network information within the user additional request
     */
    private fun detectNetwork() {

        // firstly get the context
        val applicationContext: Application = Breinify.config?.getApplication() ?: return

        // only possible if permission has been granted
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_WIFI_STATE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val wifiManager: WifiManager = applicationContext
                .applicationContext
                .getSystemService(WIFI_SERVICE) as WifiManager

            val wifiInfo: WifiInfo = wifiManager.connectionInfo

            val wifiInfoStatus = wifiInfo.supplicantState
            if (wifiInfoStatus == SupplicantState.COMPLETED) {
                var ssid = ""
                var bssid = ""
                var ip = 0

                // contains double quotes
                wifiInfo.ssid?.let { ssid = wifiInfo.ssid.replace("\"", "") }
                wifiInfo.bssid?.let { bssid = wifiInfo.bssid }
                wifiInfo.ipAddress.let { ip = wifiInfo.ipAddress }

                // Convert little-endian to big-endianif needed
                if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
                    ip = Integer.reverseBytes(ip)
                }

                val detectedIpAddress = try {
                    val ipByteArray: ByteArray = BigInteger.valueOf(ip.toLong()).toByteArray()
                    InetAddress.getByAddress(ipByteArray).hostAddress
                } catch (ex: UnknownHostException) {
                    Log.e("WIFIIP", "Breinify - unable to get host address.")
                    null
                }

                if (detectedIpAddress != null) {
                    if (detectedIpAddress.isNotEmpty()) {
                        setIpAddress(detectedIpAddress)
                    }
                }

                val linkSpeed: Int = wifiInfo.linkSpeed
                val macAddress = ""
                val rssi: Int = wifiInfo.rssi
                val networkId: Int = wifiInfo.networkId
                val state: String = wifiInfo.supplicantState.toString()
                val networkData = JsonObject()
                networkData.addProperty("ssid", ssid)
                networkData.addProperty("bssid", bssid)
                networkData.addProperty("ipAddress", this.ipAddress)
                networkData.addProperty("linkSpeed", linkSpeed)
                networkData.addProperty("macAddress", macAddress)
                networkData.addProperty("rssi", rssi)
                networkData.addProperty("networkId", networkId)
                networkData.addProperty("state", state)

                this.additionalMap["network"] = networkData
            }
        }
    }

    /**
     * provides a nicer output of the user details
     *
     * @return String nicer output
     */
    override fun toString(): String {
        val config = BreinConfig(null)
        val requestData = HashMap<String, Any?>()

        prepareRequestData(config, requestData)

        return Gson().toJson(requestData)
    }

    /**
     * Sets the users value and overrides any current value. Cannot used to override the `additional` field.
     *
     * @param key    String the name of the value to be set
     * @param value  Object the value to be set
     * @return       BreinUser the object itself
     */
    operator fun set(key: String, value: Any?, additional: Boolean): BreinUser {
        if (additional) {
            this.additionalMap[key] = value
        } else {
            this.userMap[key] = value
        }

        return this
    }

    /**
     * Retrieves for a given key the object
     * @param key   String, contains the key
     * @param <T>   T contains the object
     * @return      T contains the object
    </T> */
    operator fun get(key: String): Any? {
        return get(key, false)
    }

    /**
     * Retrieves for a given key within the additional or userMap the value
     *
     * @param key         String, contains the key
     * @param additional  boolean true if additional part should be used
     * @param <T>         T contains the value
     * @return            T contains the value
    </T> */
    operator fun get(key: String, additional: Boolean): Any? {
        return if (additional) {
            this.additionalMap[key]
        } else {
            this.userMap[key]
        }
    }

    /**
     * Retrieves the additional value
     *
     * @param key  String contains the key
     * @param <T>  T contains the value
     * @return     T contains the value
    </T> */
    fun getAdditional(key: String): Any? {
        return get(key, true)
    }

    /**
     * Sets an additional value.
     *
     * @param key    String, the name of the additional value to be set
     * @param value  Object the value to be set
     * @return       BreinUser the object itself
     */
    fun setAdditional(key: String, value: Any?): BreinUser {
        this.additionalMap[key] = value
        return this
    }

    /**
     * prepares the request data
     *
     * @param config      BreinConfig, contains the configuration (if necessary)
     * @param requestData Map request destination
     */
    @Suppress("UNUSED_PARAMETER")
    fun prepareRequestData(config: BreinConfig?, requestData: HashMap<String, Any?>) {
        val userRequestData = HashMap<String, Any?>()

        requestData["user"] = userRequestData

        // add the user-data, if there is any
        if (this.userMap.isNotEmpty()) {
            // loop a Map
            for ((key, value) in this.userMap) {
                if (BreinUtil.containsValue(value)) {
                    userRequestData[key] = value
                }
            }
        }

        // tries to detect gps and network data this will be added to property additionalMap
        detectGpsCoordinates()
        detectNetwork()

        // check or create userAgent
        generateUserAgent()

        // add the additional-data, if there is any
        if (this.additionalMap.isNotEmpty()) {
            userRequestData["additional"] = BreinMapUtil.copyMap(this.additionalMap)
        }
    }

    /**
     * Checks if a userAgent has been set if not it will be generated and set.
     */
    private fun generateUserAgent() {
        if (this.userAgent.isEmpty()) {
            this.userAgent = createUserAgent()
        }

        if (this.userAgent.isNotEmpty()) {
            this.additionalMap["userAgent"] = this.userAgent
        }
    }

}