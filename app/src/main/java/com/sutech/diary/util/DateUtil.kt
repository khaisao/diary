package com.sutech.diary.util

import java.util.Calendar
import java.util.Date

object DateUtil {
    fun getDaysAgo(daysAgo: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)

        return calendar.time
    }
}