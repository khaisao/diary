package com.sutech.diary.util

import com.sutech.journal.diary.diarywriting.lockdiary.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import android.content.Context
import com.sutech.diary.view.calendar.CalendarFragment
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class Common {
    companion object{
        fun getDay(dayTime:String,context:Context):String{
            val sdf = SimpleDateFormat(Constant.FormatdayDDMMYY, Locale.getDefault())
            val date: Date = sdf.parse(dayTime) as Date
            val calendar: Calendar = Calendar.getInstance()
            calendar.time = date
            val dayName = when (calendar.get(Calendar.DAY_OF_WEEK)) {
                Calendar.SUNDAY -> R.string.sunday
                Calendar.MONDAY -> R.string.monday
                Calendar.TUESDAY -> R.string.tuesday
                Calendar.WEDNESDAY -> R.string.wednesday
                Calendar.THURSDAY -> R.string.thursday
                Calendar.FRIDAY -> R.string.friday
                Calendar.SATURDAY -> R.string.saturday
                else -> R.string.saturday
            }

            return context.getString(dayName)
        }

        fun monthYearFromDate(date: LocalDate): String {
            val formatter = DateTimeFormatter.ofPattern(Constant.FormatdayMMMMYY, Locale.US)
            return date.format(formatter)
        }
        fun daysInMonthArray(date: LocalDate): ArrayList<String> {
            val daysInMonthArray = ArrayList<String>()
            val yearMonth = YearMonth.from(date)
            val daysInMonth = yearMonth.lengthOfMonth()
            val firstOfMonth: LocalDate = CalendarFragment.selectedDate.withDayOfMonth(1)!!
            val dayOfWeek = firstOfMonth.dayOfWeek.value
            for (i in 1..42) {
                if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                    daysInMonthArray.add("")
                } else {
                    daysInMonthArray.add((i - dayOfWeek).toString())
                }
            }
            return daysInMonthArray
        }
    }
}