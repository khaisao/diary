package com.sutech.diary.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sutech.diary.database.dao.DiaryDao
//import androidx.room.migration.Migration
//import androidx.sqlite.db.SupportSQLiteDatabase
import com.sutech.diary.model.DiaryModel

@Database(entities = [DiaryModel::class], version = 1)
@TypeConverters(Converters::class)
abstract class DiaryDatabase : RoomDatabase() {
    abstract fun getDiaryDao(): DiaryDao

    companion object {
        @Volatile
        private var instance: DiaryDatabase? = null


//        private val MIGRATION = object : Migration(1, 2) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                database.apply {
//                    execSQL("ALTER TABLE widgets ADD COLUMN avatar String DEFAULT ''")
//                    execSQL("ALTER TABLE widgets ADD COLUMN checkList String DEFAULT ''")
//                }
//            }
//        }
        fun getInstance(context: Context): DiaryDatabase {
            if (instance == null) {
                instance =  Room.databaseBuilder(context, DiaryDatabase::class.java, "MyDiary")
//                        .addMigrations(MIGRATION)
                        .build()
            }
            return instance!!
        }
    }

}