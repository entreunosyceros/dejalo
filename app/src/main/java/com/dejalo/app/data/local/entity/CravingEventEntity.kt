package com.dejalo.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "craving_events")
data class CravingEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val triggeredAtMillis: Long,
    val trigger: String,
    val durationSeconds: Int = 0,
    val resolved: Boolean = true,
    val notes: String = "",
    /** Intensidad al inicio del episodio (0–10). -1 = no registrada (datos antiguos). */
    val intensityInitial: Int = -1,
    /** Intensidad al cerrar el episodio (0–10). -1 = no registrada. */
    val intensityFinal: Int = -1,
    /** Herramientas usadas, separadas por | (p. ej. respiracion|burbujas|tarjeta). */
    val toolsCsv: String = ""
) {
    val intensityDrop: Int?
        get() = if (intensityInitial in 0..10 && intensityFinal in 0..10) {
            intensityInitial - intensityFinal
        } else {
            null
        }
}
