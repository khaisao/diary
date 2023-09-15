package com.sutech.diary.util

import android.content.Context
import android.util.Log
import com.sutech.diary.database.DiaryDatabase
import com.sutech.diary.model.HashtagQuantity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object HashtagUtil {
    suspend fun getHashtagQuantities(context: Context): List<HashtagQuantity> {
        return withContext(Dispatchers.IO) {
            return@withContext DiaryDatabase.getInstance(context).getDiaryDao().getAllDiary().map {
                it.listContent?.map { contentModel -> contentModel.listHashtag }
            }.flatMap {
                if (it.isNullOrEmpty()) {
                    emptyList()
                } else {
                    it
                }
            }.flatMap {
                it.ifEmpty {
                    emptyList()
                }
            }.groupBy { it }.map {
                Log.d("", "getHashtagQuantities: key: ${it.key}, value: ${it.value}")
                HashtagQuantity(it.key, it.value.size)
            }
        }
    }
}