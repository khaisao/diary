package com.sutech.diary.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.sutech.diary.database.Converters

@Entity(tableName = "diary_table")
data class DiaryModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_col")
    var id: Long? =null,
    @ColumnInfo(name = "date_time_col")
    var dateTime: String = "",
    @ColumnInfo(name = "date_list_content")
    @TypeConverters(Converters::class)
    var listContent:ArrayList<ContentModel>?=null

)