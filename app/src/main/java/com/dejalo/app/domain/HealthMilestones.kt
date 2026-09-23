package com.dejalo.app.domain

enum class HealthFactKind {
    /** Cambio fisiológico descrito en literatura de deshabituación. */
    PHYSIOLOGICAL,
    /** Estimación poblacional / motivacional (no predicción individual). */
    ESTIMATE
}

data class HealthMilestone(
    val id: String,
    val title: String,
    val summary: String,
    val explanation: String,
    val source: String,
    val kind: HealthFactKind,
    val targetMillis: Long
)

data class BodyNowStatus(
    val completedCount: Int,
    val totalCount: Int,
    val current: HealthMilestone?,
    val progressToCurrent: Float,
    val justCompleted: HealthMilestone?,
    val headline: String,
    val detail: String
)

object HealthMilestones {
    private const val MINUTE = 60_000L
    private const val HOUR = 3_600_000L
    private const val DAY = 86_400_000L

    /**
     * Hitos alineados con referencias habituales de salud pública
     * (p. ej. CDC / American Lung Association / WHO en materiales de cese).
     * Los tiempos son orientativos a nivel poblacional.
     */
    val all: List<HealthMilestone> = listOf(
        HealthMilestone(
            id = "20m",
            title = "20 minutos",
            summary = "La frecuencia cardíaca y la presión arterial comienzan a volver hacia niveles normales.",
            explanation = "Al dejar de fumar, el sistema cardiovascular deja de recibir el estímulo agudo de la nicotina. " +
                "En los primeros minutos muchos materiales clínicos describen una tendencia a normalizar el pulso y la tensión.",
            source = "Referencias habituales CDC / American Lung Association (línea temporal de cese).",
            kind = HealthFactKind.PHYSIOLOGICAL,
            targetMillis = 20 * MINUTE
        ),
        HealthMilestone(
            id = "8h",
            title = "8 horas",
            summary = "El nivel de monóxido de carbono en sangre desciende de forma notable.",
            explanation = "El CO del humo reduce la capacidad de la sangre para transportar oxígeno. " +
                "Tras varias horas sin fumar, ese nivel suele caer de forma significativa.",
            source = "Referencias habituales CDC / WHO (efectos del monóxido de carbono).",
            kind = HealthFactKind.PHYSIOLOGICAL,
            targetMillis = 8 * HOUR
        ),
        HealthMilestone(
            id = "24h",
            title = "24 horas",
            summary = "El monóxido de carbono se elimina en gran medida del cuerpo.",
            explanation = "En torno al primer día, el CO residual suele haberse depurado de forma importante, " +
                "mejorando la oxigenación respecto al consumo activo.",
            source = "Referencias habituales de línea temporal de recuperación tras dejar de fumar.",
            kind = HealthFactKind.PHYSIOLOGICAL,
            targetMillis = DAY
        ),
        HealthMilestone(
            id = "48h",
            title = "48 horas",
            summary = "Las terminaciones nerviosas empiezan a regenerarse; gusto y olfato pueden mejorar.",
            explanation = "Sin el humo constante, el sentido del gusto y del olfato a menudo se recuperan de forma perceptible " +
                "en los primeros días. La experiencia varía entre personas.",
            source = "Referencias habituales American Lung Association / materiales de cese.",
            kind = HealthFactKind.PHYSIOLOGICAL,
            targetMillis = 2 * DAY
        ),
        HealthMilestone(
            id = "72h",
            title = "72 horas",
            summary = "Los bronquios se relajan y la capacidad pulmonar puede empezar a mejorar.",
            explanation = "En los primeros días sin tabaco, la respiración puede notarse menos forzada. " +
                "La mejora pulmonar plena es gradual y depende de años de consumo y de cada persona.",
            source = "Referencias habituales de recuperación respiratoria tras el cese.",
            kind = HealthFactKind.PHYSIOLOGICAL,
            targetMillis = 3 * DAY
        ),
        HealthMilestone(
            id = "2w",
            title = "2 a 12 semanas",
            summary = "Mejora significativa de la circulación sanguínea.",
            explanation = "Con el tiempo, la circulación suele mejorar y el esfuerzo físico puede resultar menos pesado. " +
                "Es un proceso progresivo, no un interruptor.",
            source = "Referencias habituales CDC / WHO (beneficios cardiovasculares del cese).",
            kind = HealthFactKind.PHYSIOLOGICAL,
            targetMillis = 14 * DAY
        ),
        HealthMilestone(
            id = "1m",
            title = "1 a 9 meses",
            summary = "Reducción de la tos, la congestión y la dificultad respiratoria.",
            explanation = "Los cilios bronquiales se recuperan poco a poco y ayudan a limpiar los pulmones. " +
                "La tos puede aumentar temporalmente mientras el cuerpo limpia; luego suele mejorar.",
            source = "Referencias habituales American Lung Association / CDC.",
            kind = HealthFactKind.PHYSIOLOGICAL,
            targetMillis = 30 * DAY
        ),
        HealthMilestone(
            id = "1y",
            title = "1 año",
            summary = "El riesgo de enfermedad coronaria se reduce hacia la mitad frente a quien sigue fumando.",
            explanation = "Se trata de una estimación epidemiológica a nivel poblacional: describe tendencias en grupos, " +
                "no una predicción médica individual de tu riesgo exacto.",
            source = "Estimaciones poblacionales citadas en materiales de cese (p. ej. CDC / WHO).",
            kind = HealthFactKind.ESTIMATE,
            targetMillis = 365 * DAY
        )
    )

    fun progress(elapsedMillis: Long, milestone: HealthMilestone): Float =
        (elapsedMillis.toFloat() / milestone.targetMillis).coerceIn(0f, 1f)

    fun bodyNow(elapsedMillis: Long): BodyNowStatus {
        val completed = all.filter { progress(elapsedMillis, it) >= 1f }
        val current = all.firstOrNull { progress(elapsedMillis, it) < 1f }
        val justCompleted = completed.lastOrNull()
        val progressToCurrent = current?.let { progress(elapsedMillis, it) } ?: 1f

        val headline = when {
            elapsedMillis <= 0L -> "Aún no has marcado el inicio."
            current == null -> "Has alcanzado todos los hitos de esta lista."
            justCompleted == null -> "Tu cuerpo está en las primeras horas de recuperación."
            else -> "Ahora mismo: camino hacia «${current.title}»."
        }
        val detail = when {
            current == null && justCompleted != null ->
                justCompleted.summary + "\n\n" + justCompleted.explanation
            current != null && justCompleted != null ->
                "Último hito alcanzado: ${justCompleted.title}.\n\n" +
                    "Siguiente: ${current.summary}"
            current != null -> current.explanation
            else -> "Completa el onboarding para ver tu recuperación en vivo."
        }

        return BodyNowStatus(
            completedCount = completed.size,
            totalCount = all.size,
            current = current,
            progressToCurrent = progressToCurrent,
            justCompleted = justCompleted,
            headline = headline,
            detail = detail
        )
    }
}
