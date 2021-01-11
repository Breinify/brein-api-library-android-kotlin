package com.brein.api

import com.brein.domain.*
import com.brein.domain.results.BreinTemporalDataResult
import com.brein.domain.results.temporaldataparts.BreinLocationResult
import com.brein.engine.BreinEngineType
import org.junit.*

/**
 * Test of Breinify Java API (static option)
 */

class TestApi {
    /**
     * Contains the Breinify User
     */
    private val breinUser = BreinUser("User.Name@email.com")

    /**
     * Contains the Category
     */
    private val breinCategoryType = BreinCategoryType.HOME

    /**
     * Contains the BreinActivityType
     */
    private val breinActivityType = BreinActivityType.LOGIN

    /**
     * Correct configuration
     */
    val breinConfig = BreinConfig(VALID_SECRET_API_KEY, VALID_SECRET)

    /**
     * Catches the result from the rest call
     */
    internal inner class RestResult : ICallback<BreinResult?> {
        override fun callback(data: BreinResult?) {
            // Assert.assertTrue(data != null)
            println("within RestResult")
            println("Data is: $data")
            for ((key, value) in data?.map!!) {
                println("entry: $key - value: $value")
//                val temporalDataResult = BreinTemporalDataResult(data)

//                val hasWeather: Boolean = temporalDataResult.hasWeather()
//                val hasEvents: Boolean = temporalDataResult.hasEvents()
//                val hasLocalDateTime: Boolean = temporalDataResult.hasLocalDateTime()
//                val hasEpochDateTime: Boolean = temporalDataResult.hasEpochDateTime()
//                val hasHolidays: Boolean = temporalDataResult.hasHolidays()
            }
        }
    }

    internal inner class RestFailResult : ICallback<BreinResult?> {
        override fun callback(data: BreinResult?) {
            Assert.assertTrue(data == null)
        }
    }

    private val restCallback: ICallback<BreinResult?> = RestResult()
    private val restFailCallback: ICallback<BreinResult?> = RestFailResult()

