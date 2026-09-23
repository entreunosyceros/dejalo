package com.dejalo.app.domain

data class BadgeDefinition(
    val id: String,
    val title: String,
    val description: String,
    val predicate: (LiveMetrics) -> Boolean
)

data class LiveMetrics(
    val cleanDays: Double,
    val moneySaved: Double,
    val cigarettesAvoided: Double,
    val consecutiveCleanDays: Double
)

object BadgeCatalog {
    val definitions: List<BadgeDefinition> = listOf(
        BadgeDefinition(
            id = "day_1",
            title = "Primer día",
            description = "1 día limpio",
            predicate = { it.cleanDays >= 1.0 }
        ),
        BadgeDefinition(
            id = "week_1",
            title = "Una semana",
            description = "7 días sin caer",
            predicate = { it.consecutiveCleanDays >= 7.0 }
        ),
        BadgeDefinition(
            id = "saved_100",
            title = "Caja fuerte",
            description = "100 € ahorrados",
            predicate = { it.moneySaved >= 100.0 }
        ),
        BadgeDefinition(
            id = "cigs_500",
            title = "500 menos",
            description = "500 cigarrillos evitados",
            predicate = { it.cigarettesAvoided >= 500.0 }
        ),
        BadgeDefinition(
            id = "month_1",
            title = "Un mes",
            description = "30 días limpios",
            predicate = { it.cleanDays >= 30.0 }
        )
    )
}
