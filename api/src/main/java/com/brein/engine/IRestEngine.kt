package com.brein.engine

import com.brein.api.*
import com.brein.domain.BreinConfig
import com.brein.domain.BreinResult

/**
 * Interface for all possible rest  engines
 */
interface IRestEngine {
    /**
     * configures the rest engine
     *
     * @param breinConfig configuration object
     */
    fun configure(breinConfig: BreinConfig?)

    /**
     * invokes the post request
     *
     * @param breinActivity data
     */
    @Throws(BreinException::class)
    fun doRequest(breinActivity: BreinActivity?)

    /**
     * performs a lookup and provides details
     *
     * @param breinLookup contains request data
     * @return response from Breinify
     */
    @Throws(BreinException::class)
    fun doLookup(breinLookup: BreinLookup?): BreinResult?

    /**
     * terminates the rest engine
     */
    fun terminate()

    /**
     * Retrieves the rest engine
     *
     * @param engine BreinEngineType contains the engine
     * @return IRestEngine instance
     */
    fun getRestEngine(engine: BreinEngineType?): IRestEngine?

    /**
     * Retrieves the rest engine type
     * @param engine BreinEngineType contains type of engine
     * @return BreinEngineType
     */
    fun getRestEngineType(engine: BreinEngineType?): BreinEngineType?

    /**
     * invokes request
     * @param config    BreinConfig contains the configuration object
     * @param data      BreinBase contains the obect to send
     * @param callback  ICallback contains the callback handler
     */
    fun invokeRequest(
        config: BreinConfig?,
        data: BreinBase?,
        callback: ICallback<BreinResult?>?
    )
}