    @After
    fun waitSomeSeconds() {
        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    /**
     * testcase how to use the activity api
     */
    @Test
    fun testLogin() {
        Breinify.setConfig(breinConfig)

        breinUser.setFirstName("User")
            .setLastName("Name")

        Breinify.activity(
            breinUser,
            breinActivityType,
            breinCategoryType,
            "Login-Description",
            restCallback
        )
    }

    /**
     * testcase without category type
     */
    @Test
    fun testWithoutCategoryType() {
        Breinify.setConfig(breinConfig)
        breinUser.setFirstName("User")
            .setLastName("Name")

        Breinify.activity(
            breinUser,
            breinActivityType,
            null,
            "Login-Description",
            restCallback
        )
    }

    /**
     * Testcase with null value as apikey
     */
    @Test
    fun testLoginWithNullApiKey() {
        val description = "Login-Description"
        val config = BreinConfig(null, VALID_SECRET)
        Breinify.setConfig(config)

        breinUser.setFirstName("User")
            .setLastName("Name")

        Breinify.activity(
            breinUser,
            breinActivityType,
            breinCategoryType,
            description,
            restFailCallback
        )
    }

    @Test
    fun testWithoutCallback() {
        Breinify.activity(
            breinUser,
            BreinActivityType.LOGIN,
            BreinCategoryType.HOME,
            "Login-Description", null
        )
    }

    /**
     * Testcase with null value as base url
     */
    @Test
    fun testWithoutSecret() {
        val config = BreinConfig(VALID_API_KEY, null)
        Breinify.setConfig(config)
    }

    /**
     * Testcase with null rest engine. This will throw an
     * exception.
     */
    @Test
    fun testLoginWithDefaultRestEngine() {
        val description = "Login-Description"

        var config: BreinConfig? = null
        try {
            config =
                BreinConfig(VALID_API_KEY, VALID_SECRET, BreinEngineType.NO_ENGINE)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Breinify.setConfig(config)
        breinUser.setFirstName("User")
            .setLastName("Name")

        Breinify.activity(
            breinUser,
            breinActivityType,
            breinCategoryType,
            description,
            restCallback
        )
    }

    /**
     * Test case with wrong endpoint configuration
     */
    @Test
    fun testWithWrongEndPoint() {
        val description = "Login-Description"
//        val config = BreinConfig(VALID_SECRET_API_KEY, VALID_SECRET)
        val config = BreinConfig(VALID_API_KEY, VALID_SECRET)

        config.activityEndpoint = "/wrongEndPoint"
        Breinify.setConfig(config)

        breinUser.setFirstName("User")
        breinUser.setLastName("Name")

        Breinify.activity(
            breinUser,
            breinActivityType,
            breinCategoryType,
            description,
            restCallback
        )
    }

    /**
     * Invoke a test call with 20 logins
     */
    @Test
    fun testWith20Logins() {
        val maxLogin = 20
        for (index in 0 until maxLogin) {
            testLogin()
        }
    }

    /**
     * test case how to invoke logout activity
     */
    @Test
    fun testLogout() {
        val description = "Logout-Description"
        Breinify.setConfig(breinConfig)
        breinUser.setDateOfBirth("12")
        Breinify.activity(
            breinUser,
            breinActivityType,
            breinCategoryType,
            description,
            restCallback
        )
    }

    /**
     * test case how to invoke search activity
     */
    @Test
    fun testSearch() {
        val description = "Search-Description"
        Breinify.setConfig(breinConfig)
        Breinify.activity(
            breinUser,
            BreinActivityType.SEARCH,
            breinCategoryType,
            description,
            restCallback
        )
    }

    /**
     * test case how to invoke addToCart activity
     */
    @Test
    fun testAddToCart() {
        val description = "AddToCart-Description"
        Breinify.setConfig(breinConfig)
        Breinify.activity(
            breinUser,
            BreinActivityType.ADD_TO_CART,
            breinCategoryType,
            description,
            restCallback
        )
    }

    /**
     * test case how to invoke removeFromCart activity
     */
    @Test
    fun testRemoveFromCart() {
        val description = "RemoveFromCart-Description"
        Breinify.setConfig(breinConfig)
        Breinify.activity(
            breinUser,
            BreinActivityType.REMOVE_FROM_CART,
            breinCategoryType,
            description,
            restCallback
        )
    }

    /**
     * test case how to invoke selectProduct activity
     */
    @Test
    fun testSelectProduct() {
        val description = "Select-Product-Description"
        Breinify.setConfig(breinConfig)

        Breinify.activity(
            breinUser,
            BreinActivityType.SELECT_PRODUCT,
            breinCategoryType,
            description,
            restCallback
        )
    }

    /**
     * test case how to invoke other activity
     */
    @Test
    fun testOther() {
        val description = "Other-Description"
        Breinify.setConfig(breinConfig)
        Breinify.activity(
            breinUser,
            BreinActivityType.OTHER,
            breinCategoryType,
            description,
            restCallback
        )
    }

    /**
     * test case how to invoke it with flebile values
     */
    @Test
    fun testFlexibleValues() {
        val description = "Other-Description"
        Breinify.setConfig(breinConfig)
        Breinify.activity(
            breinUser,
            BreinActivityType.OTHER,
            breinCategoryType,
            description,
            restCallback
        )
    }

    /**
     * test case containing additional information
     */
    @Test
    fun testPageVisit() {

        // set configuration
//        val breinConfig = BreinConfig(VALID_SECRET_API_KEY, VALID_SECRET)
        val breinConfig = BreinConfig(VALID_API_KEY, VALID_SECRET)
        Breinify.setConfig(breinConfig)

        // user data
        val breinUser = BreinUser("FirstName.LastName@email.com")

        breinUser.setLastName("LastName")
            .setFirstName("FirstName")
            .setDateOfBirthValue(11, 20, 1999)
            .setDeviceId("DD-EEEEE")
            .setImei("55544455333")
            .setSessionId("r3V2kDAvFFL_-RBhuc_-Dg")
            .setUrl("https://sample.com.au/home")
            .setReferrer("https://sample.com.au/track")
            .setIpAddress("10.11.12.130")
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2486.0 Safari/537.36 Edge/13.10586")


        val breinActivity: BreinActivity = Breinify.getBreinActivity()

        // just in case you want to set the unixTimestamp
        breinActivity.setUnixTimestamp(System.currentTimeMillis() / 1000L)
        breinActivity.setUser(breinUser)
        breinActivity.setCategory(BreinCategoryType.APPAREL)
        breinActivity.setActivityType(BreinActivityType.PAGE_VISIT)
        breinActivity.setDescription("your description")
        breinActivity.setToTagMap("t1", 0.0)
        breinActivity.setToTagMap("t2", 5)
        breinActivity.setToTagMap("t3", "0.0")
        breinActivity.setToTagMap("t4", 5.0000)
        breinActivity.setToTagMap("nr", 3000)
        breinActivity.setToTagMap("sortid", "1.0")

        Breinify.activity(breinActivity, restCallback)
    }

    /**
     * test case without having set the BreinUser.
     * This will lead to an Exception.
     */
    //@Test(expected= BreinException.class)
    fun testPageVisitWithException() {

        // set configuration
//        val breinConfig = BreinConfig(VALID_SECRET_API_KEY)
        val breinConfig = BreinConfig(VALID_API_KEY)
        Breinify.setConfig(breinConfig)

        // user data
        val breinUser = BreinUser("User.Name@email.com")

        breinUser.setLastName("Name")
            .setDateOfBirthValue(11, 20, 1999)
            .setDeviceId("DD-EEEEE")
            .setImei("55544455333")
            .setSessionId("r3V2kDAvFFL_-RBhuc_-Dg")
            .setUrl("https://sample.com.au/home")
            .setReferrer("https://sample.com.au/track")
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2486.0 Safari/537.36 Edge/13.10586")
        Breinify.activity(
            breinUser,
            BreinActivityType.PAGE_VISIT,
            BreinCategoryType.APPAREL,
            "Description",
            restCallback
        )
    }

    /**
     * simply demonstrate the configuration of the engine
     */
    @Test
    fun testConfiguration() {
        val breinEngine = breinConfig.getBreinEngine()
        breinConfig.setConnectionTimeout(10000)
        breinConfig.setSocketTimeout(10000)
        breinEngine!!.configure(breinConfig)
    }

    /**
     * Test a login activity with sign and correct secret
     */
    @Test
    fun testLoginWithSign() {
//        val breinConfig = BreinConfig(VALID_SECRET_API_KEY, null)
        val breinConfig = BreinConfig(VALID_API_KEY, null)
        breinConfig.setSecret(VALID_SECRET)
        Breinify.setConfig(breinConfig)

        // invoke activity call
        Breinify.activity(
            breinUser,
            "login",
            "home",
            "Login-Description",
            restCallback
        )
    }

    /**
     * Test a login activity with sign but wrong secret
     */
    @Test
    fun testLoginWithSignButWrongSecret() {
        val wrongSecret = "ThisIsAWrongSecret"
        val breinConfig = BreinConfig(VALID_SECRET_API_KEY, wrongSecret)
        Breinify.setConfig(breinConfig)
        Breinify.activity(
            breinUser,
            BreinActivityType.LOGIN,
            BreinCategoryType.HOME,
            "Login-Description",
            restCallback
        )
    }

    /**
     * test case where an activity is sent without having set
     * the category type for this particular activity object.
     * In this case the default category type has to be used.
     * If this is not set then the call needs to be rejected.
     */
    @Test
    fun testActivityWithoutCategory() {
//        val breinConfig = BreinConfig(VALID_SECRET_API_KEY, VALID_SECRET)
        val breinConfig = BreinConfig(VALID_API_KEY, VALID_SECRET)
            .setDefaultCategory("DEF-CAT-TYPE")
        Breinify.setConfig(breinConfig)

        val breinUser = BreinUser("email.mail")

        Breinify.activity(breinUser, "ACT-TYPE", "CAT-TYPE", "DESC", restCallback)
        Breinify.activity(breinUser, "ACT-TYPE", "", "DESC", restCallback)
        Breinify.activity(breinUser, "ACT-TYPE", null, "DESC", restCallback)
        Breinify.activity(breinUser, "ACT-TYPE", "bla", null, restCallback)
        Breinify.activity(breinUser, "ACT-TYPE", "bla", "Desc", null)
    }

    @Test
    fun testTemporalData() {
//        val breinConfig = BreinConfig(VALID_SECRET_API_KEY, VALID_SECRET)
        val breinConfig = BreinConfig(VALID_API_KEY, VALID_SECRET)
        Breinify.setConfig(breinConfig)
        val breinUser = BreinUser("fred.firestone@email.com")
            .setIpAddress("74.115.209.58")
            .setTimezone("America/Los_Angeles")
            .setLocalDateTime("Sun Jul 2 2017 18:15:48 GMT-0800 (PST)")

        val breinTemporalData = BreinTemporalData()
        breinTemporalData.setUser(breinUser)

        breinTemporalData.setLocation("san francisco")
            .setUser(breinUser)
        breinTemporalData.execute(restCallback)
    }

    @Test
    fun testRecommendation() {
//        val breinConfig = BreinConfig(VALID_SECRET_API_KEY, VALID_SECRET)
        val breinConfig = BreinConfig(VALID_API_KEY, VALID_SECRET)
        Breinify.setConfig(breinConfig)

        val breinUser = BreinUser("tester.breinify@email.com")
        breinUser.setSessionId("1133AADDDEEE")

        val numberOfRecommendations = 3
        val recommendation = BreinRecommendation()
        recommendation.setUser(breinUser)
        recommendation.setNumberOfRecommendations(numberOfRecommendations)
        Breinify.recommendation(recommendation, restCallback)
    }

    @Test
    fun testForDocSendingReadArticle() {
        val breinConfig = BreinConfig(VALID_API_KEY, VALID_SECRET)
        Breinify.setConfig(breinConfig)

        val breinUser = BreinUser()
        val breinActivity = BreinActivity()

        breinActivity.setUser(breinUser)
        breinActivity.setActivityType("readArticle")
        breinActivity.setDescription("A Homebody Persident Sits Out His Honeymoon Period")

        Breinify.sendActivity(breinActivity)
    }

    @Test
    fun testForDocTemporalDataUserInfo() {
//        val breinConfig = BreinConfig(VALID_SECRET_API_KEY, VALID_SECRET)
        val breinConfig = BreinConfig(VALID_API_KEY, VALID_SECRET)
        Breinify.setConfig(breinConfig)
        val breinTemporalData = BreinTemporalData()
            .setLocation("san francisco")
        breinTemporalData.execute(object : ICallback<BreinResult?> {
            override fun callback(data: BreinResult?) {
                val temporalDataResult = BreinTemporalDataResult(data!!)

//                // todo check
//                if (temporalDataResult.hasWeather()) {
//                    val weatherResult: BreinWeatherResult = temporalDataResult.getWeather()
//                }
//                if (temporalDataResult.hasEvents()) {
//                    val eventResults: List<BreinEventResult> = temporalDataResult.getEvents()
//                }
//                if (temporalDataResult.hasLocalDateTime()) {
//                    println("")
//                }
//                if (temporalDataResult.hasEpochDateTime()) {
//                    println("")
//                }
//                if (temporalDataResult.hasHolidays()) {
//                    val holidayResults: List<BreinHolidayResult> = temporalDataResult.getHolidays()
//                }
            }
        })
    }

    @Test
    fun testForDocTemporalDataGeocoding() {
        val breinConfig = BreinConfig(VALID_API_KEY, VALID_SECRET)
        Breinify.setConfig(breinConfig)

        val breinTemporalData = BreinTemporalData()
            .setLocation("The Big Apple")

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
    }

    @Test
    fun testForDocTemporalDataReverseGeoCoding() {
        val breinConfig = BreinConfig(VALID_API_KEY, VALID_SECRET)
        Breinify.setConfig(breinConfig)

        val breinTemporalData = BreinTemporalData()
            .setLatitude(37.7609295)
            .setLongitude(-122.4194155)
            .setShapeTypes("CITY", "NEIGHBORHOOD")

        breinTemporalData?.execute(object : ICallback<BreinResult?> {
            override fun callback(data: BreinResult?) {
                val temporalDataResult = BreinTemporalDataResult(data!!)

                // access the geoJson instances for the CITY and the NEIGHBORHOOD
//                temporalDataResult.getLocation().getGeoJson("CITY")
//                temporalDataResult.getLocation().getGeoJson("NEIGHBORHOOD")
            }
        })
    }

    companion object {
        /**
         * This has to be a valid api key
         */
        private const val VALID_API_KEY = "41B2-F48C-156A-409A-B465-317F-A0B4-E0E8"
        private const val VALID_SECRET_API_KEY = "CA8A-8D28-3408-45A8-8E20-8474-06C0-8548"
        private const val VALID_SECRET = "lmcoj4k27hbbszzyiqamhg=="

        /**
         * Housekeeping...
         */
        @AfterClass
        fun tearDown() {
            try {
                Thread.sleep(1000)
                Breinify.shutdown()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }
}