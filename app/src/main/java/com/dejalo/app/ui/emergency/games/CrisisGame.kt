package com.dejalo.app.ui.emergency.games

enum class GameMechanism(val label: String) {
    HANDS("Manos ocupadas"),
    MIND("Mente ocupada"),
    WAIT("Esperar"),
    RELAX("Relajación")
}

enum class CrisisGame(
    val title: String,
    val blurb: String,
    val mechanism: GameMechanism,
    /** Clave persistida en toolsCsv para estadísticas «qué me funciona». */
    val toolKey: String
) {
    BUBBLE(
        title = "Rompe el impulso",
        blurb = "Toca las burbujas. Cada toque es un segundo que ganas.",
        mechanism = GameMechanism.HANDS,
        toolKey = "burbujas"
    ),
    REACTION(
        title = "Reacción rápida",
        blurb = "Cuando el círculo se ponga verde, tócalo. Entrena manos y atención.",
        mechanism = GameMechanism.HANDS,
        toolKey = "reaccion"
    ),
    TRACE(
        title = "Traza el camino",
        blurb = "Sigue la línea con el dedo. Ocupa la mano hasta que baje el pico.",
        mechanism = GameMechanism.HANDS,
        toolKey = "traza"
    ),
    MEMORY(
        title = "Memoria libre",
        blurb = "Empareja símbolos. La mente ocupada deja pasar el pico.",
        mechanism = GameMechanism.MIND,
        toolKey = "memoria"
    ),
    NUMBERS(
        title = "Orden numérico",
        blurb = "Toca los números en orden. La mente cuenta; el ansia espera.",
        mechanism = GameMechanism.MIND,
        toolKey = "numeros"
    ),
    HOLD(
        title = "Aguanta la ola",
        blurb = "Mantén pulsado. Entrena esperar a que el ansia baje.",
        mechanism = GameMechanism.WAIT,
        toolKey = "ola"
    ),
    PROGRESSIVE(
        title = "Temporizador progresivo",
        blurb = "Aguanta intervalos cada vez un poco más largos. Solo espera.",
        mechanism = GameMechanism.WAIT,
        toolKey = "progresivo"
    ),
    RHYTHM(
        title = "Ritmo suave",
        blurb = "Toca al compás. Un ritmo constante calma el impulso.",
        mechanism = GameMechanism.RELAX,
        toolKey = "ritmo"
    ),
    FOLLOW(
        title = "Sigue el punto",
        blurb = "Sigue con la mirada (y el dedo) el punto que se mueve.",
        mechanism = GameMechanism.RELAX,
        toolKey = "sigue"
    )
}
