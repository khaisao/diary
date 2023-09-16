package com.sutech.diary.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sutech.diary.model.ContentModel
import java.lang.reflect.Type

class Converters {

    @TypeConverter
    fun fromStringArrCheckList(value: String?): ArrayList<ContentModel>? {
        try {
            val listType: Type = object : TypeToken<ArrayList<ContentModel>>() {}.type
            return if (value.isNullOrEmpty()) {
                null
            } else {
                return Gson().fromJson(value, listType)
            }
        } catch (e: Exception) {
            return null
        }
    }


    @TypeConverter
    fun fromArrayRing(list: ArrayList<ContentModel>): String? {
        return try {
            if (list.isNullOrEmpty()) {
                ""
            } else {
                Gson().toJson(list)
            }
        } catch (e: Exception) {
            ""
        }

    }

}