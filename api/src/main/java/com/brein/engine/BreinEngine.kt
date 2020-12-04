package com.brein.engine

import com.brein.api.*
import com.brein.domain.BreinConfig
import com.brein.domain.BreinResult


/**
 * Creates the Rest Engine (currently only unirest) and provides the methods to
 * invoke activity and lookup calls
 */
class BreinEngine {
    /**
     * returns the brein com.brein.engine
     *
     * @return com.brein.engine itself
     */
    /**
     * creation of rest com.brein.engine.
     */
    var restEngine: IRestEngine? = null

    /**
     * sends an activity to the breinify server
     *
     * @param activity data
     */
    @Suppress("UNUSED")
    fun sendActivity(activity: BreinActivity?) {
        if (activity != null) {
            this.restEngine!!.doRequest(activity)
        }
    }

    /**
     * performs a lookup. This will be delegated to the
     * configured restEngine.
     *
     * @param breinLookup contains the appropriate data for the lookup
     * request
     * @return if succeeded a BreinResponse object or  null
     */
    fun performLookUp(breinLookup: BreinLookup?): BreinResult? {
        return if (breinLookup != null) {
            val doLookup = restEngine!!.doLookup(breinLookup)
            doLookup
        } else null
    }

    /**
     * configuration of com.brein.engine
     *
     * @param breinConfig configuration object
     */
    fun configure(breinConfig: BreinConfig?) {
        this.restEngine!!.configure(breinConfig)
    }

    @Suppress("UNUSED_PARAMETER", "UNUSED")
    fun getRestEngineType(engine: BreinEngineType?): BreinEngineType {
        return BreinEngineType.HTTP_URL_CONNECTION_ENGINE
    }

    operator fun invoke(config: BreinConfig?, data: BreinBase?, callback: ICallback<BreinResult?>?) {
        getEngine()!!.invokeRequest(config, data, callback)
    }

    fun getEngine(): IRestEngine? {
        return this.restEngine
    }

    fun terminate() {}

    /**
     * Creates the com.brein.engine
     */
    init {
        this.restEngine = HttpUrlRestEngine()
    }
}