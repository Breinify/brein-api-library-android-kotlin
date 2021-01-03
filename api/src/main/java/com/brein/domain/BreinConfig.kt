package com.brein.domain

import android.app.Application
import com.brein.api.BreinInvalidConfigurationException
import com.brein.api.BreinifyManager
import com.brein.engine.BreinEngine
import com.brein.engine.BreinEngineType
import com.brein.util.BreinUtil

/**
 * Contains Breinify Endpoint configuration
 */
class BreinConfig {

    /**
     * BASE URL
     */
    var baseUrl = DEFAULT_BASE_URL

    /**
     * contains the api-key
     */
    var apiKey: String = ""

    /**
     * Default REST client
     */
    private var restEngineType = BreinEngineType.HTTP_URL_CONNECTION_ENGINE

    /**
     * contains the activity endpoint (default = ACTIVITY_ENDPOINT)
     */
    var activityEndpoint = DEFAULT_ACTIVITY_ENDPOINT

    /**
     * contains the lookup endpoint (default = LOOKUP_ENDPOINT)
     */
    var lookupEndpoint = DEFAULT_LOOKUP_ENDPOINT

    /**
     * contains the temporalData endpoint (default = DEFAULT_TEMPORALDATA_ENDPOINT)
     */
    var temporalDataEndpoint = DEFAULT_TEMPORALDATA_ENDPOINT

    /**
     * contains the recommendation endpoint (default = DEFAULT_RECOMMENDATION_ENDPOINT)
     */
    var recommendationEndpoint = DEFAULT_RECOMMENDATION_ENDPOINT

    /**
     * set the connection timeout
     *
     */
    var connectionTimeout = DEFAULT_CONNECTION_TIMEOUT

    /**
     * Engine with default value
     */
    private var breinEngine: BreinEngine? = null

    /**
     * set the socket timeout
     *
     */
    var socketTimeout = DEFAULT_SOCKET_TIMEOUT

    /**
     * contains the default category (if set)
     *
     * @return String default category
     */
     var defaultCategory: String = ""

    /**
     * contains the configured secret
     *
     * @return String raw secret
     */
    var secret: String? = ""

    /**
     * @param apiKey  contains the Breinify com.brein.api-key
     */
    constructor(apiKey: String?) {
        setApiKey(apiKey)
        if (getRestEngineType() !== BreinEngineType.HTTP_URL_CONNECTION_ENGINE) {
            setRestEngineType(BreinEngineType.HTTP_URL_CONNECTION_ENGINE)
        }
    }

    /**
     * @param apiKey  String contains the Breinify com.brein.api-key
     * @param secret  String contains the secret
     */
    constructor(apiKey: String?, secret: String?) : this(apiKey) {
        setSecret(secret)
        initEngine()
    }

    /**
     * Configuration object
     *
     * @param apiKey            String contains the Breinify com.brein.api-key
     * @param secret            String contains the secret
     * @param breinEngineType   BreinEngineType selected com.brein.engine
     */
    constructor(apiKey: String?, secret: String?, breinEngineType: BreinEngineType) : this(apiKey) {
        setSecret(secret)
        setRestEngineType(breinEngineType)
        initEngine()
    }

    /**
     * Empty Ctor - necessary
     */
    constructor()

    /**
     * initializes the rest client
     */
    fun initEngine() {
        breinEngine = BreinEngine()
    }

    /**
     * set the base url of the breinify backend and will check
     * if the URL is valid.
     *
     * @param baseUrl  String contains the url
     * @return         BreinConfig the config object itself
     */
    fun setBaseUrl(baseUrl: String): BreinConfig {
        this.baseUrl = baseUrl
        checkBaseUrl(baseUrl)
        return this
    }

    /**
     * checks if the url is valid. If not a BreinInvalidConfigurationException will
     * be thrown.
     *
     * @param baseUrl String url to check
     */
    @Throws(BreinInvalidConfigurationException::class)
    fun checkBaseUrl(baseUrl: String) {
        if (!isUrlValid(baseUrl)) {
            val msg = ("BreinConfig issue. Value for BaseUrl is not valid. Value is: "
                    + baseUrl)
            throw BreinInvalidConfigurationException(msg)
        }
    }

