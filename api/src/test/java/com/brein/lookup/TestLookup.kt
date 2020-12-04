package com.brein.lookup

import com.brein.api.BreinLookup
import com.brein.api.Breinify
import com.brein.api.RestCallback
import com.brein.domain.BreinConfig
import com.brein.domain.BreinDimension
import com.brein.domain.BreinUser
import junit.framework.TestCase
import org.junit.AfterClass
import org.junit.Before
import org.junit.Test

/**
 * Test cases for lookup functionality
 */

class TestLookup {
    /**
     * Contains the Breinify User
     */
    private val breinUser = BreinUser("toni.maroni@mail.net")

    /**
     * The Lookup itself
     */
    private val breinLookup = BreinLookup()
    private val restCallback = RestCallback()

    /**
     * Preparation of test case
     */
    @Before
    fun setUp() {
        val breinConfig = BreinConfig(VALID_API_KEY, VALID_SECRET)
        Breinify.setConfig(breinConfig)
    }

    /**
     * Tests the lookup functionality
     *
     */
    @Test
    fun testLookup() {
        val dimensions = arrayOf(
            "firstname", "gender",
            "age", "agegroup", "digitalfootprint", "images"
        )
        val breinDimension = BreinDimension(dimensions.toList())
        breinLookup.setBreinDimension(breinDimension)
        breinLookup.setUser(breinUser)
        breinLookup.execute(restCallback)

        /*
        if (breinResult != null) {
            final Object dataFirstname = breinResult.get("firstname");
            final Object dataGender = breinResult.get("gender");
            final Object dataAge = breinResult.get("age");
            final Object dataAgeGroup = breinResult.get("agegroup");
            final Object dataDigitalFootprinting = breinResult.get("digitalfootprint");
            final Object dataImages = breinResult.get("digitalfootprint");
        }
        */
    }

    companion object {
        /**
         * This has to be a valid api key & secret
         */
        private const val VALID_API_KEY = "41B2-F48C-156A-409A-B465-317F-A0B4-E0E8"
        private const val VALID_SECRET_API_KEY = "CA8A-8D28-3408-45A8-8E20-8474-06C0-8548"
        private const val VALID_SECRET = "lmcoj4k27hbbszzyiqamhg=="

        /**
         * Housekeeping...
         */
        @AfterClass
        fun tearDown() {
            /**
             * we have to wait some time in order to allow the asynch rest processing
             */
            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                TestCase.fail()
            }
        }
    }
}