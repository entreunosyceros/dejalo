package com.dejalo.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.dejalo.app.data.local.entity.CravingEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CravingDao {
    @Insert
    suspend fun insert(event: CravingEventEntity): Long

    @Query("SELECT * FROM craving_events ORDER BY triggeredAtMillis DESC")
    fun observeAll(): Flow<List<CravingEventEntity>>

    @Query("SELECT COUNT(*) FROM craving_events")
    fun observeCount(): Flow<Int>
}
