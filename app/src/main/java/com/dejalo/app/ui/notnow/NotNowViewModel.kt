package com.dejalo.app.ui.notnow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dejalo.app.data.QuitRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class NotNowPhase {
    READY,
    COUNTING,
    CHECK_IN,
    DONE
}

data class NotNowUiState(
    val phase: NotNowPhase = NotNowPhase.READY,
    val remainingSec: Int = 300,
    val intensityAfter: Int = 3,
    val intensityBefore: Int = 7
)

class NotNowViewModel(
    private val repository: QuitRepository
) : ViewModel() {

    private val _state = MutableStateFlow(NotNowUiState())
    val state: StateFlow<NotNowUiState> = _state.asStateFlow()

    private var timerJob: Job? = null
    private var startedAt = 0L

    fun setIntensityBefore(value: Int) {
        _state.update { it.copy(intensityBefore = value.coerceIn(0, 10)) }
    }

    fun setIntensityAfter(value: Int) {
        _state.update { it.copy(intensityAfter = value.coerceIn(0, 10)) }
    }

    fun start() {
        timerJob?.cancel()
        startedAt = System.currentTimeMillis()
        _state.update {
            it.copy(phase = NotNowPhase.COUNTING, remainingSec = 300)
        }
        timerJob = viewModelScope.launch {
            while (_state.value.remainingSec > 0) {
                delay(1000)
                _state.update { s ->
                    val next = s.remainingSec - 1
                    if (next <= 0) {
                        s.copy(
                            remainingSec = 0,
                            phase = NotNowPhase.CHECK_IN,
                            intensityAfter = (s.intensityBefore - 2).coerceIn(0, 10)
                        )
                    } else {
                        s.copy(remainingSec = next)
                    }
                }
            }
        }
    }

    fun skipToCheckIn() {
        timerJob?.cancel()
        _state.update {
            it.copy(
                phase = NotNowPhase.CHECK_IN,
                remainingSec = 0,
                intensityAfter = (it.intensityBefore - 2).coerceIn(0, 10)
            )
        }
    }

    fun saveAndFinish(onDone: () -> Unit) {
        viewModelScope.launch {
            val s = _state.value
            val duration = if (startedAt > 0) {
                ((System.currentTimeMillis() - startedAt) / 1000).toInt().coerceAtLeast(1)
            } else {
                300
            }
            repository.logCraving(
                trigger = "Ahora no",
                durationSeconds = duration.coerceAtMost(300),
                notes = "no voy a fumar ahora",
                intensityInitial = s.intensityBefore,
                intensityFinal = s.intensityAfter,
                tools = listOf("ahora_no")
            )
            _state.update { it.copy(phase = NotNowPhase.DONE) }
            onDone()
        }
    }

    fun reset() {
        timerJob?.cancel()
        startedAt = 0L
        _state.value = NotNowUiState()
    }

    companion object {
        fun factory(repository: QuitRepository) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NotNowViewModel(repository) as T
            }
        }
    }
}
