package com.dejalo.app.data.local

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DejaloDatabaseMigrationTest {

    private val dbName = "migration-test.db"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        DejaloDatabase::class.java,
        emptyList(),
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrate1To2() {
        helper.createDatabase(dbName, 1).apply {
            execSQL(
                """
                CREATE TABLE IF NOT EXISTS craving_events (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    triggeredAtMillis INTEGER NOT NULL,
                    trigger TEXT NOT NULL,
                    durationSeconds INTEGER NOT NULL,
                    resolved INTEGER NOT NULL,
                    notes TEXT NOT NULL
                )
                """.trimIndent()
            )
            close()
        }
        helper.runMigrationsAndValidate(dbName, 2, true, DejaloDatabase.MIGRATION_1_2)
    }

    @Test
    @Throws(IOException::class)
    fun migrate2To3() {
        helper.createDatabase(dbName, 2).apply { close() }
        helper.runMigrationsAndValidate(dbName, 3, true, DejaloDatabase.MIGRATION_2_3)
    }

    @Test
    @Throws(IOException::class)
    fun migrate3To4() {
        helper.createDatabase(dbName, 3).apply {
            execSQL(
                """
                CREATE TABLE IF NOT EXISTS relapse_events (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    occurredAtMillis INTEGER NOT NULL,
                    cigarettes INTEGER NOT NULL,
                    cause TEXT NOT NULL,
                    notes TEXT NOT NULL
                )
                """.trimIndent()
            )
            close()
        }
        helper.runMigrationsAndValidate(dbName, 4, true, DejaloDatabase.MIGRATION_3_4)
    }

    @Test
    @Throws(IOException::class)
    fun migrate1To4_chained() {
        helper.createDatabase(dbName, 1).apply {
            execSQL(
                """
                CREATE TABLE IF NOT EXISTS craving_events (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    triggeredAtMillis INTEGER NOT NULL,
                    trigger TEXT NOT NULL,
                    durationSeconds INTEGER NOT NULL,
                    resolved INTEGER NOT NULL,
                    notes TEXT NOT NULL
                )
                """.trimIndent()
            )
            execSQL(
                """
                CREATE TABLE IF NOT EXISTS relapse_events (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    occurredAtMillis INTEGER NOT NULL,
                    cigarettes INTEGER NOT NULL,
                    cause TEXT NOT NULL,
                    notes TEXT NOT NULL
                )
                """.trimIndent()
            )
            close()
        }
        helper.runMigrationsAndValidate(
            dbName,
            4,
            true,
            *DejaloDatabase.allMigrations()
        )
    }
}
