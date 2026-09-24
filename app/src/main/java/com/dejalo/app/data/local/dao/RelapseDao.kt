package com.dejalo.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.dejalo.app.data.local.entity.RelapseEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RelapseDao {
    @Insert
    suspend fun insert(event: RelapseEventEntity): Long

    @Query("SELECT * FROM relapse_events ORDER BY occurredAtMillis DESC")
    fun observeAll(): Flow<List<RelapseEventEntity>>

    @Query("SELECT COALESCE(SUM(cigarettes), 0) FROM relapse_events")
    fun observeTotalCigarettes(): Flow<Int>

    @Query("DELETE FROM relapse_events")
    suspend fun deleteAll()
}
