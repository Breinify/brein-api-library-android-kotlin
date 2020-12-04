package com.brein.domain.results.temporaldataparts

import com.brein.util.BreinUtil
import com.brein.util.JsonHelper
import java.util.Locale

class BreinEventResult(private val result: Map<String, Any?>?) {

    val name: String
    val start: Long
    val end: Long
    val category: EventCategory
    val size: Int?

    enum class EventCategory {
        CONCERT, COMEDY, OTHERSHOW, POLITICAL, SPORTS, EDUCATIONAL, FITNESS, UNKNOWN
    }

    init {
        name = JsonHelper.getOr(result, NAME_KEY, null).toString()
        start = JsonHelper.getOrLong(result, START_KEY)!!
        end = JsonHelper.getOrLong(result, END_KEY)!!
        val innerSize: Long? = JsonHelper.getOrLong(result, SIZE_KEY)
        size =
            if (innerSize == null || innerSize == -1L) null else BreinUtil.safeLongToInt(innerSize)

        val categoryName: String? = JsonHelper.getOrString(result as MutableMap<String, Any?>?, CATEGORY_KEY)

        categoryName?.replace("eventCategory", "")
        categoryName?.toUpperCase(Locale.getDefault())

        var tmpCategory = EventCategory.UNKNOWN

        for (eventCategory in EventCategory.values()) {
            val str = eventCategory.toString()
            if (str.compareTo(categoryName.toString(), ignoreCase = true) == 0) {
                tmpCategory = eventCategory
            }
        }
        category = tmpCategory
    }

    companion object {
        private const val NAME_KEY = "displayName"
        private const val START_KEY = "startTime"
        private const val END_KEY = "endTime"
        private const val CATEGORY_KEY = "category"
        private const val SIZE_KEY = "sizeEstimated"
    }
}