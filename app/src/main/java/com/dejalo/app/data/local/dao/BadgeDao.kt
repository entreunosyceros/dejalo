package com.dejalo.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dejalo.app.data.local.entity.BadgeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BadgeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun unlock(badge: BadgeEntity)

    @Query("SELECT * FROM badges ORDER BY unlockedAtMillis ASC")
    fun observeAll(): Flow<List<BadgeEntity>>

    @Query("SELECT id FROM badges")
    suspend fun getUnlockedIds(): List<String>
}
