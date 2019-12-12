package com.abhishek.notesyncing.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class Converters {
    companion object {
        @TypeConverter
        @JvmStatic
        fun interestStringToList(json: String): List<String> {
            val gson = Gson()
            val listType = object : TypeToken<List<String>>() {}.type
            return gson.fromJson(json , listType )
        }

        @TypeConverter
        @JvmStatic
        fun interestListToString(list: List<String>): String {
            val gson = Gson()
            val type = object : TypeToken<List<String>>() { }.type
            return gson.toJson(list, type)
        }
    }
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }

}