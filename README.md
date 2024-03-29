<p align="center">
  <img src="https://www.breinify.com/img/Breinify_logo.png" alt="Breinify: Leading Temporal AI Engine" width="250">
</p>


# Breinify's API Library


<sup>Features: **Temporal Data**, **(Reverse) Geocoding**, **Events**, **Weather**, **Holidays**, **Analytics**</sup>


This library utilizes [Breinify's API](https://www.breinify.com) to provide tasks like `PushNotifications`, `geocoding`, `reverse geocoding`, `weather and events look up`, `holidays determination` through the API's endpoints, i.e., `/activity` and `/temporaldata`. Each endpoint provides different features, which are explained in the following paragraphs. In addition, this documentation gives detailed examples for each of the features available for the different endpoints.

**PushNotifications**: 
The goal of utilizing Breinify’s Time-Driven push notifications is to send highly dynamic & individualized engagements to single app-users (customer) rather than the everyone in a traditional segments. These push notifications are triggered due to user behavior and a combination of hyper-relevant weather, events, and holidays. 

**Activity Endpoint**: The endpoint is used to understand the usage-patterns and the behavior of a user using, e.g., an application, a mobile app, or a web-browser. The endpoint offers analytics and insights through Breinify's dashboard.

**TemporalData Endpoint**: The endpoint offers features to resolve temporal information like a timestamp, a location (latitude and longitude or free-text), or an IP-address, to temporal information (e.g., timezone, epoch, formatted dates, day-name),  holidays at the specified time and location, city, zip-code, neighborhood, country, or county of the location, events at the specified time and location (e.g., description, size, type), weather at the specified time and location (e.g., description, temperature).


## Getting Started

### Retrieving an API-Key

First of all, you need a valid API-key, which you can get for free at [https://www.breinify.com](https://www.breinify.com). In the examples, we assume you have the following api-key:

**938D-3120-64DD-413F-BB55-6573-90CE-473A**


It is recommended to use signed messages when utilizing the Android library. A signed messages ensures, that the request is authorized. To activate signed message ensure that Verification Signature is enabled for your key (see Breinify's API Docs for further information). In this documentation we assume that the following secret is attached to the API key and used to sign a message.

**utakxp7sm6weo5gvk7cytw==**

### Targets

- min Android Version: 15
- default target: 26

### Installation

#### Including the Library

The library is available through maven and jcenter repository and can be easily added within the
gradle configuration within the *app/build.gradle* like this:

```gradle
dependencies {
    ...
    implementation 'com.breinify:brein-api-library-android-kotlin:1.0.17'
    ...
}
```



#### Consideration of  Firebase Libraries

The following Firebase Libraries needs to be included in the within the *app/build.gradle* file:

```gradle
dependencies {
    ...
    // Import the Firebase BoM 
    implementation platform('com.google.firebase:firebase-bom:26.1.0')
    
    // Firebase Cloud Messaging (Kotlin)
    implementation 'com.google.firebase:firebase-messaging-ktx'
    ...
}
```



#### Permissions

The Breinify SDK needs some permission in order to retrieve the appropriate information. In your `AndroidManifest.xml` file you have to add the following permissions:

```xml
<!-- This permission is required to allow the application to send requests to Breinify -->
<uses-permission android:name="android.permission.INTERNET" />

```



### Configuring the Library

Whenever the library is used, it needs to be configured, i.e., the configuration defines API key and secret 
(if signed messages are enabled, i.e., `Verification Signature` is checked) to be used. 

Do this on the *Main Activity*.

##### Initialize the Library and Firebase Connection

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ...
  
        initBreinify()
        initToken()
    }

func initBreinify() {
   val kValidApiKey = "938D-3120-64DD-413F-BB55-6573-90CE-473A"
   val kValidSecret = "**utakxp7sm6weo5gvk7cytw==**"

   Breinify.initialize(this.application, this, kValidApiKey, kValidSecret)
} 

func initToken() {
  Firebase.messaging.getToken().addOnCompleteListener(OnCompleteListener { task ->
     if (!task.isSuccessful) {
         Log.w(TAG, "Fetching FCM registration token failed", task.exception)
         return@OnCompleteListener
     }
     // Get new FCM registration token
     val token = task.result
     
     // Provide some User Information (optional)
     val userInfoMap = HashMap<String, String>()
     userInfoMap["firstName"] = "Elvis"
     userInfoMap["lastName"] = "Presley"
     userInfoMap["phone"] = "0123456789"
     userInfoMap["email"] = "elvis.presly@mail.com"
     
     // configure token                                                                           
     Breinify.initWithDeviceToken(token, userInfoMap)
}
```

The Breinify API is now configured with a valid configuration object and valid device token.

### Clean-Up after Usage

Whenever the library is not used anymore, it is recommended to clean-up and release the resources held. To do so, the `Breinify.shutdown()`
method is used. A typical framework may look like that:

```kotlin
override fun onDestroy() {
    super.onDestroy()
    Breinify.shutdown()
}
```



## Activity: Selected Usage Examples

The `/activity` endpoint is used to track the usage of, e.g., an application, an app, or a web-site. There are several libraries available to be used for different system (e.g.,  [Node.js](https://github.com/Breinify/brein-api-library-node), [iOS](https://github.com/Breinify/brein-api-library-ios), [Java](https://github.com/Breinify/brein-api-library-java), [JavaScript](https://github.com/Breinify/brein-api-library-javascript-browser), [Ruby](https://github.com/Breinify/brein-api-library-ruby), [PHP](https://github.com/Breinify/brein-api-library-php), [Python](https://github.com/Breinify/brein-api-library-python)).

### Sending an Activity 

The example shows, how to send a login activity, reading the data from an request. In general, activities are added to the interesting measure points within your applications process (e.g., `login`, `addToCart`, `readArticle`). The endpoint offers analytics and insights through Breinify's dashboard.

```kotlin
// get User and set additional user data
val appUser = Breinify.getUser()
      .setEmail("elvis.presley@gmail.com")
      .setFirstName("Elvis")
      .setLastName("Presley")
               
// invoke an activity that the user has logged in
val breinActivity = Breinify.getActivity()
      .setCategory(BreinCategoryType.HOME)
      .setActivityType(BreinActivityType.LOGIN)
        
// send the activity
Breinify.sendActivity(breinActivity)
```



## TemporalData: Selected Usage Examples

The `/temporalData` endpoint is used to transform your temporal data into temporal information, i.e., enrich your temporal data with information like *current weather*, *upcoming holidays*, *regional and global events*, and *time-zones*, as well as geocoding and reverse geocoding.

### Getting User Information

Sometimes it is necessary to get some more information about the user of an application, e.g., to increase usability and enhance the user experience, to handle time-dependent data correctly, to add geo-based services, or increase quality of service. The client's information can be retrieved easily 
by calling the `/temporaldata` endpoint utilizing the `Breinify.temporalData(...)` method or by executing a `BreinTemporalData` instance, i.e.,:

```kotlin
breinTemporalData.execute(object : ICallback<BreinResult?> {
            override fun callback(data: BreinResult?) {
                val locationResult = BreinLocationResult(data?.map)
                val lat = locationResult.lat
                val lon = locationResult.lon
                val country = locationResult.country
                val state = locationResult.state
                val city = locationResult.city
                val granu = locationResult.granularity
            }
        })
```

The returned result contains detailed information about the time, the location, the weather, holidays, and events at the time and the location. A detailed example of the returned values can be found <a target="_blank" href="https://www.breinify.com/documentation">here</a>.

<p align="center">
  <img src="https://raw.githubusercontent.com/Breinify/brein-api-library-java/master/documentation/img/sample-user-information.png" alt="Sample output of the user information." width="500"><br/>
  <sup>Possilbe sample output utilizing some commanly used features.</sup>
</p>


### Geocoding (resolve Free-Text to Locations)

Sometimes it is necessary to resolve a textual representation to a specific geo-location. The textual representation can be structured and even partly unstructured, e.g., the textual representation `the Big Apple` is considered to be unstructured, whereby a structured location would be, e.g., `{ city: 'Seattle', state: 'Washington', country: 'USA' }`. 

It is also possible to pass in partial information and let the system try to resolve/complete the location, e.g., `{ city: 'New York', country: 'USA' }`.

```kotlin
val breinTemporalData = BreinTemporalData()
            .setLocation("The Big Apple")
        
        breinTemporalData.execute(object : ICallback<BreinResult?> {
            override fun callback(data: BreinResult?) {
                val locationResult = BreinLocationResult(data?.map)
                println("Latitude is: " + locationResult.lat)
                println("Longitude is: " + locationResult.lon)
                println("Country is: " + locationResult.country)
                println("State is: " + locationResult.state)
                println("City is: " + locationResult.city)
            }
        })
```

This will lead to the following result:

```
Latitude is: 40.7614927583
Longitude is: -73.9814311179
Country is: US
State is: NY
City is: New York
```

Or shown as an Apple Map result:

<p align="center">
  <img src="https://raw.githubusercontent.com/Breinify/brein-api-library-android/master/documentation/img/geomap.png" alt="Sample Map of the results from the geocoding requests." width="400"><br/>
  <sup>Map output by utilizing the result of reverse geocoding requests.</sup>
</p>

### Reverse Geocoding (retrieve GeoJsons for, e.g., Cities, Neighborhoods, or Zip-Codes)

The library also offers the feature of reverse geocoding. Having a specific geo-location and resolving the coordinates to a specific city or neighborhood (i.e., names of neighborhood, city, state, country, and optionally GeoJson shapes). 

A possible request if you're interesed in events might look like this:

```kotlin
val breinTemporalData = BreinTemporalData()
            .setLatitude(37.7609295)
            .setLongitude(-122.4194155)
            .setShapeTypes("CITY", "NEIGHBORHOOD")
        
        breinTemporalData?.execute(object : ICallback<BreinResult?> {
            override fun callback(data: BreinResult?) {
                val temporalDataResult = BreinTemporalDataResult(data!!)

                // access the geoJson instances for the CITY and the NEIGHBORHOOD
                temporalDataResult.getLocation().getGeoJson("CITY")
                temporalDataResult.getLocation().getGeoJson("NEIGHBORHOOD")
            }
        })
```

## PushNotifications: Selected Usage Example


Let's integrate Breinify's PushNotifications within an Android App using [Firebase Cloud Messaging](https://firebase.google.com/docs/cloud-messaging/). 

### 1. Initialization


Using Breinify Push Notifications in Android apps is straightforward. 

The Breinify SDK integrates smoothly within the Android Application Lifecycle. Add in your MainActivity the initialization of the Breinify SDK the initialization and device token functionality.

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
     super.onCreate(savedInstanceState)
     
     ...
     initBreinify()
     initToken()
}

private fun initBreinify() {
     val kValidApiKey = "938D-3120-64DD-413F-BB55-6573-90CE-473A"
     val kValidSecret = "**utakxp7sm6weo5gvk7cytw==**"

     Breinify.initialize(this.application, this, kValidApiKey, kValidSecret)
  
  	 // add this for handling with pushNotifications 
     val channelId = R.string.default_notification_channel_id
     val channelName = R.string.default_notification_channel_name
     Breinify.configureNotificationDefaultChannel(channelId, channelName)
}

private fun initToken() {
     Firebase.messaging.getToken().addOnCompleteListener(OnCompleteListener { task ->
     if (!task.isSuccessful) {
         Log.w(TAG, "Fetching FCM registration token failed", task.exception)
         return@OnCompleteListener
     }

     // Get new FCM registration token
     val token = task.result
                                                                 
     // Possible to provide already some user info                                                                                                                                     
     val userInfoMap = HashMap<String, String>()
     userInfoMap["firstName"] = "Elvis"
     userInfoMap["lastName"] = "Presley"
     userInfoMap["phone"] = "0123456789"
     userInfoMap["email"] = "elvis.presly@mail.com" // mandatory

     Breinify.initWithDeviceToken(token, userInfoMap)                                                                          
     })
 }
   
```



### 2. PushNotification Configuration

Enhance  `AndroidManifest.xml` like this:

```xml
<!-- This permission is required to allow the application to send requests to Breinify -->
<uses-permission android:name="android.permission.INTERNET" />

<application
   ...
   <!-- Configures the Default Notification Channel for Push Notifications -->  
   <meta-data     
       android:name="com.google.firebase.messaging.default_notification_channel_id"
       android:value="@string/default_notification_channel_id" />
       ...
    <!-- Configures the Service Class handling incoming remote messages (e.g. FirebaseMessagingService) -->  
    <service
        android:name=".FirebaseMessagingService"
        android:exported="false">
    <intent-filter>
        <action android:name="com.google.firebase.MESSAGING_EVENT" />
    </intent-filter>
</service>
    ...
</application>

```



Provide Default Channel Resource Names in File `strings.xml` like this:

```XML
<resources> 
...
<string name="default_notification_channel_id" 
              translatable="false">Your Default Channel</string>
<string name="default_notification_channel_name" 
              translatable="false">Your Default Channel Name</string>
   ...
</resources>

```



Handle the PushNotifications in your service class e.g. FirebaseMessagingService() like this:

```kotlin
class YourMessageService : FirebaseMessagingService() {

   override fun onMessageReceived(remoteMessage: RemoteMessage) {
     if (Breinify.isBreinifyPushNotificationMessage(remoteMessage)) {
         // handling Breinify related messages
         val kApiKey = "938D-3120-64DD-413F-BB55-6573-90CE-473A"
         val kSecret = "**utakxp7sm6weo5gvk7cytw==**"

         Breinify.initialize(this.application, null, kApiKey, kSecret)
         Breinify.onMessageReceived(applicationContext, remoteMessage)
     } else {
         super.onMessageReceived(remoteMessage)
     }
  }
  
  override fun onNewToken(token: String) {
     // send the new fcm token to the Breinify Engine
     Breinify.configureDeviceToken(token)
  }   
}

```





When sending a Push Notification it will appear like this:

<p align="center">
  <img src="https://raw.githubusercontent.com/Breinify/brein-api-library-android/master/documentation/img/android-push-sample.png" alt="Sample Map of the results from the geocoding requests." width="400"><br/>
  <sup>Map output by utilizing the result of reverse geocoding requests.</sup>
</p>


### Further links
To understand all the capabilities of Breinify's APIs, have a look at:

* [Breinify's Website](https://www.breinify.com).

