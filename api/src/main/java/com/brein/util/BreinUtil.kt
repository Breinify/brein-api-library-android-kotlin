package com.brein.util

import com.brein.api.BreinBase
import com.brein.api.BreinException
import com.brein.api.Breinify
import com.brein.domain.BreinConfig
import com.brein.domain.BreinIpInfo
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.security.InvalidKeyException
import java.security.SecureRandom
import java.util.*

/**
 * Utility class
 */
object BreinUtil {
    private const val AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private val RANDOM = Random()

    /**
     * Verifies if the object contains a value
     * Return false in case of:
     * - null
     * - empty strings
     *
     * @param value to check
     * @return true if object contains data
     */
    fun containsValue(value: Any?): Boolean {
        if (value == null) {
            return false
        }
        if (value.javaClass == String::class.java) {
            val strObj = value as String
            return strObj.isNotEmpty()
        }
        return true
    }

    /**
     * Helper method to generate a random string
     *
     * @return string
     */
    fun randomString(): String {
        val len = 1 + RANDOM.nextInt(100)
        return randomString(len)
    }

    /**
     * Helper methods generates a random string by len
     *
     * @param len of the requested string
     * @return random string
     */
    fun randomString(len: Int): String {
        val sb = StringBuilder(len)
        for (i in 0 until len) {
            sb.append(AB[RANDOM.nextInt(AB.length)])
        }
        return sb.toString()
    }

    /**
     * Creates a secret by given len
     *
     * @param length of the secret
     * @return created secret
     */
    fun generateSecret(length: Int): String? {
        val random = SecureRandom()
        val bytes = ByteArray(length / 8)
        random.nextBytes(bytes)
        return BreinCrypto.encodeBytes(bytes)
    }

    /**
     * generates the signature
     *
     * @param message contains the message
     * @param secret  contains the secret
     * @return signature
     */
    fun generateSignature(message: String, secret: String): String {
        return try {
           BreinCrypto.generateSignature(message, secret)
        } catch (e: InvalidKeyException) {
            throw IllegalStateException("Unable to create signature!", e)
        }
    }


    /**
     * Validates if the URL is correct.
     *
     * @param url to check
     * @return true if ok otherwise false
     */
    fun isUrlValid(url: String?): Boolean? {
        return url?.isNotEmpty()
    }

    fun isUrlValidNotWorkingOnAndroid(url: String?): Boolean {
        return try {
            val u = URL(url)
            val huc = u.openConnection() as HttpURLConnection
            huc.requestMethod = "POST"
            huc.setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)"
            )

            // Todo: this has caused issues on Android
            huc.connect()
            true
        } catch (e: IOException) {

            // this must be an error case!
            false
        }
    }

    /**
     * checks if the url is valid -> if not an exception will be thrown
     *
     * @param fullyQualifiedUrl url with endpoint
     */
    @Throws(BreinException::class)
    fun validateUrl(fullyQualifiedUrl: String) {
        val validUrl = isUrlValid(fullyQualifiedUrl)
        if (validUrl == false) {
            val msg = "URL: $fullyQualifiedUrl is not valid!"
            throw BreinException(msg)
        }
    }

    /**
     * validates the activity object
     *
     * @param breinBase object to validate
     */
    fun validateBreinBase(breinBase: BreinBase?) {
        if (null == breinBase) {
            throw BreinException(BreinException.BREIN_BASE_VALIDATION_FAILED)
        }
    }

    /**
     * validates the configuration object
     *
     * @param breinBase activity or lookup object
     */
    @Suppress("UNUSED_PARAMETER", "UNUSED")
    fun validateConfig(breinBase: BreinBase?) {
        Breinify.config ?: throw BreinException(BreinException.CONFIG_VALIDATION_FAILED)
    }

    /**
     * retrieves the fully qualified url (base + endpoint)
     *
     * @param breinBase activity or lookup object
     * @return full url
     */
    fun getFullyQualifiedUrl(breinBase: BreinBase): String {
        val breinConfig: BreinConfig? = Breinify.config
        val url: String = breinConfig?.baseUrl ?: throw BreinException(BreinException.URL_IS_NULL)
        val endPoint: String? = breinBase.getEndPoint(breinConfig)
        return url + endPoint
    }

    /**
     * retrieves the request body depending of the object
     *
     * @param breinBase object to use
     * @return request as json string
     */
    fun getRequestBody(breinBase: BreinBase): String {
        val requestBody: String = breinBase.prepareRequestData(Breinify.config!!)
        if (!containsValue(requestBody)) {
            throw BreinException(BreinException.REQUEST_BODY_FAILED)
        }
        return requestBody
    }

    /**
     * Invokes validation of BreinBase object, configuration and url.
     * The "validator" will throw an exception in case of any mis-behaviour.
     *
     * @param breinBase activity or lookup object
     */
    fun validate(breinBase: BreinBase?) {

        // validation of activity and config
        validateBreinBase(breinBase)
        validateConfig(breinBase)

        // validate URL, might throw an exception...
        // validateUrl(getFullyQualifiedUrl(breinBase));
    }

    /**
     * Safely casting long to int in Java without using java.lang.Math.toIntExact
     *
     * @param value long value to cast
     * @return int or exception
     */
    fun safeLongToInt(value: Long): Int {
        val i = value.toInt()
        require(i.toLong() == value) { "$value cannot be cast to int without changing its value." }
        return i
    }

    /*
    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':') < 0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (final Exception ex) {
            // for now eat exceptions
        }
        return "";
    }
    */
    fun detectIpAddress(): String {
        return BreinIpInfo.externalIp.toString()
    }

    init {
//        try {
//            mac = Mac.getInstance("HmacSHA256")
//        } catch (e: NoSuchAlgorithmException) {
//            throw IllegalStateException("Unable to find needed algorithm!", e)
//        }
    }
}