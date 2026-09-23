package com.dejalo.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "relapse_events")
data class RelapseEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val occurredAtMillis: Long,
    val cigarettes: Int = 1,
    val cause: String = "",
    val notes: String = "",
    /** Qué probar la próxima vez (aprendizaje post-recaída). */
    val nextPlan: String = ""
)
