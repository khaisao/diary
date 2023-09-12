package com.sutech.diary.util

import com.sutech.journal.diary.diarywriting.lockdiary.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import android.content.Context
class Common {
    companion object{
        fun getDay(dayTime:String,context:Context):String{
            val sdf = SimpleDateFormat(Constant.FormatdayDDMMYY, Locale.getDefault())
            val date: Date? = sdf.parse(dayTime)
            val calendar: Calendar = Calendar.getInstance()
            calendar.time = date!!
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
    }
}