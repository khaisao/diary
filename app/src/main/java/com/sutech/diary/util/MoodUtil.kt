package com.sutech.diary.util

import com.sutech.diary.model.MoodObj
import com.sutech.journal.diary.diarywriting.lockdiary.R

object MoodUtil {
    val moods = listOf(
        MoodObj(1, Mood.SMILING.name, R.drawable.ic_smiling),
        MoodObj(2, Mood.DISAPPOINTED.name, R.drawable.ic_disappointed),
        MoodObj(3, Mood.CRYING.name, R.drawable.ic_crying),
        MoodObj(4, Mood.HEART_EYES.name, R.drawable.ic_heart_eyes),
        MoodObj(5, Mood.ENRAGED.name, R.drawable.ic_enraged),
        MoodObj(6, Mood.ROLLING_EYES.name, R.drawable.ic_rolling_eyes),
        MoodObj(7, Mood.SUNGLASSES.name, R.drawable.ic_sunglasses),
        MoodObj(8, Mood.NAUSEATED.name, R.drawable.ic_nauseated),
    )

    fun getMoodByName(name: String): MoodObj {
        return moods.first { moodObj -> moodObj.name == name }
    }

    fun getMoodById(id: Int): MoodObj {
        return moods.first { moodObj -> moodObj.id == id }
    }

    enum class Mood {
        SMILING, DISAPPOINTED, CRYING, HEART_EYES, ENRAGED, ROLLING_EYES, SUNGLASSES, NAUSEATED
    }
}