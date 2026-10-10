package com.example.a2048.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.a2048.data.db.RecordEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface RecordsDao {

    @Query("SELECT * FROM records ORDER BY score DESC, createdAt ASC")
    fun observeAll(): Flow<List<RecordEntity>>

    @Insert
    suspend fun insert(record: RecordEntity)

    @Query("DELETE FROM records")
    suspend fun clear()
}
