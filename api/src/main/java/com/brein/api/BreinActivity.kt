package com.brein.api

import com.brein.domain.BreinConfig
import com.brein.domain.BreinResult
import com.brein.domain.BreinUser
import com.brein.util.BreinMapUtil
import com.brein.util.BreinUtil
import com.brein.util.BreinUtil.containsValue

/**
 * Sends an activity to the com.brein.engine utilizing the API.
 * The call is done asynchronously as a POST request. It is important
 * that a valid API-key & secret is configured prior before using this class.
 */
class BreinActivity : BreinBase(), ISecretStrategy, IAsyncExecutable<BreinResult?> {

    /**
     * contains the tags
     */
    private var tagsMap = HashMap<String, Any>()

    /**
     * contains the fields that are part of the activity map
     */
    private var activityMap = HashMap<String, Any>()

    /**
     * returns activity type
     *
     * @return activity type
     */
    private var activityType = ""
    fun setActivityType(s: String): BreinActivity {
        this.activityType = s
        setToActivityMap("type", s)
        return this
    }

    fun getActivityType(): String {
        return this.activityType
    }

    /**
     * sets brein category
     *
     *
     * @return self
     */
    private var category: String? = ""

    fun setCategory(category: String?): BreinActivity {

        category?.let {
            this.category = category
            setToActivityMap("category", category)
        }

        return this
    }

    /**
     * retrieves brein category. if it is empty or null then
     * the default category (if set) will be used.
     *
     * @return category object
     */
    fun getCategory(config: BreinConfig?): String? {
        if (this.category.isNullOrEmpty()) {
            return config?.defaultCategory
        }

        return this.category
    }

    /**
     * retrieves the description
     *
     * @return description
     */
    private var description: String = ""

    /**
     * sets the description
     *
     * @param description string to set as description
     *
     * @return self
     */
    fun setDescription(description: String): BreinActivity {
        this.description = description
        setToActivityMap("description", description)
        return this
    }

    fun getDescription(): String {
        return this.description
    }

    /**
     * retrieves the configured activity endpoint (e.g. \activitiy)
     *
     * @return endpoint
     */
    override fun getEndPoint(config: BreinConfig?): String? {
        return config?.activityEndpoint
    }

    private fun setToActivityMap(key: String, value: Any): BreinActivity {
        this.activityMap[key] = value
        return this
    }

    /**
     * Clear the content of the instance
     */
    fun clear() {
        clearBase()
        this.tagsMap.clear()
        this.activityMap.clear()
        this.activityType = ""
        this.category = ""
        this.description = ""
    }

    /**
     * Sends an activity to the Breinify server.
     *
     * @param breinUser         the user-information
     * @param breinActivityType the type of activity
     * @param breinCategoryType the category (can be null or undefined)
     * @param description       the description for the activity
     */
    fun activity(
        breinUser: BreinUser?,
        breinActivityType: String?,
        breinCategoryType: String?,
        description: String?,
        callback: ICallback<BreinResult?>?
    ) {

        /*
         * set the values for further usage
         */
        setUser(breinUser)
        if (breinActivityType != null) {
            this.activityType = breinActivityType
        }

        if (breinCategoryType != null) {
            this.category = breinCategoryType
        }

        if (description != null) {
            this.description = description
        }

        execute(callback)
    }

    /**
     * initializes the values of this instance
     */
    fun init() {
        this.clear()
    }

    private fun getCategoryOrDefault(config: BreinConfig): String {
        val category = this.activityMap["category"]
        if (containsValue(category)) {
            return category as String
        } else {
            return config.defaultCategory
        }
    }

    override fun prepareRequestData(config: BreinConfig?, requestData: HashMap<String, Any?>) {
        val activityRequestData = HashMap<String, Any?>()

        // add the user-data, if there is any
        if (this.activityMap.isNotEmpty()) {
            // loop a Map
            for ((key, value) in this.activityMap) {
                if (containsValue(value)) {
                    activityRequestData[key] = value
                }
            }
        }

        // we have to set the category again, because it may be set to default
        activityRequestData["category"] = getCategoryOrDefault(config!!)

        // add tagsMap map if configured
        if (this.tagsMap.isNotEmpty()) {
            val tagData = BreinMapUtil.copyMap(this.tagsMap)
            activityRequestData[TAGS_FIELD] = tagData
        }

        requestData[ACTIVITY_FIELD] = activityRequestData
    }

    /**
     * Generates the signature for the request
     *
     * @return full signature
     */
    override fun createSignature(config: BreinConfig): String {
        val message = String.format("%s%d%d", this.activityType, this.unixTimestamp, 1)
        val secret = config.secret!!
        return BreinUtil.generateSignature(message, secret)
    }

    fun setToTagsDic(key: String, value: Any): BreinActivity {
        this.tagsMap[key] = value
        return this
    }

    fun setTagsDic(map: HashMap<String, Any>): BreinActivity {
        this.tagsMap = map
        return this
    }

    fun getTagsDic(): HashMap<String, Any> {
        return this.tagsMap
    }

    override fun execute(callback: ICallback<BreinResult?>?) {
        Breinify.activity(this, callback)
    }

    companion object {
        const val ACTIVITY_FIELD = "activity"
        const val TAGS_FIELD = "tags"
    }
}
