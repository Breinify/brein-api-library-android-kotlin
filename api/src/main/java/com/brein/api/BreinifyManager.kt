package com.brein.api


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.brein.util.BreinUtil
import java.util.*

@SuppressLint("StaticFieldLeak")
object BreinifyManager {

    // contains the device token
    var pushDeviceRegistration: String? = null

    var userEmail: String? = null

    var userId: String? = null

    var sessionId: String? = null

    // application context
    private var application: Application? = null

    // contains the main activity
    private var mainActivity: Activity? = null

    // instance of push notification service
    private val breinPushNotificationReceiver = BreinPushNotificationReceiver()

    private var apiKey: String? = null

    private var secret: String? = null

    private var backgroundInterval: Long = 0

    /**
     * Set the deviceRegistration
     *
     * @param pushDeviceRegistration String
     */
    fun setDeviceRegistration(pushDeviceRegistration: String) {
        Log.d(TAG, "pushDeviceRegistration is: $pushDeviceRegistration")
        this.pushDeviceRegistration = pushDeviceRegistration

        // set user as well -> necessary for correct request
        Breinify.getUser().pushDeviceRegistration = pushDeviceRegistration
    }

    /**
     * Provides the Android Application Object
     *
     * @return Application contains Android Application
     */
    @Suppress("UNUSED")
    fun getApplication(): Application? {
        return application
    }

    /**
     *
     * @return Activity mainActivity
     */
    @Suppress("UNUSED")
    fun getMainActivity(): Activity? {
        return mainActivity
    }

    /**
     *
     * @param mainActivity Activity contains main activity
     */
    @Suppress("UNUSED")
    fun setMainActivity(mainActivity: Activity?) {
        this.mainActivity = mainActivity
    }

    /**
     * Sets the Android Application Object
     *
     * @param application Android Application Object
     */
    @Suppress("UNUSED")
    fun setApplication(application: Application?) {
        this.application = application
    }

    /**
     * Initializes the appropriate callbacks and timer for background processing
     *
     * @param application  Application contains the application object
     * @param mainActivity Activity contains main activity
     * @param apiKey       String contains the Breinify API Key
     * @param secret       String contains the Breinfiy secret
     */
    @Suppress("UNUSED")
    fun initialize(
        application: Application?,
        mainActivity: Activity?,
        apiKey: String?,
        secret: String?,
        backgroundInterval: Long
    ) {

        this.application = application
        this.mainActivity = mainActivity
        this.apiKey = apiKey
        this.secret = secret
        this.backgroundInterval = backgroundInterval
        initLifecycleAndEngine(backgroundInterval)

        // invoke ipAddress detection now

        // todo check how to invoke
        // BreinIpInfo.instance
    }

    /**
     * Initializes the Lifecycle support and Engine
     *
     * @param backgroundInterval long contains background interval
     */
    private fun initLifecycleAndEngine(backgroundInterval: Long) {
        // register the callbacks for lifecycle support - necessary to determine if app
        // is in background or foreground
        application?.registerActivityLifecycleCallbacks(BreinifyLifecycle())

        // configure the API
        Breinify.setConfig(apiKey, secret)

        // configure the recipient of push notifications
        initNotificationReceiver()

        // read user defaults (email, userId, token)
        readAndInitUserDefaults()

        // configure the background processing
        initBackgroundHandler(backgroundInterval)

        // configure the session
        configureSession()

        // send the user identification to the backend
        sendIdentifyInfo()
    }

    /**
     * Initializes the notification receiver programmatically
     */
    fun initNotificationReceiver() {
        if (application != null) {
            val filter = IntentFilter("com.google.android.c2dm.intent.RECEIVE")
            application!!.registerReceiver(breinPushNotificationReceiver, filter)
        }
    }

    /**
     * Stop sending notifications
     */
    fun destroyNotificationReceiver() {
        application?.unregisterReceiver(breinPushNotificationReceiver)
    }

    /**
     * Background Handler for sending messages
     *
     * @param backgroundInterval long interval in ms
     */
    fun initBackgroundHandler(backgroundInterval: Long) {
        Log.d(TAG, "initBackgroundHandler invoked with duration: $backgroundInterval")


        Timer().schedule(object : TimerTask() {
            override fun run() {
                Log.d("initBackgroundHandler", "Called on main thread")

                // flag if sending is possible
                sendLocationInfo()
            }
        }, backgroundInterval)
    }

    /**
     * Called in case of API Shutdown and will stop all services
     */
    @Suppress("UNUSED")
    fun shutdown() {
        Log.d(TAG, "shutdown invoked ")
        destroyNotificationReceiver()
    }

    /**
     * initializes the session id with an identifier
     */
    fun configureSession() {
        this.sessionId = UUID.randomUUID().toString()
    }

