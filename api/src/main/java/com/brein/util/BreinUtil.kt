package com.brein.util

import com.brein.api.BreinBase
import com.brein.api.BreinException
import com.brein.api.Breinify
import com.brein.domain.BreinConfig
import com.brein.domain.BreinIpInfo
import java.security.InvalidKeyException
import java.security.SecureRandom
import java.util.Random

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
    fun generateSecret(length: Int): String {
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

    fun detectIpAddress(): String {
        return BreinIpInfo.externalIp.toString()
    }

    init {
    }
}