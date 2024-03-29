package com.brein.api


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import com.brein.domain.BreinActivityType
import com.brein.domain.BreinConfig
import com.brein.util.BreinUtil
import java.text.SimpleDateFormat
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

        // read user defaults (email, userId, token)
        readAndInitUserDefaults()

        // configure the background processing
        // initBackgroundHandler(backgroundInterval)

        // configure the session
        configureSession()
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
    fun sendIdentifyInfo() {
        Log.d(TAG, "Breinify sendIdentifyInfo invoked")

        if (BreinUtil.containsValue(pushDeviceRegistration)) {
            val appUser = Breinify.getBreinUser()
            appUser.setPushDeviceRegistration(this.pushDeviceRegistration)

            val breinActivity: BreinActivity = Breinify.getBreinActivity()

            val map = collectAdditionalTagInformation()
            if (!map.isEmpty()) {
                breinActivity.setTagsDic(map)
            }

            // sendActivity(BreinActivityType.IDENTIFY, null)

            breinActivity.setActivityType(BreinActivityType.IDENTIFY)
            Breinify.activity(breinActivity)
        }
    }

    private fun collectAdditionalTagInformation(): HashMap<String, Any> {

        try {
            val packageManager = BreinifyManager.mainActivity?.packageManager
            var appVersion = ""
            var appInstallDate = ""
            var appName = ""

            mainActivity?.let { val packageInfo = packageManager?.getPackageInfo(it.packageName, 0)

                // contains the version of the App
                appVersion = packageInfo?.versionName.toString()

                val applicationInfo = packageInfo?.applicationInfo
                val lastUpdateTime = packageInfo?.lastUpdateTime

                val timeZoneDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                appInstallDate = timeZoneDate.format(lastUpdateTime)

                // collect name of the app
                val labelRes = applicationInfo?.labelRes

                // contains the appName
                appName = labelRes?.let { it1 -> application?.applicationContext?.getString(it1) }.toString()

            }

            // collect version of Breinify SDK
            val breinifySdkVersion = BreinConfig.VERSION

            val tagsDic = mapOf(
                "appInstallation" to appInstallDate,
                "appVersion" to appVersion,
                "appName" to appName,
                "Breinify-SDK" to breinifySdkVersion
            ) as HashMap<String, Any>

            return tagsDic
        } catch (e: Exception) {
            return hashMapOf<String, Any>()
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

    /**
     * Configures the default notification channel
     *
     * @param channelId       Int Resource contains the channel Id
     * @param channelDescId   Int Resource contains the channel description Id
     */
    fun configureDefaultNotificationChannel(channelId: Int, channelDescId: Int) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val context = getApplication()?.applicationContext
            val manager = context?.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager

            manager.let {
                val channelIdString = context.getString(channelId)
                if (manager.getNotificationChannel(channelIdString) == null) {
                    val channel = NotificationChannel(
                        channelIdString,
                        context.getString(channelId),
                        NotificationManager.IMPORTANCE_HIGH
                    )
                    channel.description = context.getString(channelDescId)
                    manager.createNotificationChannel(channel)
                }
            }
        }
    }

    // some handy constants
    const val TAG = "BreinifyManager"
    const val BREIN_PREF_NAME = "breinify.pref"
    const val BREIN_PUSH_DEVICE_REGISTRATION = "breinPushDeviceRegistration"
    const val BREIN_USER_EMAIL = "breinUserEmail"
    const val BREIN_USER_ID = "breinUserId"

}