package com.brein.engine

import android.util.Log
import com.brein.api.*
import com.brein.domain.BreinConfig
import com.brein.domain.BreinResult
import com.brein.util.BreinUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.PrintWriter
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

class HttpUrlRestEngine : IRestEngine {

    /**
     * invokes the post request. Needs to run a thread.
     *
     * @param breinActivity data
     */
    override fun doRequest(breinActivity: BreinActivity?) {

        // validate the input objects
        BreinUtil.validate(breinActivity)

        val fullUrl: String = BreinUtil.getFullyQualifiedUrl(breinActivity as BreinBase)
        val requestBody: String = BreinUtil.getRequestBody(breinActivity)
        Log.d(TAG, "Request is: $requestBody")

        val connectionTimeout = Breinify.config?.connectionTimeout as Int
        val readTimeout = Breinify.config?.socketTimeout as Int

        Thread {
            try {
                val url = URL(fullUrl)
                val conn = url.openConnection() as HttpURLConnection

                conn.readTimeout = readTimeout
                conn.connectTimeout = connectionTimeout
                conn.requestMethod = POST_METHOD
                conn.doInput = true
                conn.doOutput = true
                Log.d(TAG, "Outputstream is: " + conn.outputStream)

                val out = PrintWriter(conn.outputStream)
                out.print(requestBody)
                out.close()
                conn.connect()
                val response = conn.responseCode
                Log.d(TAG, "response is: $response")
            } catch (e: Exception) {
                Log.d(TAG, "HttpUrlRestEngine exception is: $e")
            }
        }.start()
    }

    /**
     * performs a lookup and provides details
     *
     * @param breinLookup contains request data
     * @return response from Breinify
     */
    override fun doLookup(breinLookup: BreinLookup?): BreinResult? {

        // validate the input objects
        BreinUtil.validate(breinLookup as BreinBase)

        val fullUrl: String = BreinUtil.getFullyQualifiedUrl(breinLookup as BreinBase)
        val requestBody: String = BreinUtil.getRequestBody(breinLookup)
        val connectionTimeout = Breinify.config?.connectionTimeout as Int
        val readTimeout = Breinify.config?.socketTimeout as Int

        Thread {
            try {
                val url = URL(fullUrl)
                val conn = url.openConnection() as HttpURLConnection

                conn.readTimeout = readTimeout
                conn.connectTimeout = connectionTimeout
                conn.requestMethod = POST_METHOD
                conn.doInput = true
                conn.doOutput = true
                Log.d(TAG, "Outputstream is: " + conn.outputStream)

                val out = PrintWriter(conn.outputStream)
                out.print(requestBody)
                out.close()
                conn.connect()

                val response = conn.responseCode
                if (response == HttpURLConnection.HTTP_OK) {
                    val sb = StringBuilder()
                    val mInputStream = conn.inputStream
                    var i: Int
                    while (mInputStream.read().also { i = it } != -1) {
                        sb.append(i.toChar())
                    }
                } else {
                    Log.d(TAG, "doLookup - exception")
                }
            } catch (e: Exception) {
                Log.d(TAG, "doLookup - exception")
            }
        }.start()
        return null
    }

    /**
     * stops possible functionality (e.g. threads)
     */
    override fun terminate() {}
    override fun getRestEngine(engine: BreinEngineType?): IRestEngine? {
        return null
    }

    override fun getRestEngineType(engine: BreinEngineType?): BreinEngineType? {
        return null
    }

    /**
     * configuration of the rest  client
     */
    override fun configure(breinConfig: BreinConfig?) {}

    override fun invokeRequest(
        config: BreinConfig?,
        data: BreinBase?,
        callback: ICallback<BreinResult?>?
    ) {

        // validate the input objects
        BreinUtil.validate(data)
        val fullUrl: String = BreinUtil.getFullyQualifiedUrl(data!!)
        val requestBody: String = BreinUtil.getRequestBody(data)
        Log.d(TAG, "InvokeRequest - request is:  $requestBody")

        val connectionTimeout = Breinify.config?.connectionTimeout
        val readTimeout = Breinify.config?.socketTimeout

        Thread {
        try {
            val url = URL(fullUrl)
            val bytes = requestBody.toByteArray(Charset.forName("utf-8"))
            val conn = url.openConnection() as HttpURLConnection

            if (readTimeout != null) {
                conn.readTimeout = readTimeout
            }

            if (connectionTimeout != null) {
                conn.connectTimeout = connectionTimeout
            }

            conn.requestMethod = POST_METHOD
            conn.doInput = true
            conn.doOutput = true
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("Content-Length", bytes.size.toString())
            conn.setRequestProperty("charset", "utf-8")
            conn.setFixedLengthStreamingMode(bytes.size)
            conn.outputStream.write(bytes, 0, bytes.size)
            conn.outputStream.flush()

            val response = conn.responseCode
            Log.d(TAG, "InvokeRequest - response is:  " + response)

            var breinResponse: BreinResult? = null
            if (response == HttpURLConnection.HTTP_OK) {
                val jsonResponse = StringBuilder()
                val mInputStream = conn.inputStream
                var i: Int

                while (mInputStream.read().also { i = it } != -1) {
                    jsonResponse.append(i.toChar())
                }

                val mapResponse: MutableMap<String, Any?> =
                    Gson().fromJson(
                        jsonResponse.toString(),
                        object : TypeToken<Map<String, Any?>>() {}.type
                    )

                breinResponse = BreinResult(mapResponse)
            } else {
                val res = mutableMapOf<String, Any?>()
                res["code"] = response
                breinResponse = BreinResult(res)
            }
            conn.disconnect()
            callback?.callback(breinResponse)

        } catch (e: Exception) {
            Log.d(TAG, "HttpUrlRestEngine exception is: $e")
            // throw new BreinException("REST rest call exception");
        }
        }.start()
    }

    companion object {
        private const val TAG = "HttpUrlRestEngine"

        /**
         * constant for post method
         */
        private const val POST_METHOD = "POST"
    }
}