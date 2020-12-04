package com.brein.engine

import org.junit.AfterClass
import org.junit.Before
import org.junit.Test

class TestEngine {
    /**
     * Preparation for the test cases
     */
    @Before
    fun setUp() {
    }

    /**
     * This should run some tests for the jersey client api...
     */
    @Test
    fun testJerseyRestEngine() {
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
        }
    }
}