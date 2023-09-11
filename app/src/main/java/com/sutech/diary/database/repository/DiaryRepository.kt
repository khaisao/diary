package com.sutech.diary.database.repository

import android.app.Application
import com.sutech.diary.database.dao.DiaryDao
import com.sutech.diary.database.DiaryDatabase
import com.sutech.diary.model.DiaryModel

class DiaryRepository(app: Application) {
    private val diaryDao: DiaryDao
    init {
        val diaryDatabase: DiaryDatabase = DiaryDatabase.getInstance(app)
        diaryDao = diaryDatabase.getDiaryDao()
    }

    suspend fun insertDiary(diary: DiaryModel) = diaryDao.insertDiary(diary)
    suspend fun updateDiary(diary: DiaryModel) = diaryDao.updateDiary(diary)
    suspend fun deleteDiary(diary: DiaryModel) = diaryDao.deleteDiary(diary)

    fun getAllDiary(): MutableList<DiaryModel> = diaryDao.getAllDiary()
}