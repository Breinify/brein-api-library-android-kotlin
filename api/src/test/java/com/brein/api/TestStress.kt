package com.brein.api

import com.brein.domain.BreinActivityType
import com.brein.domain.BreinCategoryType
import com.brein.domain.BreinConfig
import com.brein.domain.BreinUser
import org.junit.AfterClass
import org.junit.Test

class TestStress {

    private val breinUser = BreinUser("User.Name@email.com")

     val breinConfig = BreinConfig(VALID_API_KEY, VALID_SECRET)

    @Test
    fun testActivityStress() {
        var index = 0
        do {
            Thread { testActivity() }.start()
            index++
            println("Thread $index started")
        } while (index <= 2000)
    }

    private fun testActivity() {
        Breinify.setConfig(breinConfig)
        breinUser.setFirstName("Toni")
        breinUser.setLastName("Maroni")
        Breinify.activity(
            breinUser,
            BreinActivityType.OTHER,
            BreinCategoryType.HOME,
            "Description",
            null
        )
    }

    companion object {
        /**
         * This has to be a valid api key
         */
        private const val VALID_API_KEY = "41B2-F48C-156A-409A-B465-317F-A0B4-E0E8"
        private const val VALID_SECRET_API_KEY = "CA8A-8D28-3408-45A8-8E20-8474-06C0-8548"
        private const val VALID_SECRET = "lmcoj4k27hbbszzyiqamhg=="



        @AfterClass
        fun tearDown() {

            // we have to wait some time in order to allow the asynch rest processing
            try {
                Breinify.shutdown()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }
}