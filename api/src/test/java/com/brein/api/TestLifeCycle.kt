package com.brein.api

import com.brein.domain.BreinActivityType
import com.brein.domain.BreinCategoryType
import org.junit.AfterClass
import org.junit.Assert
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

        val breinConfig = Breinify.config

        Assert.assertEquals(appUser.getFirstName(), "Elvis")
        Assert.assertEquals(appUser.getLastName(), "Presley")
        Assert.assertEquals(breinActivity.getActivityType(), BreinActivityType.LOGIN)
        Assert.assertEquals(breinActivity.getDescription(), "This is a good description")
        Assert.assertEquals(breinConfig?.apiKey, VALID_SIGNATURE_API_KEY)
        Assert.assertEquals(breinConfig?.secret, VALID_SIGNATURE)

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

        val breinConfig = Breinify.config

        Assert.assertEquals(appUser.getFirstName(), "Elvis")
        Assert.assertEquals(appUser.getLastName(), "Presley")
        Assert.assertEquals(breinActivity.getActivityType(), BreinActivityType.LOGIN)
        Assert.assertEquals(breinActivity.getDescription(), "This is a good description")
        Assert.assertEquals(breinConfig?.apiKey, VALID_SIGNATURE_API_KEY)
        Assert.assertEquals(breinConfig?.secret, VALID_SIGNATURE)

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

        val breinConfig = Breinify.config

        Assert.assertEquals(breinUser.getFirstName(), "Elvis")
        Assert.assertEquals(breinUser.getLastName(), "Presley")
        Assert.assertEquals(breinActivity.getActivityType(), BreinActivityType.LOGIN)
        Assert.assertEquals(breinActivity.getDescription(), "This is a good description")
        Assert.assertEquals(breinConfig?.secret, null)
        Assert.assertEquals(breinConfig?.apiKey, VALID_API_KEY)

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

        val config = Breinify.config

        val tagsMap = breinActivity.getTagsDic()

        Assert.assertEquals(breinActivity.getActivityType(), BreinActivityType.PAGE_VISIT)
        Assert.assertEquals(config?.secret, VALID_SIGNATURE)
        Assert.assertEquals(config?.apiKey, VALID_SIGNATURE_API_KEY)
        Assert.assertEquals(tagsMap["pageId"], tagsDic["pageId"])

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

        val config = Breinify.config

        val tagsMap = breinActivity.getTagsDic()

        Assert.assertTrue(tagsDic.size == 3)
        Assert.assertEquals(breinActivity.getActivityType(), BreinActivityType.PAGE_VISIT)
        Assert.assertEquals(config?.secret, VALID_SIGNATURE)
        Assert.assertEquals(config?.apiKey, VALID_SIGNATURE_API_KEY)
        Assert.assertEquals(tagsMap["balancePromotional"], tagsDic["balancePromotional"])
        Assert.assertEquals(tagsMap["balanceCampaign"], tagsDic["balanceCampaign"])
        Assert.assertEquals(tagsMap["pageId"], tagsDic["pageId"])

        Breinify.sendActivity(breinActivity)
    }


    @Suppress("UNCHECKED_CAST")
    @Test
    fun testCheckOutClaro() {

        // 1. configure API
        Breinify.configure(VALID_SIGNATURE_API_KEY, VALID_SIGNATURE)

        val tagsDic = mapOf(
            "productPrices" to listOf(1000),
            "productIds" to listOf("Recharge"),
            "productQuantities" to listOf(1),
            "transactionPriceTotal" to 1000,
            "transactionTotal" to 1000
        ) as HashMap<String, Any>


        val config = Breinify.config
        val breinActivity = Breinify.getBreinActivity()
        breinActivity.setTagsDic(tagsDic)
        val dic = breinActivity.getTagsDic()
        breinActivity.setActivityType(BreinActivityType.CHECKOUT)

        Assert.assertTrue(dic.size == 5)
        Assert.assertEquals(breinActivity.getActivityType(), BreinActivityType.CHECKOUT)
        Assert.assertEquals(config?.secret, VALID_SIGNATURE)
        Assert.assertEquals(config?.apiKey, VALID_SIGNATURE_API_KEY)
        Assert.assertEquals(dic["productPrices"], tagsDic["productPrices"])
        Assert.assertEquals(dic["productIds"], tagsDic["productIds"])
        Assert.assertEquals(dic["productQuantities"], tagsDic["productQuantities"])
        Assert.assertEquals(dic["transactionPriceTotal"], tagsDic["transactionPriceTotal"])
        Assert.assertEquals(dic["transactionTotal"], tagsDic["transactionTotal"])

        Breinify.sendActivity(breinActivity)
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun testAnotherPageVisitActivity() {
        Breinify.configure(VALID_SIGNATURE_API_KEY, VALID_SIGNATURE)

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

        breinActivity.setTagsDic(tagsDic)
        val dic = breinActivity.getTagsDic()

        Assert.assertTrue(dic.size == 7)
        Assert.assertEquals(breinActivity.getActivityType(), BreinActivityType.PAGE_VISIT)
        Assert.assertEquals(dic["balance"], tagsDic["balance"])
        Assert.assertEquals(dic["recharge"], tagsDic["recharge"])
        Assert.assertEquals(dic["package"], tagsDic["package"])
        Assert.assertEquals(dic["consumption"], tagsDic["consumption"])
        Assert.assertEquals(dic["available"], tagsDic["available"])
        Assert.assertEquals(dic["other"], tagsDic["other"])
        Assert.assertEquals(dic["pageId"], tagsDic["pageId"])

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

        val config = Breinify.config

        val tagsMap = breinActivity.getTagsDic()

        Assert.assertEquals(breinActivity.getActivityType(), BreinActivityType.PAGE_VISIT)
        Assert.assertEquals(config?.secret, VALID_SIGNATURE)
        Assert.assertEquals(config?.apiKey, VALID_SIGNATURE_API_KEY)
        Assert.assertEquals(tagsMap["pageId"], tagsDic["pageId"])
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