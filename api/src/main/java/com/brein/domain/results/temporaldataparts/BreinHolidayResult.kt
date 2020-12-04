package com.brein.domain.results.temporaldataparts

import java.util.*

class BreinHolidayResult(private val result: Map<String, Any>?) {

    var types: List<HolidayType>? = null
    var source: HolidaySource? = null
    var name: String? = null

    enum class HolidaySource {
        GOVERNMENT, UNITED_NATIONS, PUBLIC_INFORMATION, UNKNOWN
    }

    enum class HolidayType {
        NATIONAL_FEDERAL, STATE_FEDERAL, LEGAL, CIVIC, SPECIAL_DAY, EDUCATIONAL, HALLMARK, CULTURAL, RELIGIOUS
    }

    init {
        if (this.result == null || this.result.isEmpty()) {
            this.types = emptyList()
            this.source = HolidaySource.UNKNOWN
            this.name = null
        } else {
            name = if (this.result.containsKey(HOLIDAY_NAME_KEY)) {
                val toString = result[HOLIDAY_NAME_KEY].toString()
                toString
            } else {
                null
            }
            this.source = if (this.result.containsKey(HOLIDAY_SOURCE_KEY)) {
                HolidaySource.valueOf(
                    result[HOLIDAY_SOURCE_KEY]
                        .toString()
                        .replace(' ', '_')
                        .toUpperCase(Locale.getDefault())
                )
            } else {
                HolidaySource.UNKNOWN
            }
            this.types = if (result.containsKey(HOLIDAY_TYPE_KEY)) {
                val holidayList = HolidayType.values().toList()
                holidayList
            } else {
                emptyList()
            }
        }
    }

    companion object {
        private const val HOLIDAY_TYPE_KEY = "types"
        private const val HOLIDAY_SOURCE_KEY = "source"
        private const val HOLIDAY_NAME_KEY = "holiday"
    }

}