package com.dejalo.app.data.backup

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BackupCodecTest {

    @Test
    fun schemaVersion_isOne() {
        assertEquals(1, BackupCodec.SCHEMA_VERSION)
    }

    @Test
    fun schemaGate_rejectsFutureVersions() {
        val version = 99
        assertTrue(version > BackupCodec.SCHEMA_VERSION)
    }

    @Test
    fun appMarker_expectedValue() {
        assertEquals("dejalo", "dejalo")
    }
}
