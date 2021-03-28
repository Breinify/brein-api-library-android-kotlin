package com.brein.domain

import android.util.Log
import com.brein.api.BreinActivity
import com.brein.api.Breinify
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

/**
 * Test classes for the domain objects
 */

class TestDomain {
    /**
     * creates a brein request object that will be used within the body
     * of the request
     */
    @Test
    fun testBreinRequest() {
        val breinConfig = BreinConfig(VALID_API_KEY, VALID_SECRET)

        val breinUser = BreinUser("toni.maroni@mail.com")
            .setFirstName("Toni")
            .setLastName("Maroni")

        val breinActivity = BreinActivity()

        Breinify.setConfig(breinConfig)
        breinActivity.setUser(breinUser)
        breinActivity.setActivityType(BreinActivityType.LOGIN)
        breinActivity.setDescription("Super-Description")
        breinActivity.setCategory(BreinCategoryType.HOME)

        val jsonOutput = breinActivity.prepareRequestData(breinConfig)
        Assert.assertTrue(jsonOutput.isNotEmpty())
    }

    /**
     * creates a brein request object that will be used within the body
     * of the request but with less data
     */
    @Test
    fun testBreinRequestWithLessData() {
        val breinConfig = BreinConfig(VALID_API_KEY, VALID_SECRET)
        val breinUser = BreinUser()
        val breinActivity = BreinActivity()

        Breinify.setConfig(breinConfig)

        breinActivity.setUser(breinUser)
        breinActivity.setActivityType(BreinActivityType.LOGIN)
        breinActivity.setDescription("Super-Description")
        breinActivity.setCategory(BreinCategoryType.FOOD)

        val jsonOutput = breinActivity.prepareRequestData(breinConfig)

        Assert.assertTrue(jsonOutput.isNotEmpty())
        Assert.assertEquals(BreinActivityType.LOGIN, breinActivity.getActivityType())
    }

    /**
     * Test the birthday settings
     */
    @Test
    fun testBirthday() {
        val breinUser = BreinUser()

        // set right values
        breinUser.setDateOfBirthValue(10, 2, 2020) // this is correct date
        Assert.assertFalse(breinUser.getDateOfBirth().isEmpty())

        // set wrong day
        breinUser.resetDateOfBirth()
        breinUser.setDateOfBirthValue(0,0,0) // this is wrong date
        Assert.assertTrue(breinUser.getDateOfBirth().isEmpty()!!)
    }

    /**
     * Tests all BreinUser Methods
     */
    @Test
    fun testBreinUserMethods() {
        val breinUser: BreinUser = BreinUser()
            .setFirstName("User")
            .setLastName("Anyhere")
            .setImei("356938035643809")
            .setDateOfBirthValue(6, 20, 1985)
            .setDeviceId("AAAAAAAAA-BBBB-CCCC-1111-222222220000")

        assertFalse(breinUser.toString().isEmpty())
    }

    /**
     * Tests all BreinUser Methods
     */
    @Test
    fun testBreinUserWithNoMethods() {
        val breinUser = BreinUser()
        assertFalse(breinUser.toString().isEmpty())
    }

    /**
     * Test of breinActivityType options
     */
    @Test
    fun testBreinActivityTypeSetToPredefinedString() {
        val breinActivityType = BreinActivityType.CHECKOUT
        assertTrue(breinActivityType == BreinActivityType.CHECKOUT)
    }

    /**
     * Test of breinActivityType options
     */
    @Test
    fun testBreinActivityTypeSetToAnyString() {
        val breinActivityType = "whatYouWant"
        Assert.assertTrue(breinActivityType == "whatYouWant")
    }

    /**
     * Test of breinCategory options to predefined string
     */
    @Test
    fun testBreinCategoryTypeSetToPredefinedString() {
        val breinCategoryType = BreinCategoryType.APPAREL
        Assert.assertTrue(breinCategoryType == BreinCategoryType.APPAREL)
    }

    /**
     * Test of breinCategory options to flexible string
     */
    @Test
    fun testBreinCategoryTypeSetToFlexibleString() {
        val breinCategoryType = "flexibleString"
        Assert.assertTrue(breinCategoryType == "flexibleString")
    }

    @Test
    fun testUserAgent() {
        val userAgent = System.getProperty("http.agent")
        println("UserAgent is: $userAgent")
    }

    @Test
    fun testBreinIpInfo() {
        val externalIp = BreinIpInfo.externalIp
        println("External IP is: $externalIp")
    }

    @Test
    fun testWrongURL() {
        val spec = ""
        val s: String? = invokeRequest(spec)
        if (s != null) {
            println("s is not null")
        } else {
            println ("s is null")
        }
    }

    @Test
    fun testRightURL() {
        val spec = "http://www.ip-api.com/json"
        val s: String? = invokeRequest(spec)
        if (s != null) {
            println("s is not null")
        } else {
            println ("s is null")
        }
    }

    fun invokeRequest(spec: String): String? {
        try {
            return URL(spec).readText()
        } catch (e: Exception) {
            Log.e("BreinIpInfo", "Breinify - exception occurred calling invokeRequest", e)
        }
        return null
    }

    @Test
    fun testLocalDateTime() {
        val defTimeZone = TimeZone.getDefault()
        println("Default Timezone is: $defTimeZone")
        val c = Calendar.getInstance()
        val date = c.time
        val df = SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss ZZZZ (zz)")
        df.timeZone = defTimeZone
        val strDate = df.format(date)
        println("Current LocalTimeZone is: $strDate")
    }

    @Test
    fun testClear() {
        val activity = Breinify.getBreinActivity()
        activity.setDescription("Sample Description")
        val descBefore = activity.getDescription()
        Assert.assertTrue(descBefore.isNotEmpty())

        activity.init()
        val descAfter = activity.getDescription()

        Assert.assertTrue(descAfter.isEmpty())
    }

    companion object {
        private const val VALID_API_KEY = "41B2-F48C-156A-409A-B465-317F-A0B4-E0E8"
        private const val VALID_SECRET_API_KEY = "CA8A-8D28-3408-45A8-8E20-8474-06C0-8548"
        private const val VALID_SECRET = "lmcoj4k27hbbszzyiqamhg=="

        /**
         * Init part
         */
        @BeforeClass
        fun setUp() {
        }
    }
}