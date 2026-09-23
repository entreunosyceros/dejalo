package com.dejalo.app.ui.emergency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dejalo.app.data.QuitRepository
import com.dejalo.app.ui.emergency.games.CrisisGame
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class BreathPhase(val label: String, val seconds: Int) {
    INHALE("Inhala", 4),
    HOLD("Mantén", 7),
    EXHALE("Exhala", 8)
}

enum class EmergencyPhase {
    /** Eligiendo intensidad inicial y herramientas. */
    ACTIVE,
    /** Midiendo intensidad final antes de guardar. */
    WRAP_UP,
    /** Episodio guardado. */
    SAVED
}

data class EmergencyUiState(
    val crisisRemainingSec: Int = 240,
    val crisisRunning: Boolean = false,
    val phase: BreathPhase = BreathPhase.INHALE,
    val phaseRemaining: Int = BreathPhase.INHALE.seconds,
    val breathingActive: Boolean = false,
    val distractionCard: String = "",
    val selectedTrigger: String = "Estrés",
    val selectedGame: CrisisGame? = null,
    val gamesPlayed: Int = 0,
    val intensityInitial: Int = 7,
    val intensityFinal: Int = 3,
    val usedBreathing: Boolean = false,
    val usedGame: Boolean = false,
    val usedCard: Boolean = false,
    /** Claves concretas de juegos usados (burbujas, memoria, …). */
    val gameToolKeys: Set<String> = emptySet(),
    val flowPhase: EmergencyPhase = EmergencyPhase.ACTIVE,
    val lastDrop: Int? = null
)

