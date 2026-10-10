package com.example.a2048.data.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromIntMatrix(value: List<List<Int>>): String = gson.toJson(value)

    @TypeConverter
    fun toIntMatrix(value: String): List<List<Int>> =
        gson.fromJson(value, object : TypeToken<List<List<Int>>>() {}.type)

    @TypeConverter
    fun fromPairs(value: List<Pair<Int, Int>>): String = gson.toJson(value)

    @TypeConverter
    fun toPairs(value: String): List<Pair<Int, Int>> =
        gson.fromJson(value, object : TypeToken<List<Pair<Int, Int>>>() {}.type)
}