    /**
     * retrieves rest type client
     *
     * @return  BreinEngineType configured rest type client
     */
    fun getRestEngineType(): BreinEngineType {
        return restEngineType
    }

    /**
     * set rest type client
     *
     * @param restEngineType  BreinEngineType rest impl
     * @return                BreinConfig the config object itself
     */
    fun setRestEngineType(restEngineType: BreinEngineType): BreinConfig {
        this.restEngineType = restEngineType
        return this
    }

    /**
     * returns the configured BreinEngine for the rest calls
     *
     * @return   BreinEngine
     */
    fun getBreinEngine(): BreinEngine? {
        return breinEngine
    }

    /**
     * sets the apikey
     *
     * @param apiKey   String the apikey
     * @return         BreinConfig the config object itself
     */
    fun setApiKey(apiKey: String?): BreinConfig {
        if (BreinUtil.containsValue(apiKey)) {
            if (apiKey != null) {
                this.apiKey = apiKey
            }
        }
        return this
    }

    /**
     * sets the lookup endpoint
     *
     * @param lookupEndpoint String endpoint
     * @return               BreinConfig the config object itself
     */
    fun setLookupEndpoint(lookupEndpoint: String): BreinConfig {
        this.lookupEndpoint = lookupEndpoint
        return this
    }

    /**
     * sets the recommendation endpoint
     *
     * @param recommendationEndpoint String endpoint
     * @return                       BreinConfig the config object itself
     */
    fun setRecommendationEndpoint(recommendationEndpoint: String): BreinConfig {
        this.recommendationEndpoint = recommendationEndpoint
        return this
    }

    /**
     * set the secret
     *
     * @param secret String raw secret
     * @return       BreinConfig the config object itself
     */
    fun setSecret(secret: String?): BreinConfig {
        this.secret = secret
        return this
    }

    /**
     * sets the default category
     *
     * @param defaultCategory String default to set
     */
    fun setDefaultCategory(defaultCategory: String): BreinConfig {
        this.defaultCategory = defaultCategory
        return this
    }

    /**
     * Sets the Android Application
     * @param application  Application instance
     * @return             BreinConfig the object itself
     */
    fun setApplication(application: Application?): BreinConfig {
        BreinifyManager.setApplication(application)
        return this
    }

    /**
     * Provides the Android Application Object
     *
     * @return Application Android Application Object
     */
    fun getApplication(): Application? {
        return BreinifyManager.getApplication()
    }

    /**
     * invokes the termination of the rest com.brein.engine.
     * Depending of the configured com.brein.engine additional threads might
     * have been allocated and this will close those threads.
     */
    fun shutdownEngine() {

        // check valid objects
        if (this.breinEngine == null) {
            return
        }
        if (this.breinEngine?.restEngine == null) {
            return
        }

        // invoke termination of the com.brein.engine
        breinEngine?.restEngine?.terminate()
    }

    /**
     * Validates if the URL is correct.
     *
     * @param url String to check
     * @return boolean true if ok otherwise false
     */
    fun isUrlValid(url: String?): Boolean {
        return BreinUtil.isUrlValid(url)!!
    }

    fun setConnectionTimeout(value: Int) : BreinConfig {
        this.connectionTimeout = value
        return this
    }

    fun setSocketTimeout(value: Int): BreinConfig {
        this.socketTimeout = value
        return this
    }

    val isSign: Boolean
        get() = secret != null && secret!!.isNotEmpty()

    companion object {
        const val VERSION = "2.0.0"
        const val DEFAULT_ACTIVITY_ENDPOINT = "/activity"
        const val DEFAULT_TEMPORALDATA_ENDPOINT = "/temporaldata"
        const val DEFAULT_RECOMMENDATION_ENDPOINT = "/recommendation"
        const val DEFAULT_LOOKUP_ENDPOINT = "/lookup"
        const val DEFAULT_CONNECTION_TIMEOUT: Int = 10000
        const val DEFAULT_SOCKET_TIMEOUT: Int = 10000
        const val DEFAULT_BASE_URL = "https://api.breinify.com"
    }
}