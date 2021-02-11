package com.brein.config

import com.brein.domain.BreinConfig
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test

/**
 * Test of configuration
 */

class TestConfig {

    //@Test(expected= BreinInvalidConfigurationException.class)
    fun testConfigWithWrongUrl() {
        val wrongUrl = "https://breeeeeinify.com"
        val breinConfig = BreinConfig(testApiKey)
            .setBaseUrl(wrongUrl)
        val isValid = breinConfig.isUrlValid(wrongUrl)
        Assert.assertFalse(isValid)
    }

    /**
     * Test a config with a correct url
     */
    @Test
    fun testConfigWithCorrectUrl() {
        val correctUrl = "http://google.com"
        val breinConfig = BreinConfig(testApiKey)
            .setBaseUrl(correctUrl)
        val isValid = breinConfig.isUrlValid(correctUrl)
        Assert.assertTrue(isValid)
    }

    /**
     * Tests if both Breinify URL's are reachable:
     * https://api.breinify.com
     * http://api.breinify.com
     */
    @Test
    fun testBreinifyUrls() {
        val httpsUrl = "https://api.breinify.com"
        val httpUrl = "http://api.breinify.com"

        // HTTPS
        val breinConfigHttps = BreinConfig(testApiKey)
            .setBaseUrl(httpsUrl)
        val isHttpsValid = breinConfigHttps.isUrlValid(httpsUrl)
        Assert.assertTrue(isHttpsValid)

        // HTTP
        val breinConfigHttp = BreinConfig(testApiKey)
            .setBaseUrl(httpUrl)
        val isHttpValid = breinConfigHttp.isUrlValid(httpUrl)
        Assert.assertTrue(isHttpValid)
    }

    companion object {
        private const val testApiKey = "TEST-API-KEY"

        /**
         * Init part
         */
        @BeforeClass
        fun setUp() {
        }
    }
}