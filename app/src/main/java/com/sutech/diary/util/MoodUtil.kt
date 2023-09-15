package com.sutech.diary.util

import android.content.Context
import com.sutech.diary.database.DiaryDatabase
import com.sutech.diary.model.MoodObj
import com.sutech.journal.diary.diarywriting.lockdiary.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

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

    suspend fun calTrendPercentages(context: Context, id: Int): String {
        return withContext(Dispatchers.IO) {
            val allMoodsNumber: Int
            val selectedMoodNumber: Int

            DiaryDatabase.getInstance(context).getDiaryDao().getAllDiary().map {
                it.listContent?.map { contentModel -> contentModel.mood }
            }.flatMap {
                if (it.isNullOrEmpty()) {
                    emptyList()
                } else {
                    it
                }
            }.also {
                allMoodsNumber = it.size
            }.filter { moodObj -> moodObj.id == id }.also {
                selectedMoodNumber = it.size
            }
            val percent = selectedMoodNumber.toFloat() / allMoodsNumber * 100

            if (selectedMoodNumber * 100 % allMoodsNumber * 100 == 0) {
                return@withContext percent.toInt().toString()
            } else {
                return@withContext String.format("%.1f", percent)
            }
        }
    }

    suspend fun calTrendOnTenScale(context: Context, id: Int, filter: MoodFilter): Float {
        return withContext(Dispatchers.IO) {
            val allMoodsNumber: Int
            val selectedMoodNumber: Int

            DiaryDatabase.getInstance(context).getDiaryDao().getAllDiary().filter {
                val parser = SimpleDateFormat("dd-MM-yyyy", Locale.US)
                val date = parser.parse(it.dateTime)
                if (date != null) {
                    when (filter) {
                        MoodFilter.SEVEN_DAY -> date >= DateUtil.getDaysAgo(7)
                        MoodFilter.THIRTY_DAY -> date >= DateUtil.getDaysAgo(30)
                        MoodFilter.NINETY_DAY -> date >= DateUtil.getDaysAgo(90)
                        MoodFilter.ALL -> true
                    }
                } else false
            }.map {
                it.listContent?.map { contentModel -> contentModel.mood }
            }.flatMap {
                if (it.isNullOrEmpty()) {
                    emptyList()
                } else {
                    it
                }
            }.also {
                allMoodsNumber = it.size
            }.filter { moodObj -> moodObj.id == id }.also {
                selectedMoodNumber = it.size
            }

            return@withContext selectedMoodNumber.toFloat() / allMoodsNumber * 10
        }
    }

    enum class Mood {
        SMILING, DISAPPOINTED, CRYING, HEART_EYES, ENRAGED, ROLLING_EYES, SUNGLASSES, INJURY, SLEEP, OPEN_MOUTH
    }
}