package com.sutech.diary.database.dao

import androidx.room.*
import com.sutech.diary.model.DiaryModel

@Dao
interface DiaryDao {

    @Insert
    suspend fun insertDiary(diaryModel: DiaryModel)

    @Update
    suspend fun updateDiary(diaryModel: DiaryModel)

    @Delete
    suspend fun deleteDiary(diaryModel: DiaryModel)


    @Query("select * from diary_table  ORDER BY id_col DESC")
    fun getAllDiary(): MutableList<DiaryModel>

   @Query("select * from diary_table where date_time_col = :date_time")
    fun getDiaryByDateTime(date_time:String): DiaryModel?

   @Query("delete from diary_table where id_col = :idDiary")
    fun deleteDiary(idDiary :Long?)

   @Query("select * from diary_table where date_time_col = :date_time ORDER BY id_col DESC")
    fun searchDiaryByDateTime(date_time:String): MutableList<DiaryModel>

   @Query("select * from diary_table where date_list_content LIKE :title ORDER BY id_col DESC")
    fun searchDiaryByTitle(title:String): MutableList<DiaryModel>


}