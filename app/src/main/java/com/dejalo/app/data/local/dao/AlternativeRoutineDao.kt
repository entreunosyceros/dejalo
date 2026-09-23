package com.dejalo.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dejalo.app.data.local.entity.AlternativeRoutineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlternativeRoutineDao {
    @Query("SELECT * FROM alternative_routines ORDER BY situation ASC")
    fun observeAll(): Flow<List<AlternativeRoutineEntity>>

    @Query("SELECT * FROM alternative_routines WHERE situation = :situation LIMIT 1")
    fun observeBySituation(situation: String): Flow<AlternativeRoutineEntity?>

    @Query("SELECT * FROM alternative_routines WHERE situation = :situation LIMIT 1")
    suspend fun getBySituation(situation: String): AlternativeRoutineEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(routine: AlternativeRoutineEntity): Long

    @Update
    suspend fun update(routine: AlternativeRoutineEntity)

    @Delete
    suspend fun delete(routine: AlternativeRoutineEntity)

    @Query("DELETE FROM alternative_routines WHERE id = :id")
    suspend fun deleteById(id: Long)
}
