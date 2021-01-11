package com.brein.api

import com.brein.domain.BreinActivityType
import com.brein.domain.BreinCategoryType
import com.brein.domain.BreinConfig
import com.brein.domain.BreinUser
import com.brein.engine.BreinEngineType
import org.junit.*

/**
 * Test of Breinify API (static option)
 */
class TestHttpUrlConnectionApi {
    /**
     * Contains the Breinify User
     */
    private val breinUser = BreinUser("toni.tester@mail.net")

    /**
     * Contains the Category
     */
    private val breinCategoryType = "home"

    /**
     * Correct configuration
     */
    private val breinConfig = BreinConfig(VALID_API_KEY, VALID_SECRET)
    private val restCallback = RestCallback()

    /**
     * setup for each test
     */
    @Before
    fun setUp() {
    }

    @After
    fun waitSeconds() {
        try {
            Thread.sleep(2000)
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
        breinUser.setFirstName("Marco")
        breinUser.setLastName("Recchioni")

        Breinify.activity(
            breinUser,
            BreinActivityType.LOGIN,
            BreinCategoryType.HOME,
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
        breinUser.setFirstName("Marco")
        breinUser.setLastName("Recchioni")

        Breinify.activity(
            breinUser,
            BreinActivityType.LOGIN,
            breinCategoryType,
            description,
            restCallback
        )
    }

    /**
     * Testcase with null value as base url
     */
    @Test
    fun testLoginWithoutSecret() {
        val description = "Login-Description"
        val config = BreinConfig(VALID_API_KEY, null)

        Breinify.setConfig(config)
        breinUser.setFirstName("Marco")
        breinUser.setLastName("Recchioni")

        Breinify.activity(
            breinUser,
            BreinActivityType.LOGIN,
            breinCategoryType,
            description,
            restCallback
        )
    }

    /**
     * Testcase with null rest engine. This will throw an
     * exception.
     */
    // @Test(expected= BreinException.class)
    fun testLoginWithNoRestEngine() {
        val description = "Login-Description"
        var config: BreinConfig? = null

        try {
            config = BreinConfig(VALID_API_KEY, VALID_SECRET, BreinEngineType.NO_ENGINE)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        Breinify.setConfig(config)
        breinUser.setFirstName("Marco")
        breinUser.setLastName("Recchioni")

        Breinify.activity(
            breinUser,
            BreinActivityType.LOGIN,
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
        val config = BreinConfig(VALID_API_KEY, VALID_SECRET)

        config.activityEndpoint = "/wrongEndPoint"
        Breinify.setConfig(config)

        breinUser.setFirstName("Marco")
        breinUser.setLastName("Recchioni")
        Breinify.activity(
            breinUser,
            BreinActivityType.LOGIN,
            breinCategoryType,
            description, restCallback
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
            BreinActivityType.LOGOUT,
            breinCategoryType,
            description, restCallback
        )
    }

    /**
     * testcase
     */
    @Test
    fun testSearch() {
        val description = "Search-Description"
        Breinify.setConfig(breinConfig)
        Breinify.activity(
            breinUser,
            BreinActivityType.SEARCH,
            breinCategoryType,
            description, restCallback
        )
    }

    /**
     * testcase
     */
    @Test
    fun testAddToCart() {
        val description = "AddToCart-Description"
        Breinify.setConfig(breinConfig)
        Breinify.activity(
            breinUser,
            BreinActivityType.ADD_TO_CART,
            breinCategoryType,
            description, restCallback
        )
    }

    /**
     * testcase
     */
    @Test
    fun testRemoveFromCart() {
        val description = "RemoveFromCart-Description"
        Breinify.setConfig(breinConfig)

        Breinify.activity(
            breinUser,
            BreinActivityType.REMOVE_FROM_CART,
            breinCategoryType,
            description, restCallback
        )
    }

    /**
     * testcase
     */
    @Test
    fun testSelectProduct() {
        val description = "Select-Product-Description"
        Breinify.setConfig(breinConfig)

        Breinify.activity(
            breinUser,
            BreinActivityType.SELECT_PRODUCT,
            breinCategoryType,
            description, restCallback
        )
    }

    /**
     * testcase
     */
    @Test
    fun testOther() {
        val description = "Other-Description"
        Breinify.setConfig(breinConfig)

        Breinify.activity(
            breinUser,
            BreinActivityType.OTHER,
            breinCategoryType,
            description, restCallback
        )
    }

    /**
     * simply demonstrate the configuration of the engine
     */
    @Test
    fun testConfiguration() {
        val breinEngine = breinConfig.getBreinEngine()
        breinConfig.setConnectionTimeout(30000)
        breinConfig.setSocketTimeout(25000)
        breinEngine!!.configure(breinConfig)
    }

    companion object {
        /**
         * This has to be a valid api key
         */
        private const val VALID_API_KEY = "41B2-F48C-156A-409A-B465-317F-A0B4-E0E8"
        private const val VALID_SECRET_API_KEY = "CA8A-8D28-3408-45A8-8E20-8474-06C0-8548"
        private const val VALID_SECRET = "lmcoj4k27hbbszzyiqamhg=="

        /**
         * Init part
         */
        @BeforeClass
        fun init() {
        }

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