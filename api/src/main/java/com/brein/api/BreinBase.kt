package com.brein.api

import com.brein.domain.BreinConfig
import com.brein.domain.BreinIpInfo
import com.brein.domain.BreinUser
import com.brein.util.BreinUtil
import com.google.gson.Gson

/**
 * Base Class for activity and lookup operations.
 */
abstract class BreinBase : ISecretStrategy {

    private var config: BreinConfig? = null

    fun getConfig(): BreinConfig? {
        return this.config
    }

    fun setConfig(config: BreinConfig?) {
        this.config = config
    }

    /**
     * Contains user information for the request
     */
    private var user: BreinUser? = null

    /**
     * The base data for the request
     */
    private var baseMap = mutableMapOf<String, Any?>()

    /**
     * Retrieves the current `BreinUser` for the request. This method never returns `null`, instead it
     * creates an empty `BreinUser` instance if none is available so far.
     *
     * @return the current `BreinUser` for the request
     */
    fun getUser(): BreinUser? {
        if (user == null) {
            user = BreinUser()
        }
        return user
    }

    /**
     * Sets the `BreinUser` instance for the request. It is recommended to get the user (using `getUser()`)
     * and manipulate the retrieved instance directly or to use the `setUser(key, value)` method to set user
     * specific data.
     *
     * @param user the `BreinUser` to set
     *
     * @return this
     *
     */
    fun setUser(user: BreinUser?): BreinBase {
        this.user = user
        return this
    }

    /**
     * Sets a specific data point for the user data of the request, i.e.:
     *
     *
     * <pre>
     * {
     * user: {
     * 'key': 'value'
     * }
     * }
    </pre> *
     *
     *
     * Typically, that would be, e.g., `email`,
     * `sessionId`, and/or `userId`
     *
     * @param key   the value to be set (e.g., `"email"` or `"sessionId"`)
     * @param value the value to be set for the specified key
     *
     * @return `this`
     */
    fun setUser(key: String, value: Any?): BreinBase {
        getUser()?.set(key, value, false)
        return this
    }

    /**
     * Sets a specific data point for the additional part of the request, i.e.:
     *
     *
     * <pre>
     * {
     * user: {
     * additional: {
     * 'key': 'value'
     * }
     * }
     * }
    </pre> *
     *
     * @param key   the value to be set (e.g., `"localDateTime"`, `"userAgent"`, `"referrer"` or
     * `"timezone"`)
     * @param value the value to be set for the specified key
     *
     * @return `this`
     */
    fun setAdditional(key: String, value: Any?): BreinBase {
        getUser()?.setAdditional(key, value)
        return this
    }

    /**
     * Gets the endpoint to be used to send the request to
     *
     * @param config the current configuration
     *
     * @return the endpoint to be used to send the request to
     *
     * @see BreinConfig
     */
    abstract fun getEndPoint(config: BreinConfig?): String?

    /**
     * Retrieves the currently set `unixTimestamp`. If now should be used as timestamp, the method returns `-1L`.
     *
     * @return unix timestamp
     */
    var unixTimestamp: Long = 0

    /**
     * Sets the timestamp.
     *
     * @param unixTimestamp unix timestamp to be used
     *
     * @return this
     */
    protected fun setUnixTimestamp(unixTimestamp: Long): BreinBase {
        this.unixTimestamp = unixTimestamp
        addToBaseMap(UNIX_TIMESTAMP_FIELD, unixTimestamp)
        return this
    }

    private fun addToBaseMap(key: String, value: Any?): BreinBase {
        this.baseMap[key] = value
        return this
    }

    protected fun getFromBaseMap(key: String): Any? {
        return this.baseMap[key]
    }

    /**
     * sets the ipaddress
     *
     * @param ipAddress contains the ipAddress
     *
     * @return this
     */
    fun setClientIpAddress(ipAddress: String?): BreinBase {
        ipAddress?.let { addToBaseMap(IP_ADDRESS, ipAddress) }
        return this
    }

    fun clearBase() {
        this.baseMap.clear()
        this.unixTimestamp = 0
    }

    /**
     * This method adds the request specific information to the `requestData`. It is called by [ ][.prepareRequestData] after the request data of the base information is added.
     *
     * @param requestData the request data to be sent to the endpoint
     */
    abstract fun prepareRequestData(config: BreinConfig?, requestData: MutableMap<String, Any?>)

    /**
     * Method to generate the body part of the request.
     *
     * @param config the configuration used to create the request body
     *
     * @return the created request body (JSON)
     */
    open fun prepareRequestData(config: BreinConfig): String {
        val requestData = HashMap<String, Any?>()

        requestData[API_KEY_FIELD] = config.apiKey

        // add the base values
        if (this.baseMap.isNotEmpty()) {
            for ((key, value) in this.baseMap) {
                if (BreinUtil.containsValue(value)) {
                    requestData[key] = value
                }
            }
        }

        // set current unixtimestamp
        val timestamp = System.currentTimeMillis() / 1000L
        setUnixTimestamp(timestamp)
        requestData[UNIX_TIMESTAMP_FIELD] = timestamp

        // check if we have user data
        this.user?.prepareRequestData(config, requestData)

        // add the sub-type specific values
        prepareRequestData(config, requestData)

        // check if we have to add a signature
        if (config.isSign) {
            requestData[SIGNATURE_FIELD] = createSignature(config)
            requestData[SIGNATURE_TYPE_FIELD] = "HmacSHA256"
        }

        setClientIpAddress(BreinIpInfo.externalIp)

        return Gson().toJson(requestData)
    }

    companion object {
        const val API_KEY_FIELD = "apiKey"
        const val UNIX_TIMESTAMP_FIELD = "unixTimestamp"
        const val SIGNATURE_FIELD = "signature"
        const val SIGNATURE_TYPE_FIELD = "signatureType"
        const val IP_ADDRESS = "ipAddress"
    }
}