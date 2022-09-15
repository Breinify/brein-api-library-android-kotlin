package com.brein.api

import com.brein.domain.BreinConfig
import com.brein.domain.BreinResult
import com.brein.engine.BreinEngine

class Brein {

    private var config: BreinConfig? = BreinConfig()
    private var engine: BreinEngine? = BreinEngine()

    /**
     * Sets the configuration
     *
     * @param breinConfig config object
     */
    fun setConfig(breinConfig: BreinConfig?): Brein {
        this.config = breinConfig
        return this
    }

    /**
     * Sends an activity to the engine utilizing the API. The call is done asynchronously as a POST request. It is
     * important that a valid API-key is configured prior to using this function.
     *
     *
     * This request is asynchronous.
     */
    fun activity(data: BreinActivity?, callback: ICallback<BreinResult?>?) {
        getEngine()?.invoke(this.config, data, callback)
    }

    /**
     * Retrieves a lookup result from the engine. The function needs a valid API-key to be configured to succeed.
     *
     *
     * This request is synchronous.
     *
     * @param data a plain object specifying the lookup information.
     */
    fun lookup(data: BreinLookup?, callback: ICallback<BreinResult?>?) {
        getEngine()?.invoke(this.config, data, callback)
    }

    /**
     * Sends a temporalData to the engine utilizing the API. The call is done synchronously as a POST request. It is
     * important that a valid API-key is configured prior to using this function.
     *
     *
     * This request is synchronous.
     *
     */
    fun temporalData(data: BreinTemporalData?, callback: ICallback<BreinResult?>?) {
        if (callback != null) {
            getEngine()?.invoke(this.config, data, callback)
        }
    }

    /**
     * Sends a recommendation request to the engine utilizing the API. The call is done synchronously as a POST request.
     * It is important that a valid API-key is configured prior to using this function.
     *
     *
     * This request is synchronous.
     *
     * @param data contains the brein recommendation object
     */
    fun recommendation(data: BreinRecommendation?, callback: ICallback<BreinResult?>?) {
        if (callback != null) {
            getEngine()?.invoke(this.config, data, callback)
        }
    }

    /**
     * Shutdown Breinify services
     */
    fun shutdown() {
        if (this.engine != null) {
            this.engine!!.terminate()
            this.engine = null
        }
    }

    fun getEngine(): BreinEngine? {
        if (this.engine == null) {
            this.engine = BreinEngine()
        }
        return this.engine
    }

    fun getConfig(): BreinConfig? {
        return this.config
    }
}