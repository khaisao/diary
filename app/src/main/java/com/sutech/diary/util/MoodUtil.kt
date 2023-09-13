package com.sutech.diary.util

import com.sutech.diary.model.MoodObj
import com.sutech.journal.diary.diarywriting.lockdiary.R

object MoodUtil {
    val moods = listOf(
        MoodObj(1, Mood.SMILING.name, R.drawable.ic_1),
        MoodObj(2, Mood.HEART_EYES.name, R.drawable.ic_2),
        MoodObj(3, Mood.SLEEP.name, R.drawable.ic_3),
        MoodObj(4, Mood.ENRAGED.name, R.drawable.ic_4),
        MoodObj(5, Mood.ROLLING_EYES.name, R.drawable.ic_5),
        MoodObj(6, Mood.CRYING.name, R.drawable.ic_6),
        MoodObj(7, Mood.OPEN_MOUTH.name, R.drawable.ic_7),
        MoodObj(8, Mood.INJURY.name, R.drawable.ic_8),
        MoodObj(9, Mood.SUNGLASSES.name, R.drawable.ic_9),
        MoodObj(10, Mood.DISAPPOINTED.name, R.drawable.ic_10),
    )

    fun getMoodByName(name: String): MoodObj {
        return moods.first { moodObj -> moodObj.name == name }
    }

    enum class Mood {
        SMILING, DISAPPOINTED, CRYING, HEART_EYES, ENRAGED, ROLLING_EYES, SUNGLASSES, INJURY, SLEEP, OPEN_MOUTH
    }
}