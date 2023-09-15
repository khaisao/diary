package com.sutech.diary.util

import com.sutech.journal.diary.diarywriting.lockdiary.R

enum class MoodFilter(val stringRes: Int) {
    ALL(R.string.all), SEVEN_DAY(R.string.last_7_days), THIRTY_DAY(R.string.last_30_days), NINETY_DAY(R.string.last_90_days)
}