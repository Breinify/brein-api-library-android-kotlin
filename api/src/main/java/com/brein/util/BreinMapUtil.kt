package com.brein.util

import java.io.Serializable

object BreinMapUtil {
    /**
     * Map Helper method used to copy a hashmap of type String, Object
     *
     * @param source contains the original map
     *
     * @return a copy of the map or null if source is null
     */
    fun copyMap(source: MutableMap<*, *>): Map<String, Any?>? {
        val copy: MutableMap<String, Any?> = HashMap()
        for ((key, value) in source) {
            copy[key as String] = copyValue(value)
        }
        return copy
    }

    fun copyList(source: List<Any?>?): List<Any?> {
        if (source == null) {
            return emptyList<Any>()
        }
        val copy: MutableList<Any?> = ArrayList()
        for (value in copy) {
            copy.add(copyValue(value))
        }
        return copy
    }

    @Suppress("UNCHECKED_CAST")
    fun copyValue(value: Any?): Any? {
        return if (MutableList::class.java.isInstance(value)) {
            copyList(MutableList::class.java.cast(value))
        } else if (MutableMap::class.java.isInstance(value)) {
            val inputValue = value!!
            val copyMap = copyMap(inputValue as MutableMap<*, *>)
            copyMap
        } else {
            value
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getNestedValue(map: Map<String, Any?>?, vararg keys: Array<out String?>): T? {
        var currentMap: Map<String, Any?>? = map
        var value: Any? = null
        var i = 0
        while (i < keys.size) {
            val k = keys[i]
            value = currentMap?.get<Serializable?, Any?>(k)
            if (value == null) {
                break
            } else if (MutableMap::class.java.isInstance(value)) {
                currentMap = value as MutableMap<String, Any>
            } else if (i < keys.size - 1) {
                break
            }
            i++
        }
        return if (i == keys.size) ({
            value
        }) as T? else {
            null
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun hasNestedValue(map: MutableMap<String, Any>?, vararg keys: String?): Boolean {
        if (map == null) {
            return false
        }
        var currentMap: Map<String, Any?> = map
        var i = 0
        while (i < keys.size) {
            val k = keys[i]
            val value = currentMap[k]
            if (value == null) {
                break
            } else if (MutableMap::class.java.isInstance(value)) {
                currentMap = value as MutableMap<String, Any?>
            } else if (i < keys.size - 1) {
                break
            }
            i++
        }
        return i == keys.size
    }
}