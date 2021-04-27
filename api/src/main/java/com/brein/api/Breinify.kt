package com.brein.api

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import com.brein.domain.*
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

/**
 * Static Implementation of Breinify activity lookup calls
 */
class Breinify {

    companion object {
        const val version: String = BreinConfig.VERSION
        private const val TAG = "Breinify"

        private var lastConfig: BreinConfig? = BreinConfig()
        private var lastBrein: Brein? = null
        private val breinUser = BreinUser()
        private val breinActivity = BreinActivity()
        private val breinLookup = BreinLookup()
        private val breinTemporalData = BreinTemporalData()

        fun getBreinUser(): BreinUser {
            return this.breinUser
        }

        fun getBreinActivity(): BreinActivity {
            return this.breinActivity
        }

        fun getBreinLookup(): BreinLookup {
            return this.breinLookup
        }

        fun getBreinTemporalData(): BreinTemporalData {
            return this.breinTemporalData
        }

        fun configure(apiKey: String?, secret: String?) {
            if (apiKey.isNullOrEmpty()) {
                throw BreinException(BreinException.API_KEY_NOT_SET)
            }
            setConfig(apiKey, secret)
        }

        /**
         * Specifies the overall configuration used by the library. The configuration must be set prior to any call to the
         * API.
         *
         * @param config the configuration to use
         * @return the `Brein` instance, usable if multiple different configurations are used
         * @see Brein
         *
         * @see BreinConfig
         */
        fun setConfig(config: BreinConfig?): Brein {
            lastConfig = config
            return Brein().setConfig(config)
        }

        /**
         * Specifies the overall configuration used by the library. The configuration must be set prior to any call to the
         * API.
         *
         * @param apiKey the API key to be used
         * @return the `Brein` instance, usable if multiple different configurations are used
         * @see Brein
         */
        fun setConfig(apiKey: String?): Brein {
            return setConfig(apiKey, null)
        }

        /**
         * Specifies the overall configuration used by the library. The configuration must be set prior to any call to the
         * API.
         *
         * @param apiKey the API key to be used
         * @param secret the secret to be used to sign the messages (Verification Signature must be enabled for the API
         * key)
         * @return the `Brein` instance, usable if multiple different configurations are used
         * @see Brein
         */
        fun setConfig(apiKey: String?, secret: String?): Brein {
            return setConfig(BreinConfig(apiKey, secret))
        }

        /**
         * Initializes the instance
         *
         * @param application  Application contains the application context
         * @param mainActivity Activity contains the main activity
         * @param apiKey       String contains the apiKey
         * @param secret       String contains the secret
         */
        fun initialize(
            application: Application?,
            mainActivity: Activity?,
            apiKey: String?,
            secret: String?
        ) {
            val backgroundTimeInMS = 60 * 1000.toLong()
            configure(apiKey, secret)
            initialize(application, mainActivity, apiKey, secret, backgroundTimeInMS)
        }

        /**
         * Initializes the instance
         *
         * @param application        Application contains the application context
         * @param mainActivity       Activity contains the main activity
         * @param apiKey             String contains the apiKey
         * @param secret             String contains the secret
         * @param backgroundInterval long sets a background interval
         */
        fun initialize(
            application: Application?,
            mainActivity: Activity?,
            apiKey: String?,
            secret: String?,
            backgroundInterval: Long
        ) {
            configure(apiKey, secret)
            BreinifyManager.initialize(
                application,
                mainActivity,
                apiKey,
                secret,
                backgroundInterval
            )
        }

        /**
         * configures the deviceToken
         *
         * @param deviceToken String contains the deviceToken
         */
        fun configureDeviceToken(deviceToken: String?) {
            BreinifyManager.configureDeviceToken(deviceToken)
        }

        /**
         * configures the deviceToken
         *
         * @param deviceToken String contains the deviceToken
         */
        fun initWithDeviceToken(deviceToken: String?, userInfoMap: HashMap<String, String>?) {

            userInfoMap?.let {
                val firstName: String = userInfoMap["firstName"] ?: ""
                val lastName: String = userInfoMap["lastName"] ?: ""
                val phone: String = userInfoMap["phone"] ?: ""
                val email: String = userInfoMap["email"] ?: ""

                setUserInfo(firstName, lastName, phone, email)
            }

            if (deviceToken != null) {
                getUser().setPushDeviceRegistration(deviceToken)
            }
            BreinifyManager.configureDeviceToken(deviceToken)

        }

        private fun setUserInfo(firstName: String, lastName: String, phone: String, email: String) {
            val appUser = getBreinUser()
            appUser.setFirstName(firstName)
                .setLastName(lastName)
                .setPhone(phone)
                .setEmail(email)

            BreinifyManager.userEmail = email
        }

        /**
         * gets the config
         *
         * @return BreinConfig config
         */
        val config: BreinConfig?
            get() = lastConfig

        fun getUser(): BreinUser {
            return this.breinUser
        }

        init {
            breinActivity.setUser(getUser())
            breinLookup.setUser(getUser())
            breinTemporalData.setUser(getUser())
        }

        /**
         * Service method to set the email property that is part of the
         * BreinifyManager instance
         *
         */
        var email: String?
            get() = BreinifyManager.userEmail
            set(email) {
                BreinifyManager.userEmail = email
            }

        /**
         * Service method to set the userId property that is part of the
         * BreinifyManager instance
         *
         */
        var userId: String?
            get() = BreinifyManager.userId
            set(userId) {
                BreinifyManager.userId = userId
            }

        /**
         * sets the pushDeviceToken
         *
         */
        var pushDeviceRegistration: String?
            get() = BreinifyManager.pushDeviceRegistration
            set(token) {
                BreinifyManager.pushDeviceRegistration = token
            }

        /**
         * Returns the session id
         *
         * @return String containing the session id
         */
        val sessionId: String? = getUser().getSessionId()

        /**
         * Delegate to save userDefaults
         */
        fun saveUserDefaults() {
            BreinifyManager.saveUserDefaults()
        }

        /**
         * Delegate to send activities
         *
         * @param activity        Contains the activity object
         * @param callback        Contains the callback object
         */
        fun sendActivity(activity: BreinActivity?, callback: ICallback<BreinResult?>? = null) {

            try {
                this.activity(activity, callback)
            } catch (e: Exception) {
                Log.e(TAG, "could not sendActivity due to exception: $e")
            }

        }


        private fun sendActivity(activityType: String, tagsMap: HashMap<String, Any>?) {

            try {
                // save current map
                val curTagsMap = this.breinActivity.getTagsDic()

                if (tagsMap != null) {
                    this.breinActivity.setTagsDic(tagsMap)
                }

                this.activity(
                    getBreinUser(),
                    activityType,
                    null,
                    null
                )

//                this.breinActivity.setTagsDic(curTagsMap)
            } catch (e: Exception) {
                Log.e(TAG, "could not send send activity due to exception: $e")
            }
        }

        /**
         * Sends an activity to the engine utilizing the API. The call is done asynchronously as a POST request. It is
         * important that a valid API-key is configured prior to using this function.
         *
         *
         * This request is asynchronous.
         *
         * @param user         BreinUser a plain object specifying the user information the activity belongs to
         * @param activityType String the type of the activity collected, i.e., one of search, login, logout, addToCart,
         * removeFromCart, checkOut, selectProduct, or other. if not specified, the default other will
         * be used
         * @param category     String the category of the platform/service/products, i.e., one of apparel, home, education, family,
         * food, health, job, services, or other
         * @param description  String a string with further information about the activity performed
         * @param callback     ICallback function
         */
        fun activity(
            user: BreinUser?,
            activityType: String?,
            category: String?,
            description: String?,
            callback: ICallback<BreinResult?>?
        ) {

            if (user == null) {
                throw BreinException(BreinException.USER_NOT_SET)
            }

            val activity = BreinActivity()
            activity.setUser(user)
            if (activityType != null) {
                activity.setActivityType(activityType)
            } else {
                throw BreinException(BreinException.ACTIVITY_TYPE_NOT_SET)
            }

            if (category != null) {
                activity.setCategory(category)
            }
            if (description != null) {
                activity.setDescription(description)
            }

            activity(activity, callback)
        }

        /**
         * Sends an activity to the engine utilizing the API. The call is done asynchronously as a POST request. It is
         * important that a valid API-key is configured prior to using this function.
         *
         *
         * This request is asynchronous.
         *
         * @param user         BreinUser a plain object specifying the user information the activity belongs to
         * @param activityType String the type of the activity collected, i.e., one of search, login, logout, addToCart,
         * removeFromCart, checkOut, selectProduct, or other. if not specified, the default other will
         * be used
         * @param category     String the category of the platform/service/products, i.e., one of apparel, home, education, family,
         * food, health, job, services, or other
         * @param description  String a string with further information about the activity performed
         */
        fun activity(
            user: BreinUser?,
            activityType: String?,
            category: String?,
            description: String?
        ) {
            // invoke activity call without callback
            activity(user, activityType, category, description, null)
        }

        /**
         * Method to send an activity asynchronous.
         *
         * @param activityType String the activity type to be sent
         * @see BreinActivity
         */
        fun activity(activityType: String?) {
            breinActivity.setUser(breinUser)
            if (activityType != null) {
                breinActivity.setActivityType(activityType)
            }
            activity(breinActivity, null)
        }

        /**
         * Method to send an activity asynchronous.
         *
         * @param activity BreinActivity the `BreinActivity` to be sent
         * @see BreinActivity
         */
        @JvmOverloads
        fun activity(activity: BreinActivity?, callback: ICallback<BreinResult?>? = null) {

            val actType = activity?.getActivityType()
            if (actType.isNullOrEmpty()) {
                throw BreinException("Activity Type not set")
            } else {
                // send the activity
                brein!!.activity(activity, callback)
            }
        }

        /**
         * Method to retrieve temporal information based on temporal data. This method uses the available information from
         * the system it is running on to be passed to the API, which resolves the temporal information. Normally (if not
         * using a VPN) the ip-address is a good source to determine, e.g., the location.
         *
         * @param callback ICallback to be invoked
         */
        fun temporalData(callback: ICallback<BreinResult?>?) {
            val data = BreinTemporalData().setLocalDateTime()
            temporalData(data, callback)
        }

        /**
         * Method to retrieve temporal information based on temporal data. This method uses the `latitude` and `longitude` to determine further information, i.e., weather, location, events, time, timezone, and holidays.
         *
         * @param latitude   double the latitude of the geo-coordinates to resolve
         * @param longitude  double the longitude of the geo-coordinates to resolve
         * @param callback   ICallback to be invoked
         * @param shapeTypes String the shape-types to retrieve (if empty, no shape-types will be returned), e.g., CITY,
         * NEIGHBORHOOD, ZIP-CODES
         */
        fun temporalData(
            latitude: Double,
            longitude: Double,
            callback: ICallback<BreinResult?>?,
            vararg shapeTypes: String?
        ) {
            val data = BreinTemporalData()
                .setLongitude(longitude)
                .setLatitude(latitude)
                .setShapeTypes(*shapeTypes)

            brein!!.temporalData(data, callback)
        }

        /**
         * Method to retrieve temporal information based on temporal data. This method uses the `ipAddress` to
         * determine further information, i.e., weather, location, events, time, timezone, and holidays.
         *
         * @param ipAddress String the address to resolve the information for
         * @param callback  ICallback to be invoked
         */
        fun temporalData(ipAddress: String?, callback: ICallback<BreinResult?>?) {
            val data = BreinTemporalData().setLookUpIpAddress(ipAddress)
            brein!!.temporalData(data, callback)
        }

        /**
         * Method to retrieve temporal information based on temporal data. This method uses the available information from
         * the system it is running on to be passed to the API, which resolves the temporal information. Normally (if not
         * using a VPN) the ip-address is a good source to determine, e.g., the location.
         *
         * @param data     BreinTemporalData contains the object
         * @param callback ICallback to be invoked
         */
        fun temporalData(data: BreinTemporalData?, callback: ICallback<BreinResult?>?) {
            brein!!.temporalData(data, callback)
        }

        /**
         * Invokes recommendation request
         *
         * @param data     BreinRecommendation instance
         * @param callback ICallback constains callback handler
         */
        fun recommendation(data: BreinRecommendation?, callback: ICallback<BreinResult?>?) {
            brein!!.recommendation(data, callback)
        }

        /**
         * Retrieves a lookup result from the engine. The function needs a valid API-key to be configured to succeed.
         *
         *
         * This request is synchronous.
         *
         * @param data     BreinLookup a plain object specifying information about the brein lookup data.
         * @param callback ICallback a method invoked with the result set.
         */
        fun lookUp(data: BreinLookup?, callback: ICallback<BreinResult?>?) {
            brein!!.lookup(data, callback)
        }

        /**
         * Checks if the data payload contains a "breinify" element.
         *
         */
        fun isBreinifyPushNotificationMessage(remoteMessage: RemoteMessage): Boolean {
            return !remoteMessage.data["breinify"].isNullOrEmpty()
        }

        /**
         * Handle the received PushNotification.
         *
         * Delegate to BreinPushNotification
         */
        fun onMessageReceived(context: Context, remoteMessage: RemoteMessage) {
            val breinifyPayload = remoteMessage.data["breinify"]
            val type = object : TypeToken<HashMap<String, Any>>() {}.type
            val gson = GsonBuilder().setPrettyPrinting().create()
            var campaign: HashMap<String, Any> = HashMap()

            if (breinifyPayload != null) {
                // from json -> Hashmap
                val breinifyMap: Map<String, Any> =
                    gson.fromJson(
                        breinifyPayload,
                        type
                    )

                campaign = gson.fromJson(
                    gson.toJson(breinifyMap["campaign"]),
                    type
                )
            }

            // @todo Marco
            //     adapted
            sendActivity(
                getBreinActivity().setActivityType(BreinActivityType.RECEIVED_PUSH_NOTIFICATION)
                    .setTagsDic(campaign)
            )
//            sendActivity(
//                BreinActivityType.RECEIVED_PUSH_NOTIFICATION,
//                campaign
//            )
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                BreinPushNotificationService.onMessageReceived(context, remoteMessage)
            } else {
                BreinPushNotificationService.onMessageReceivedLegacy(context, remoteMessage)
            }
        }

        /**
         * Returns the last brein object
         *
         * @return Brein
         */
        protected val brein: Brein?
            get() {
                if (lastBrein == null) {
                    lastBrein = Brein().setConfig(lastConfig)
                }
                return lastBrein
            }

        /**
         * Shutdown Breinify services
         */
        fun shutdown() {
            if (config != null) {
                config!!.shutdownEngine()
            }
            BreinifyManager.shutdown()
        }

    }
}