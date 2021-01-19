package com.brein.api


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.brein.domain.BreinIpInfo
import com.brein.util.BreinUtil
import java.util.*
import kotlin.collections.HashMap

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
//    private val breinPushNotificationReceiver = BreinPushNotificationReceiver()

    private var apiKey: String? = null

    private var secret: String? = null

    private var backgroundInterval: Long = 0

    /**
     * Set the deviceRegistration
     *
     * @param pushDeviceRegistration String
     */
    fun setDeviceRegistration(pushDeviceRegistration: String) {
        Log.d(TAG, "Breinify - pushDeviceRegistration is: $pushDeviceRegistration")
        this.pushDeviceRegistration = pushDeviceRegistration

        // set user as well -> necessary for correct request
        Breinify.getUser().setPushDeviceRegistration(pushDeviceRegistration)
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
        BreinIpInfo.detect()
    }

    /**
     * Initializes the Lifecycle support and Engine
     *
     * @param backgroundInterval long contains background interval
     */
    private fun initLifecycleAndEngine(backgroundInterval: Long) {
        // register the callbacks for lifecycle support - necessary to determine if app
        // is in background or foreground

        // deactivated
        // application?.registerActivityLifecycleCallbacks(BreinifyLifecycle())

        // configure the API
        Breinify.setConfig(apiKey, secret)

        // configure the recipient of push notifications
        initNotificationReceiver()

        // read user defaults (email, userId, token)
        readAndInitUserDefaults()

        // configure the background processing
        // initBackgroundHandler(backgroundInterval)

        // configure the session
        configureSession()
    }

    /**
     * Initializes the notification receiver programmatically
     */
    fun initNotificationReceiver() {
//        if (application != null) {
//            val filter = IntentFilter("com.google.android.c2dm.intent.RECEIVE")
//            application!!.registerReceiver(breinPushNotificationReceiver, filter)
//        }
    }

    /**
     * Stop sending notifications
     */
    fun destroyNotificationReceiver() {
//        application?.unregisterReceiver(breinPushNotificationReceiver)
    }

    /**
     * Background Handler for sending messages
     *
     * @param backgroundInterval long interval in ms
     */
    fun initBackgroundHandler(backgroundInterval: Long) {
        Log.d(TAG, "Breinify - initBackgroundHandler invoked with duration: $backgroundInterval")

        Timer().schedule(object : TimerTask() {
            override fun run() {
                Log.d("initBackgroundHandler", "Breinify - called on main thread")

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
        Log.d(TAG, "Breinify shutdown invoked ")
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
        additionalContent: HashMap<String, Any?>?
    ) {
        Log.d(TAG, "Breinify - sending activity of type: $activityType")
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
     * send an identify information only if token is given
     */
    fun sendIdentifyInfo(deviceToken: String?) {
        Log.d(TAG, "Breinify sendIdentifyInfo invoked")
        if (BreinUtil.containsValue(pushDeviceRegistration)) {
            val appUser = Breinify.getBreinUser()
            appUser.setPushDeviceRegistration(this.pushDeviceRegistration)
            val breinActivity = Breinify.getBreinActivity()

            val previousTagsDic = breinActivity.getTagsDic()

            val tagsDic = HashMap<String, Any>()
            tagsDic["identify"] = "identify"

            deviceToken?.let {
                tagsDic["deviceToken"] = deviceToken
                tagsDic["deviceKind"] = "Android"
            }

            breinActivity.setTagsDic(tagsDic)
            sendActivity("identify", null)

            breinActivity.setTagsDic(previousTagsDic)
        }
    }

    /**
     * send a location information
     */
    fun sendLocationInfo() {
        Log.d(TAG, "Breinify - sendLocationInfo invoked")
        if (this.application == null) {
            Log.d(
                TAG,
                "Breinify - sendLocationInfo - cannot check permissions because application object not set"
            )
            return
        }
        if (!BreinUtil.containsValue(pushDeviceRegistration)) {
            Log.d(TAG, "Breinfiy - sendLocationInfo. No deviceRegistrationToken set.")
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
        Log.d(TAG, "Breinify - readAndInitUserDefaults invoked")
        if (this.application == null) {
            Log.d(
                TAG,
                "Breinify - readAndInitUserDefaults can not work, because application object not set."
            )
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
        Log.d(TAG, "Breinify - saveUserDefaults invoked")
        saveUserDefaultValue(BREIN_USER_EMAIL, userEmail)
        saveUserDefaultValue(BREIN_USER_ID, userId)
    }

    /**
     * Save the device token
     *
     * @param deviceToken String contains pushDeviceRegistration
     */
    @Suppress("UNUSED")
    fun configureDeviceToken(deviceToken: String?) {
        Log.d(TAG, "Breinify - configureDeviceToken invoked wit token: $deviceToken")
        if (deviceToken != null) {
            setDeviceRegistration(deviceToken)
        }

        // save device registration
        saveUserDefaultValue(BREIN_PUSH_DEVICE_REGISTRATION, deviceToken)

        saveUserDefaults()

        // send Identify
        sendIdentifyInfo(deviceToken)
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