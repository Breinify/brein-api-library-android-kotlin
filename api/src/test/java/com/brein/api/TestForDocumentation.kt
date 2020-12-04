package com.brein.api

import com.brein.domain.BreinConfig
import com.brein.domain.BreinResult
import com.brein.domain.results.temporaldataparts.BreinLocationResult
import org.junit.Test

class TestForDocumentation {

    val breinConfig = BreinConfig(VALID_API_KEY, VALID_SECRET)

    @Test
    fun testGeocoding() {
        Breinify.setConfig(breinConfig)
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
        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        Breinify.shutdown()
    }

    companion object {
        private const val VALID_API_KEY = "41B2-F48C-156A-409A-B465-317F-A0B4-E0E8"
        private const val VALID_SECRET_API_KEY = "CA8A-8D28-3408-45A8-8E20-8474-06C0-8548"
        private const val VALID_SECRET = "lmcoj4k27hbbszzyiqamhg=="

    }
}