    /**
     * send an activity
     *
     * @param activityType      String contains the activity type
     * @param additionalContent Map can contain additional data
     */
    fun sendActivity(
        activityType: String?,
        additionalContent: MutableMap<String, Any?>?
    ) {
        Log.d(TAG, "sending activity of type: $activityType")
        if (BreinUtil.containsValue(this.userEmail)) {
            Breinify.getUser().setEmail(this.userEmail!!)
        }
        if (BreinUtil.containsValue(this.userId)) {
            Breinify.getUser().setUserId(this.userId!!)
        }
        if (BreinUtil.containsValue(additionalContent)) {
            if (additionalContent != null) {
                Breinify.getUser().setAdditional("notification", additionalContent)
            }
        }
        if (activityType != null) {
            Breinify.activity(activityType)
        }
    }

    /**
     * send an identify information ony if token is given
     */
    fun sendIdentifyInfo() {
        Log.d(TAG, "sendIdentifyInfo invoked")
        if (BreinUtil.containsValue(pushDeviceRegistration)) {
            sendActivity("identify", null)
        }
    }

    /**
     * send a location information
     */
    fun sendLocationInfo() {
        Log.d(TAG, "sendLocationInfo invoked")
        if (this.application == null) {
            Log.d(
                TAG,
                "sendLocationInfo. Can not check permissions because application object not set"
            )
            return
        }
        if (!BreinUtil.containsValue(pushDeviceRegistration)) {
            Log.d(TAG, "sendLocationInfo. No deviceRegistrationToken set.")
            return
        }
        val accessFineLocationPermission: Int = ActivityCompat.checkSelfPermission(
            this.application!!,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val accessCoarseLocationPermission: Int = ActivityCompat.checkSelfPermission(
            this.application!!,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (accessCoarseLocationPermission != PackageManager.PERMISSION_GRANTED ||
            accessFineLocationPermission != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "no permission granted to invoke location requests")
            return
        }
        sendActivity("sendLoc", null)
    }

    /**
     * read the user defaults and initializes the brein email and userId properties
     */
    fun readAndInitUserDefaults() {
        Log.d(TAG, "readAndInitUserDefaults invoked")
        if (this.application == null) {
            Log.d(TAG, "readAndInitUserDefaults can not work, because application object not set.")
            return
        }
        val prefs: SharedPreferences = this.application!!.getSharedPreferences(
            BREIN_PREF_NAME,
            MODE_PRIVATE
        )
        val breinPushRegistration: String? = prefs.getString(BREIN_PUSH_DEVICE_REGISTRATION, null)
        val breinEmail: String? = prefs.getString(BREIN_USER_EMAIL, null)

        // generate UUID if not already generated
        val breinUserId: String? = prefs.getString(BREIN_USER_ID, UUID.randomUUID().toString())
        if (BreinUtil.containsValue(breinUserId)) {
            Breinify.userId = breinUserId
        }
        if (BreinUtil.containsValue(breinEmail)) {
            Breinify.email = breinEmail
        }
        if (BreinUtil.containsValue(breinPushRegistration)) {
            Breinify.pushDeviceRegistration = breinPushRegistration
        }
    }

    /**
     * Save email and userId in user defaults and send
     * an identifyInfo to the backend.
     */
    @Suppress("UNUSED")
    fun saveUserDefaults() {
        Log.d(TAG, "saveUserDefaults invoked")
        saveUserDefaultValue(BREIN_USER_EMAIL, userEmail)
        saveUserDefaultValue(BREIN_USER_ID, userId)

        // in case of changed user-identification
        sendIdentifyInfo()
    }

    /**
     * Save the device token
     *
     * @param deviceToken String contains pushDeviceRegistration
     */
    @Suppress("UNUSED")
    fun configureDeviceToken(deviceToken: String?) {
        Log.d(TAG, "configureDeviceToken invoked wit token: $deviceToken")
        if (deviceToken != null) {
            setDeviceRegistration(deviceToken)
        }

        // save device registration
        saveUserDefaultValue(BREIN_PUSH_DEVICE_REGISTRATION, deviceToken)

        // send Identify
        sendIdentifyInfo()
    }

    /**
     * Helper to save key - value pair as user defaults
     *
     * @param key   String contains the key
     * @param value String contains the value
     */
    fun saveUserDefaultValue(key: String?, value: String?) {
        if (this.application != null) {
            val editor: SharedPreferences.Editor = this.application!!.getSharedPreferences(
                BREIN_PREF_NAME,
                MODE_PRIVATE
            ).edit()
            if (key != null && value != null) {
                editor.putString(key, value)
                editor.apply()
            }
        }
    }

    /**
     * Invoked when app is set to foreground, set saved sessionId
     */
    @Suppress("UNUSED")
    fun appIsInForeground() {
        if (this.sessionId != null) {
            Breinify.getUser().setSessionId(this.sessionId!!)
        }
    }

    /**
     * Invoked when app is in set to background, remove sessionId
     */
    @Suppress("UNUSED")
    fun appIsInBackground() {
        Breinify.getUser().setSessionId("")
    }

    // some handy constants
    const val TAG = "BreinifyManager"
    const val BREIN_PREF_NAME = "breinify.pref"
    const val BREIN_PUSH_DEVICE_REGISTRATION = "breinPushDeviceRegistration"
    const val BREIN_USER_EMAIL = "breinUserEmail"
    const val BREIN_USER_ID = "breinUserId"

    init {
    }
}