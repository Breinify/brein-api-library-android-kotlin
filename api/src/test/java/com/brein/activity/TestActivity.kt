package com.brein.activity

import com.brein.api.BreinActivity
import com.brein.api.Breinify
import com.brein.api.ICallback
import com.brein.domain.BreinActivityType
import com.brein.domain.BreinConfig
import com.brein.domain.BreinResult
import com.brein.domain.BreinUser
import junit.framework.Assert.assertTrue
import org.junit.*

/**
 * This test cases shows how to use the  activity
 */

class TestActivity {
    /**
     * Contains the Breinify User
     */
    private val breinUser = BreinUser("Toni.Maroni@breinify.com")

    /**
     * Contains the Category
     */
    private val breinCategory = "services"

    /**
     * The Activity itself
     */
    private val breinActivity = BreinActivity()

    internal inner class RestResult : ICallback<BreinResult?> {
        override fun callback(data: BreinResult?) {
            assertTrue(data != null)
            println("within RestResult")
            println("Data is: $data")
        }
    }

    private val restCallback: ICallback<BreinResult?> = RestResult()

    /**
     * Preparation of test case
     */
    @Before
    fun setUp() {
        val breinConfig = BreinConfig(VALID_API_KEY, VALID_SECRET)
        Breinify.setConfig(breinConfig)
    }

    /**
     *
     */
    @After
    fun wait4FiveSeconds() {
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
        breinUser.setFirstName("Toni")
        breinUser.setLastName("Maroni")

        breinActivity.setUser(breinUser)
        breinActivity.setActivityType(BreinActivityType.LOGIN)
        breinActivity.setCategory(breinCategory)
        breinActivity.setDescription("This is a good description")

        breinActivity.execute(restCallback)

        val name = breinUser.getFirstName()
        Assert.assertEquals(name, "Toni")

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
        breinUser.setDateOfBirth("12")
        breinActivity.activity(
            breinUser,
            BreinActivityType.LOGOUT,
            breinCategory, description, restCallback
        )

        val birthday = breinUser.getDateOfBirth()
        Assert.assertEquals(birthday, ("12"))
    }

    /**
     * test case how to invoke search activity
     */
    @Test
    fun testSearch() {
        val description = "Search-Description"
        breinActivity.activity(
            breinUser,
            BreinActivityType.SEARCH,
            breinCategory, description, restCallback
        )
    }

    /**
     * test case how to invoke add-to-cart activity
     */
    @Test
    fun testAddToCart() {
        val description = "AddToCart-Description"
        breinActivity.activity(
            breinUser,
            BreinActivityType.ADD_TO_CART,
            breinCategory, description, restCallback
        )
    }

    /**
     * test case how to invoke remove-from-cart activity
     */
    @Test
    fun testRemoveFromCart() {
        val description = "RemoveFromCart-Description"
        breinActivity.activity(
            breinUser,
            BreinActivityType.REMOVE_FROM_CART,
            breinCategory, description, restCallback
        )
    }

    /**
     * test case how to invoke select product
     */
    @Test
    fun testSelectProduct() {
        val description = "Select-Product-Description"
        breinActivity.activity(
            breinUser,
            BreinActivityType.SELECT_PRODUCT,
            breinCategory, description, restCallback
        )
    }

    /**
     * test case how to invoke other
     */
    @Test
    fun testOther() {
        val description = "Other-Description"
        breinActivity.activity(
            breinUser,
            BreinActivityType.PAGE_VISIT,
            breinCategory, description, restCallback
        )
    }

    companion object {
        /**
         * This has to be a valid api key & secret
         */
        private const val VALID_API_KEY = "41B2-F48C-156A-409A-B465-317F-A0B4-E0E8"
        private const val VALID_SIGNATURE_API_KEY = "CA8A-8D28-3408-45A8-8E20-8474-06C0-8548"
        private const val VALID_SECRET = "lmcoj4k27hbbszzyiqamhg=="

        @AfterClass
        fun tearDown() {
            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }
}