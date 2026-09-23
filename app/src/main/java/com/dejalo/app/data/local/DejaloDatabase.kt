package com.dejalo.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.dejalo.app.data.local.dao.AlternativeRoutineDao
import com.dejalo.app.data.local.dao.BadgeDao
import com.dejalo.app.data.local.dao.CravingDao
import com.dejalo.app.data.local.dao.RelapseDao
import com.dejalo.app.data.local.dao.UserProfileDao
import com.dejalo.app.data.local.entity.AlternativeRoutineEntity
import com.dejalo.app.data.local.entity.BadgeEntity
import com.dejalo.app.data.local.entity.CravingEventEntity
import com.dejalo.app.data.local.entity.RelapseEventEntity
import com.dejalo.app.data.local.entity.UserProfileEntity

@Database(
    entities = [
        UserProfileEntity::class,
        CravingEventEntity::class,
        RelapseEventEntity::class,
        BadgeEntity::class,
        AlternativeRoutineEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class DejaloDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun cravingDao(): CravingDao
    abstract fun relapseDao(): RelapseDao
    abstract fun badgeDao(): BadgeDao
    abstract fun alternativeRoutineDao(): AlternativeRoutineDao

    companion object {
        @Volatile
        private var instance: DejaloDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE craving_events ADD COLUMN intensityInitial INTEGER NOT NULL DEFAULT -1"
                )
                db.execSQL(
                    "ALTER TABLE craving_events ADD COLUMN intensityFinal INTEGER NOT NULL DEFAULT -1"
                )
                db.execSQL(
                    "ALTER TABLE craving_events ADD COLUMN toolsCsv TEXT NOT NULL DEFAULT ''"
                )
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS alternative_routines (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        situation TEXT NOT NULL,
                        oldPattern TEXT NOT NULL,
                        stepsCsv TEXT NOT NULL,
                        updatedAtMillis INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE relapse_events ADD COLUMN nextPlan TEXT NOT NULL DEFAULT ''"
                )
            }
        }

        fun get(context: Context): DejaloDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    DejaloDatabase::class.java,
                    "dejalo.db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
        }
    }
}
