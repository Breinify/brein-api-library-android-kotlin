package com.brein.util

object JsonHelper {
    /**
     * Tries to grab an element from a json
     *
     * @param json         the parsed json
     * @param key          the key to look at
     * @param defaultValue the fallback value if we can't find the key
     * @param <T>          the class of the value
     *
     * @return the key value or defaultValue
    </T> */
    fun <T> getOr(json: Map<String, Any?>?, key: String?, defaultValue: T): Any? {
        return if (json == null) {
            defaultValue
        } else if (json.containsKey(key)) {
            json[key]
        } else {
            defaultValue
        }
    }

    /**
     * There isn't a clear difference between doubles and longs in jsons, so we have to specifically cast longs
     */
    fun getOrLong(json: Map<String, Any?>?, key: String?): Long? {
        return if (json == null) {
            null
        } else if (json.containsKey(key)) {
            (json[key] as Double?)!!.toLong()
        } else {
            null
        }
    }

    fun getOrDouble(json: MutableMap<String, Any?>?, key: String?): Double? {
        return if (json == null) {
            null
        } else if (json.containsKey(key)) {
            (json[key] as Double?)!!.toDouble()
        } else {
            null
        }
    }

    fun getOrString(json: MutableMap<String, Any?>?, key: String?): String? {
        return if (json == null) {
            null
        } else if (json.containsKey(key)) {
            (json[key] as String?)
        } else {
            null
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun getOrMap(json: MutableMap<String, Any?>?, key: String?): MutableMap<String, Any?>? {
        return if (json == null) {
            null
        } else if (json.containsKey(key)) {
            (json[key] as MutableMap<String, Any?>)
        } else {
            null
        }
    }

}