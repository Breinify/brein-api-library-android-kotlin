package com.brein.util

import com.brein.api.BreinActivity
import com.brein.domain.BreinConfig
import com.brein.util.BreinUtil.generateSecret
import com.brein.util.BreinUtil.generateSignature
import com.brein.util.BreinUtil.randomString
import com.google.gson.Gson
import junit.framework.Assert.assertEquals
import junit.framework.Assert.fail
import org.junit.Assert
import org.junit.Test
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException
import java.net.URL

// status: ok

class TestUtil {

    @Test
    fun testSignature() {

        val generateSignature = generateSignature("apiKey", "secretkey")
        print (generateSignature)

        Assert.assertEquals(
            "h5HRhGRwWlRs9pscyHhQWNc7pxnDOwDZBIAnnhEQbrU=",
            generateSignature("apiKey", "secretkey")
        )
        val secret = generateSecret(128)
        for (i in 0..99) {
            // this is our test case, for the message 'apiKey' and secret 'secretKey'
            // it must return the used signature, the message should vary on each
            // request
            generateSignature(randomString(), secret!!)
        }
    }

   @Test
   fun testActivitySecret() {
       val expected = "WbHv67OJ5LPSCJu7kfh9QOX8b7wkuLiTmE6OTyPqT0g="
       val timestamp: Long = 1487235949
       val activityType = "paginaUno"

       val breinActivity = BreinActivity()

       breinActivity.setUnixTimestamp(timestamp)
       breinActivity.setActivityType(activityType)

       try {
           val validApiKeyWithSecret = "CA8A-8D28-3408-45A8-8E20-8474-06C0-8548"
           val validSecret = "lmcoj4k27hbbszzyiqamhg=="

           val breinConfig = BreinConfig(validApiKeyWithSecret, validSecret)

           breinActivity.setConfig(breinConfig)

           val generated = breinActivity.createSignature(breinConfig)
           print(generated)
           assertEquals(expected, generated)
       } catch (e: Exception) {
           fail()
       }
   }

    @Test
    fun testExtendedSignature() {
        var expected = "h5HRhGRwWlRs9pscyHhQWNc7pxnDOwDZBIAnnhEQbrU="
        var generated = generateSignature("apiKey", "secretkey")
        assertEquals(expected, generated)

        expected = "qnR8UCqJggD55PohusaBNviGoOJ67HC6Btry4qXLVZc="
        generated = generateSignature("Message", "secret")
        assertEquals(expected, generated)
    }

    @Test
    fun testMobileSignature() {
        val mobileMessage = "1486992560-2017-02-13 14:30:37 GMT+01:00 (MEZ)-Europe/Berlin"
        val mobileSecret = "lmcoj4k27hbbszzyiqamhg=="
        val mobileSignature = "oZxTFc1ZPpelBCoGVhRk0/3IMm9tEtwJd9LNDFrgtM0="
        val javaSignature = generateSignature(mobileMessage, mobileSecret)

        assertEquals(mobileSignature, javaSignature)
    }

    @Test
    fun testIpAddress() {
        val extIp2 = externalIp2

        if (extIp2 != null) {
            print("ExIP is:$extIp2")
        }

        val extIp = externalIpAsJson
        if (extIp != null) {
            print("ExIP is:$extIp")
            val result: Map<String, Any> =
                Gson().fromJson<Map<*, *>>(extIp, MutableMap::class.java) as Map<String, Any>
            print("Map is: $result")
        }

        val ip1 = localIpAddress
        if (ip1 != null) {
            print("IP1 is: $ip1")
        }

        val ip2 = localIpAddress2
        if (ip2 != null) {
            print("IP2 is: $ip2")
        }

        val detectedIp = BreinUtil.detectIpAddress();
        print("detectedIp $detectedIp")
    }

    private val localIpAddress: String?
        get() {
            try {
                val en = NetworkInterface.getNetworkInterfaces()
                while (en.hasMoreElements()) {
                    val intf = en.nextElement()
                    val enumIpAddr = intf.inetAddresses
                    while (enumIpAddr.hasMoreElements()) {
                        val inetAddress = enumIpAddr.nextElement()
                        if (!inetAddress.isLoopbackAddress) {
                            return inetAddress.hostAddress
                        }
                    }
                }
            } catch (ex: Exception) {
                print("IP Address$ex")
            }
            return null
        }

    private val localIpAddress2: String?
        get() {
            try {
                val en = NetworkInterface.getNetworkInterfaces()
                while (en.hasMoreElements()) {
                    val intf = en.nextElement()
                    val enumIpAddr = intf.inetAddresses
                    while (enumIpAddr.hasMoreElements()) {
                        val inetAddress = enumIpAddr.nextElement()
                        if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                            return inetAddress.getHostAddress()
                        }
                    }
                }
            } catch (ex: SocketException) {
                ex.printStackTrace()
            }
            return null
        }

    private val externalIp2: String?
        get() {
            try {
                return URL("http://www.ip-api.com/json").readText()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

    // url = new URL("http://ifcfg.me/ip");
    private val externalIpAsJson: String?
        get() {
            try {
                return URL("http://www.ip-api.com/json").readText()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

}