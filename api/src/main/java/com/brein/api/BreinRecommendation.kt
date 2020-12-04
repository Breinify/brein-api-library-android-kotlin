package com.brein.api

import com.brein.domain.BreinConfig
import com.brein.domain.BreinResult
import com.brein.util.BreinMapUtil
import com.brein.util.BreinUtil
import java.util.*

class BreinRecommendation : BreinBase(), IAsyncExecutable<BreinResult?> {
    /**
     * get the number of recommendations
     *
     * @return number
     */
    /**
     * contains the number of recommendations - default is 3
     */
    var numberOfRecommendations = 3

    /**
     * get the recommendation category
     *
     * @return String category
     */
    /**
     * contains the category for the recommendation
     */
    var category: String? = null


    /**
     * set the number of recommendations
     *
     * @param numberOfRecommendations int number of recommendations
     *
     * @return self
     */
    fun setNumberOfRecommendations(numberOfRecommendations: Int): BreinRecommendation {
        this.numberOfRecommendations = numberOfRecommendations
        return this
    }

    /**
     * set the recommendation category
     *
     * @param category String contains the category
     *
     * @return self
     */
    fun setCategory(category: String?): BreinRecommendation {
        this.category = category
        return this
    }

    /**
     *
     * @param config the current configuration
     *
     * @return
     */
    override fun getEndPoint(config: BreinConfig?): String? {
        if (config != null) {
            return config.recommendationEndpoint
        }
        return ""
    }

    /**
     * Used to create the request object
     *
     * @param config      BreinConfig contains the configuration object
     * @param requestData Map the request data to be sent to the endpoint
     */
    override fun prepareRequestData(config: BreinConfig?, requestData: MutableMap<String, Any?>)  {

        // recommendation data
        val recommendationData: MutableMap<String, Any?> = HashMap()

        // check optional field(s)
        if (BreinUtil.containsValue(category)) {
            recommendationData["recommendationCategory"] = category
        }

        // mandatory field
        recommendationData["numRecommendations"] = numberOfRecommendations
        requestData["recommendation"] = recommendationData
    }

    /**
     * Generates the signature for the request
     *
     * @param config       BreinConfig contains the configuration
     * @return             String full signature
     */
    override fun createSignature(config: BreinConfig): String {
        val message = String.format("%d", this.unixTimestamp)
        val secret = config.secret
        return BreinUtil.generateSignature(message, secret!!)
    }

    /**
     *
     * @param callback the callback containing the response of the request, can be `null`
     */
    override fun execute(callback: ICallback<BreinResult?>?) {
        Breinify.recommendation(this, callback)
    }

}