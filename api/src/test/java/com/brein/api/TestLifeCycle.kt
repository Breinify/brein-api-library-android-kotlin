package com.brein.api

import com.brein.domain.BreinActivityType
import com.brein.domain.BreinCategoryType
import org.junit.AfterClass
import org.junit.Test

class TestLifeCycle {

    /**
     * testcase how to use the activity api
     */
    @Test
    fun testCycleWithSignature() {
        // configure API
        Breinify.configure(VALID_SIGNATURE_API_KEY, VALID_SIGNATURE)

        // get User and set additional user data
        val appUser = Breinify.getUser()
        appUser.setEmail("Elvis.Elvis@gmail.com")
            .setFirstName("Elvis")
            .setLastName("Presley")

        // init device token
        Breinify.initWithDeviceToken("superDeviceToken", null)

        // send some activities
        val breinActivity = Breinify.getBreinActivity()
        breinActivity.setActivityType(BreinActivityType.LOGIN)
            .setDescription("This is a good description")
            .setCategory(BreinCategoryType.HOME)

        Breinify.sendActivity(breinActivity)

        Breinify.shutdown()
    }

    @Test
    fun testCycleWithSignatureTwo() {

        // configure API
        Breinify.configure(VALID_SIGNATURE_API_KEY, VALID_SIGNATURE)

        // get User and set additional user data
        val appUser = Breinify.getUser()
        appUser.setEmail("user.name@gmail.com")
            .setFirstName("Elvis")
            .setLastName("Presley")

        // init device token
//        Breinify.initWithDeviceToken("superDeviceToken")

        // send some activities
        val breinActivity = Breinify.getBreinActivity()
        breinActivity.setActivityType(BreinActivityType.LOGIN)
            .setDescription("This is a good description")
            .setCategory(BreinCategoryType.HOME)

        Breinify.sendActivity(breinActivity)
        Breinify.shutdown()
    }

    /**
     * testcase how to use the activity api
     */
    @Test
    fun testCycleWithOutSignature() {

        // configure API
        Breinify.setConfig(VALID_API_KEY, null)

        // get User and set additional user data
        val breinUser = Breinify.getUser()

        breinUser.setEmail("user.name@gmail.com")
            .setFirstName("Elvis")
            .setLastName("Presley")

        val breinActivity = Breinify.getBreinActivity()
        breinActivity.setActivityType(BreinActivityType.LOGIN)
            .setDescription("This is a good description")
            .setCategory(BreinCategoryType.HOME)

        Breinify.sendActivity(breinActivity)

        Breinify.shutdown()
    }

    @Test
    fun testFullAndroidLifeCycle() {

        // 1. configure API
        Breinify.configure(VALID_SIGNATURE_API_KEY, VALID_SIGNATURE)

    }

    @Test
    fun testPageVisit() {
        // 1. configure API
        Breinify.configure(VALID_SIGNATURE_API_KEY, VALID_SIGNATURE)

        val breinActivity = Breinify.getBreinActivity()
        breinActivity.setActivityType(BreinActivityType.PAGE_VISIT)

        // add activity dic
        val tagsDic = HashMap<String, Any>()
        tagsDic["pageId"] = "packages"
        breinActivity.setTagsDic(tagsDic)

        Breinify.sendActivity(breinActivity)
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun testCheckOut() {

        // 1. configure API
        Breinify.configure(VALID_SIGNATURE_API_KEY, VALID_SIGNATURE)

        val tagsDic = mapOf(
            "balancePromotional" to 0,
            "balanceCampaign" to 0,
            "pageId" to "otherBalance"
        ) as HashMap<String, Any>

        val breinActivity = Breinify.getBreinActivity()
        breinActivity.setTagsDic(tagsDic)
        breinActivity.setActivityType(BreinActivityType.PAGE_VISIT)
        Breinify.sendActivity(breinActivity)
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun testAnotherPageVisitActivity() {
        val tagsDic = mapOf(
            "balance" to 6.902,
            "recharge" to 0,
            "package" to 0,
            "consumption" to 3.498,
            "available" to 2.904,
            "other" to 0,
            "pageId" to "consumptionDetails"
        ) as HashMap<String, Any>

        val breinActivity = Breinify.getBreinActivity()
        breinActivity.setTagsDic(tagsDic)
        breinActivity.setActivityType(BreinActivityType.PAGE_VISIT)

        Breinify.sendActivity(breinActivity)
    }

    @Test
    fun testCoupleOfPageVisitActitvityCalls() {
        // 1. configure API
        Breinify.configure(VALID_SIGNATURE_API_KEY, VALID_SIGNATURE)

        val breinActivity = Breinify.getBreinActivity()
        breinActivity.setActivityType(BreinActivityType.PAGE_VISIT)

        // add activity dic
        val tagsDic = HashMap<String, Any>()
        tagsDic["pageId"] = "packages"
        breinActivity.setTagsDic(tagsDic)

        Breinify.sendActivity(breinActivity)

        // sleep 2 seconds
        Thread.sleep(2_000)

        // send again
        Breinify.sendActivity(breinActivity)
    }


    companion object {
        /**
         * This has to be a valid api key
         */
        private const val VALID_API_KEY = "41B2-F48C-156A-409A-B465-317F-A0B4-E0E8"
        private const val VALID_SIGNATURE_API_KEY = "CA8A-8D28-3408-45A8-8E20-8474-06C0-8548"
        private const val VALID_SIGNATURE = "lmcoj4k27hbbszzyiqamhg=="

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