package com.dejalo.app.ui.util

import java.util.Locale
import kotlin.math.floor

fun formatEuros(value: Double): String =
    String.format(Locale("es", "ES"), "%.2f €", value)

fun formatCigarettes(value: Double): String {
    val whole = floor(value).toInt()
    return if (value - whole < 0.05) whole.toString() else String.format(Locale.US, "%.1f", value)
}

fun formatDurationParts(days: Int, hours: Int, minutes: Int, seconds: Int): String =
    "%d d  %02d:%02d:%02d".format(days, hours, minutes, seconds)

/** Días enteros para métricas de racha / progreso (p. ej. «3 días»). */
fun formatWholeDays(days: Double): String {
    val whole = floor(days).toInt().coerceAtLeast(0)
    return if (whole == 1) "1 día" else "$whole días"
}

fun formatLifeRegained(minutes: Double): String {
    val total = minutes.toLong()
    val days = total / (60 * 24)
    val hours = (total % (60 * 24)) / 60
    val mins = total % 60
    return when {
        days > 0 -> "${days}d ${hours}h"
        hours > 0 -> "${hours}h ${mins}m"
        else -> "${mins} min"
    }
}
