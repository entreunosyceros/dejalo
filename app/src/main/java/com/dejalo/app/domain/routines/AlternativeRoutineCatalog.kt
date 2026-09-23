package com.dejalo.app.domain.routines

/**
 * Situaciones típicas donde el cigarrillo era el ritual automático,
 * y sugerencias de pasos para sustituirlo.
 */
data class RoutineSuggestion(
    val situation: String,
    val oldPattern: String,
    val suggestedSteps: List<String>
)

object AlternativeRoutineCatalog {

    val situations: List<String> = listOf(
        "Café",
        "Estrés",
        "Alcohol",
        "Después de comer",
        "Al levantarse",
        "Durante el trabajo",
        "Al salir a la calle",
        "Hablar por teléfono",
        "Entorno social",
        "Aburrimiento",
        "Otro"
    )

    val suggestions: List<RoutineSuggestion> = listOf(
        RoutineSuggestion(
            situation = "Café",
            oldPattern = "Café → cigarrillo",
            suggestedSteps = listOf("Café", "Beber un vaso de agua", "2 min de minijuego", "Seguir con el café")
        ),
        RoutineSuggestion(
            situation = "Estrés",
            oldPattern = "Estrés → cigarrillo",
            suggestedSteps = listOf("Parar 10 segundos", "Respiración 4-7-8", "Beber agua", "Anotar qué me estresa")
        ),
        RoutineSuggestion(
            situation = "Alcohol",
            oldPattern = "Alcohol → cigarrillo",
            suggestedSteps = listOf("Cambiar de sitio", "Beber agua", "Hablar con alguien", "Esperar 4 minutos")
        ),
        RoutineSuggestion(
            situation = "Después de comer",
            oldPattern = "Comer → cigarrillo",
            suggestedSteps = listOf("Levantarme de la mesa", "Lavar los dientes o enjuague", "Paseo corto de 3 minutos")
        ),
        RoutineSuggestion(
            situation = "Al levantarse",
            oldPattern = "Despertar → cigarrillo",
            suggestedSteps = listOf("Abrir la ventana", "Beber agua", "Estirar 1 minuto", "Ducha o lavarse la cara")
        ),
        RoutineSuggestion(
            situation = "Durante el trabajo",
            oldPattern = "Pausa → cigarrillo",
            suggestedSteps = listOf("Pausa de estiramientos", "Beber agua", "1 min de respiración", "Volver a la tarea")
        ),
        RoutineSuggestion(
            situation = "Al salir a la calle",
            oldPattern = "Salir → cigarrillo",
            suggestedSteps = listOf("Chicle o caramelo", "Caminar 2 manzanas", "Mirar el móvil / motivadores")
        ),
        RoutineSuggestion(
            situation = "Hablar por teléfono",
            oldPattern = "Teléfono → cigarrillo",
            suggestedSteps = listOf("Hablar de pie o caminando", "Garabatear en un papel", "Beber agua al colgar")
        ),
        RoutineSuggestion(
            situation = "Entorno social",
            oldPattern = "Grupo fumando → cigarrillo",
            suggestedSteps = listOf("Alejarse un momento", "Beber algo sin alcohol si puedo", "Mensaje a alguien de apoyo")
        ),
        RoutineSuggestion(
            situation = "Aburrimiento",
            oldPattern = "Aburrimiento → cigarrillo",
            suggestedSteps = listOf("Minijuego 60 s", "Cambiar de actividad", "Leer mis motivadores")
        ),
        RoutineSuggestion(
            situation = "Otro",
            oldPattern = "Situación → cigarrillo",
            suggestedSteps = listOf("Respirar 4-7-8", "Beber agua", "Esperar 4 minutos", "Si sigue, repetir o registrar")
        )
    )

    fun suggestionFor(situation: String): RoutineSuggestion =
        suggestions.firstOrNull { it.situation.equals(situation, ignoreCase = true) }
            ?: RoutineSuggestion(
                situation = situation,
                oldPattern = "$situation → cigarrillo",
                suggestedSteps = listOf("Respirar 4-7-8", "Beber agua", "Esperar 4 minutos")
            )
}
