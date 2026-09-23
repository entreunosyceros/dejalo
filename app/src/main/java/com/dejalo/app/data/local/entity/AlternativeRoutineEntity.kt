package com.dejalo.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Ritual alternativo para romper la asociación automática
 * (p. ej. café → cigarrillo) por pasos sin fumar.
 */
@Entity(tableName = "alternative_routines")
data class AlternativeRoutineEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    /** Situación / desencadenante (alineado con triggers o situación libre). */
    val situation: String,
    /** Patrón antiguo, p. ej. "Café → cigarrillo". */
    val oldPattern: String,
    /** Pasos del nuevo ritual, separados por |. */
    val stepsCsv: String,
    val updatedAtMillis: Long = System.currentTimeMillis()
) {
    val steps: List<String>
        get() = stepsCsv.split('|').map { it.trim() }.filter { it.isNotEmpty() }
}