class EmergencyViewModel(
    private val repository: QuitRepository,
    private val motivators: List<String>
) : ViewModel() {

    private val _state = MutableStateFlow(
        EmergencyUiState(distractionCard = pickDistractionCard(motivators))
    )
    val state: StateFlow<EmergencyUiState> = _state.asStateFlow()

    private var crisisJob: Job? = null
    private var breathJob: Job? = null
    private var startedAt = 0L

    fun setIntensityInitial(value: Int) {
        _state.update { it.copy(intensityInitial = value.coerceIn(0, 10)) }
    }

    fun setIntensityFinal(value: Int) {
        _state.update { it.copy(intensityFinal = value.coerceIn(0, 10)) }
    }

    fun startCrisis() {
        crisisJob?.cancel()
        breathJob?.cancel()
        startedAt = System.currentTimeMillis()
        _state.update {
            it.copy(
                crisisRemainingSec = 240,
                crisisRunning = true,
                flowPhase = EmergencyPhase.ACTIVE,
                lastDrop = null,
                breathingActive = false
            )
        }
        crisisJob = viewModelScope.launch {
            while (_state.value.crisisRemainingSec > 0) {
                delay(1000)
                _state.update { it.copy(crisisRemainingSec = it.crisisRemainingSec - 1) }
            }
            _state.update { it.copy(crisisRunning = false) }
        }
    }

    fun startBreathing() {
        breathJob?.cancel()
        _state.update {
            it.copy(
                breathingActive = true,
                usedBreathing = true,
                phase = BreathPhase.INHALE,
                phaseRemaining = BreathPhase.INHALE.seconds
            )
        }
        if (startedAt == 0L) startedAt = System.currentTimeMillis()
        if (!_state.value.crisisRunning) startCrisisTimerOnly()
        breathJob = viewModelScope.launch {
            var phase = BreathPhase.INHALE
            while (true) {
                for (sec in phase.seconds downTo 1) {
                    _state.update { it.copy(phase = phase, phaseRemaining = sec) }
                    delay(1000)
                }
                phase = when (phase) {
                    BreathPhase.INHALE -> BreathPhase.HOLD
                    BreathPhase.HOLD -> BreathPhase.EXHALE
                    BreathPhase.EXHALE -> BreathPhase.INHALE
                }
            }
        }
    }

    fun stopBreathing() {
        breathJob?.cancel()
        _state.update { it.copy(breathingActive = false) }
    }

    fun shuffleDistraction() {
        _state.update {
            it.copy(
                distractionCard = pickDistractionCard(motivators, avoid = it.distractionCard),
                usedCard = true
            )
        }
    }

    fun selectTrigger(trigger: String) {
        _state.update { it.copy(selectedTrigger = trigger) }
    }

    fun selectGame(game: CrisisGame?) {
        _state.update { it.copy(selectedGame = game) }
        if (game != null) {
            stopBreathing()
            if (!_state.value.crisisRunning) {
                startCrisis()
            }
        }
    }

    fun onGameEngaged() {
        if (startedAt == 0L) {
            startedAt = System.currentTimeMillis()
        }
        val gameKey = _state.value.selectedGame?.toolKey
        _state.update {
            it.copy(
                gamesPlayed = it.gamesPlayed + 1,
                usedGame = true,
                gameToolKeys = if (gameKey != null) it.gameToolKeys + gameKey else it.gameToolKeys
            )
        }
        if (!_state.value.crisisRunning) {
            startCrisisTimerOnly()
        }
    }

    private fun startCrisisTimerOnly() {
        if (_state.value.crisisRunning) return
        crisisJob?.cancel()
        _state.update { it.copy(crisisRemainingSec = 240, crisisRunning = true) }
        crisisJob = viewModelScope.launch {
            while (_state.value.crisisRemainingSec > 0) {
                delay(1000)
                _state.update { it.copy(crisisRemainingSec = it.crisisRemainingSec - 1) }
            }
            _state.update { it.copy(crisisRunning = false) }
        }
    }

    /** Pasa a preguntar intensidad final (o guarda si ya estábamos en WRAP_UP). */
    fun requestWrapUpOrSave() {
        when (_state.value.flowPhase) {
            EmergencyPhase.ACTIVE -> {
                stopBreathing()
                crisisJob?.cancel()
                // Sugerir final un poco por debajo del inicial si no se ha tocado.
                val suggested = (_state.value.intensityInitial - 3).coerceIn(0, 10)
                _state.update {
                    it.copy(
                        crisisRunning = false,
                        breathingActive = false,
                        selectedGame = null,
                        flowPhase = EmergencyPhase.WRAP_UP,
                        intensityFinal = suggested
                    )
                }
            }
            EmergencyPhase.WRAP_UP -> saveEpisode()
            EmergencyPhase.SAVED -> Unit
        }
    }

    private fun saveEpisode() {
        viewModelScope.launch {
            val s = _state.value
            val duration = if (startedAt > 0) {
                ((System.currentTimeMillis() - startedAt) / 1000).toInt()
            } else {
                (240 - s.crisisRemainingSec).coerceAtLeast(0)
            }
            val tools = buildList {
                if (s.usedBreathing) add("respiracion")
                if (s.usedCard) add("motivadores")
                if (s.gameToolKeys.isNotEmpty()) {
                    addAll(s.gameToolKeys)
                } else if (s.usedGame) {
                    add("juego")
                }
            }
            val drop = s.intensityInitial - s.intensityFinal
            repository.logCraving(
                trigger = s.selectedTrigger,
                durationSeconds = duration,
                notes = "ansia superada",
                intensityInitial = s.intensityInitial,
                intensityFinal = s.intensityFinal,
                tools = tools
            )
            crisisJob?.cancel()
            breathJob?.cancel()
            _state.update {
                it.copy(
                    crisisRunning = false,
                    breathingActive = false,
                    selectedGame = null,
                    flowPhase = EmergencyPhase.SAVED,
                    lastDrop = drop
                )
            }
        }
    }

    fun resetForNewEpisode() {
        startedAt = 0L
        _state.update {
            EmergencyUiState(
                distractionCard = pickDistractionCard(motivators),
                intensityInitial = 7,
                intensityFinal = 3
            )
        }
    }

    companion object {
        val triggers = listOf("Estrés", "Café", "Alcohol", "Entorno social", "Aburrimiento", "Otro")

        /** Frases de refuerzo (nunca etiquetas cortas tipo «Salud»). */
        private val defaultPhrases = listOf(
            "Este impulso dura minutos. Tú puedes más.",
            "Respira. Ya has llegado hasta aquí.",
            "Un cigarrillo no borra el esfuerzo, pero sí frena tu racha.",
            "Bebe agua. Camina un poco. El pico pasa.",
            "Juega un minuto. El ansia no puede gritar tanto si estás ocupado.",
            "No tienes que dejarlo para siempre ahora: solo no fumes estos cinco minutos.",
            "Has superado picos peores. Este también bajará.",
            "Sal al aire un momento. Cambia de sitio. Rompe el automatismo.",
            "Cuenta hasta veinte despacio. Luego decide otra vez.",
            "Tu cuerpo ya está recuperándose. No interrumpas el proceso ahora.",
            "El ansia es una ola: sube, cresta y baja. Tú flotas.",
            "Mira el reloj: dentro de un rato esto será solo un recuerdo.",
            "Puedes aguantar. Ya lo estás haciendo.",
            "Cada minuto sin fumar suma. Este también cuenta.",
            "Si dudas, elige el no. El sí puede esperar."
        )

        private val motivatorPhrases = mapOf(
            "Salud" to listOf(
                "Tu salud está mejorando cada día limpio. Protégela ahora.",
                "Oxígeno, pulso, gusto: tu cuerpo nota cuando no fumas."
            ),
            "Ahorro" to listOf(
                "Ese cigarrillo también es dinero que puedes guardar para tu meta.",
                "Hoy el ahorro sigue creciendo. No lo frenes con un impulso."
            ),
            "Familia" to listOf(
                "Piensa en quien te importa: merece tu versión sin humo.",
                "Estás eligiendo estar más presente. Sigue así un poco más."
            ),
            "Ser un ejemplo" to listOf(
                "Ser un ejemplo empieza en este minuto: no fumes ahora.",
                "Alguien te mira. Muéstrale que el impulso se puede dejar pasar."
            ),
            "Ejemplo" to listOf(
                "Ser un ejemplo empieza en este minuto: no fumes ahora."
            ),
            "Rendimiento" to listOf(
                "Concentración y energía mejoran sin nicotina. Defiende ese avance.",
                "Un cigarrillo no te da rendimiento: te lo cobra después."
            ),
            "Libertad" to listOf(
                "Libertad es poder decir no cuando el ansia dice sí.",
                "No dependes de este cigarrillo. Demuéstratelo ahora."
            )
        )

        private fun phrasesForMotivators(motivators: List<String>): List<String> =
            motivators.flatMap { key ->
                motivatorPhrases[key]
                    ?: listOfNotNull(
                        key.takeIf { it.length > 12 }?.let { "Recuerda por qué lo dejas: $it" }
                    )
            }

        fun pickDistractionCard(
            motivators: List<String>,
            avoid: String? = null
        ): String {
            val pool = (phrasesForMotivators(motivators) + defaultPhrases).distinct()
            val candidates = if (avoid != null && pool.size > 1) {
                pool.filter { it != avoid }
            } else {
                pool
            }
            return candidates.random()
        }

        fun factory(repository: QuitRepository, motivators: List<String>) =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return EmergencyViewModel(repository, motivators) as T
                }
            }
    }
}
