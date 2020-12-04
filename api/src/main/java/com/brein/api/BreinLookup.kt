package com.brein.api

import com.brein.domain.BreinConfig
import com.brein.domain.BreinDimension
import com.brein.domain.BreinResult
import com.brein.util.BreinUtil
import com.brein.util.BreinUtil.containsValue
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject


/**
 * Provides the lookup functionality
 */
class BreinLookup : BreinBase(), ISecretStrategy, IAsyncExecutable<BreinResult?> {

    // used for lookup request
    private var breinDimension: BreinDimension? = null

    /**
     * retrieves the Brein dimension object
     *
     * @return BreinDimension object
     */
    fun getBreinDimension(): BreinDimension? {
        return this.breinDimension
    }

    /**
     * sets the breindimension object - will be used for lookup
     *
     * @param breinDimension object to set
     */
    fun setBreinDimension(breinDimension: BreinDimension?): BreinLookup {
        this.breinDimension = breinDimension
        return this
    }

    /**
     * initializes the values of this instance
     */
    fun init() {
        this.breinDimension = null
    }

    /**
     * retrieves the configured lookup endpoint (e.g. \lookup)
     *
     * @param config  BreinConfig configuration object
     * @return        String endpoint
     */
    override fun getEndPoint(config: BreinConfig?): String? {
        return config?.lookupEndpoint
    }

    override fun prepareRequestData(config: BreinConfig?, requestData: MutableMap<String, Any?>) {
        // todo

    }

    /**
     * Creates the signature for lookup
     *
     * @param config      BreinConfig configuration object
     * @param requestData Map containing the data
     * @return            String containing the signature
     */
    override fun createSignature(config: BreinConfig): String {
        val dimensions: List<String> = getBreinDimension()!!.getDimensionFields()

        // we need the first one
        val message = String.format(
            "%s%d%d",
            dimensions[0],
            this.unixTimestamp,
            dimensions.size
        )

        val secret = config.secret!!
        return BreinUtil.generateSignature(message, secret)
    }

    override fun execute(callback: ICallback<BreinResult?>?) {
        Breinify.lookUp(this, callback)

    }
}