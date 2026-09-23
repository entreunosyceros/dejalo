package com.dejalo.app.ui.routines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dejalo.app.data.QuitRepository
import com.dejalo.app.data.local.entity.AlternativeRoutineEntity
import com.dejalo.app.domain.routines.AlternativeRoutineCatalog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RoutineEditorState(
    val editingId: Long? = null,
    val situation: String = AlternativeRoutineCatalog.situations.first(),
    val oldPattern: String = "",
    val stepsText: String = "",
    val showEditor: Boolean = false,
    val error: String? = null
)

data class RoutinesUiState(
    val routines: List<AlternativeRoutineEntity> = emptyList(),
    val editor: RoutineEditorState = RoutineEditorState(),
    val loading: Boolean = true
)

class RoutinesViewModel(
    private val repository: QuitRepository
) : ViewModel() {

    private val editor = MutableStateFlow(RoutineEditorState())

    val state: StateFlow<RoutinesUiState> = combine(
        repository.observeRoutines(),
        editor
    ) { routines, ed ->
        RoutinesUiState(routines = routines, editor = ed, loading = false)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RoutinesUiState()
    )

    fun openNew() {
        openNewFor(AlternativeRoutineCatalog.situations.first())
    }

    fun openNewFor(situation: String) {
        val resolved = AlternativeRoutineCatalog.situations
            .firstOrNull { it.equals(situation, ignoreCase = true) }
            ?: situation
        val suggestion = AlternativeRoutineCatalog.suggestionFor(resolved)
        editor.value = RoutineEditorState(
            showEditor = true,
            situation = resolved,
            oldPattern = suggestion.oldPattern,
            stepsText = suggestion.suggestedSteps.joinToString("\n")
        )
    }

    fun openEdit(routine: AlternativeRoutineEntity) {
        editor.value = RoutineEditorState(
            editingId = routine.id,
            showEditor = true,
            situation = routine.situation,
            oldPattern = routine.oldPattern,
            stepsText = routine.steps.joinToString("\n")
        )
    }

    fun closeEditor() {
        editor.update { it.copy(showEditor = false, error = null) }
    }

    fun setSituation(situation: String) {
        val suggestion = AlternativeRoutineCatalog.suggestionFor(situation)
        editor.update {
            // Solo rellenar sugerencia si es alta nueva o el patrón sigue vacío / genérico
            val keepCustom = it.editingId != null
            it.copy(
                situation = situation,
                oldPattern = if (keepCustom && it.oldPattern.isNotBlank()) it.oldPattern else suggestion.oldPattern,
                stepsText = if (keepCustom && it.stepsText.isNotBlank()) {
                    it.stepsText
                } else {
                    suggestion.suggestedSteps.joinToString("\n")
                },
                error = null
            )
        }
    }

    fun setOldPattern(value: String) {
        editor.update { it.copy(oldPattern = value, error = null) }
    }

    fun setStepsText(value: String) {
        editor.update { it.copy(stepsText = value, error = null) }
    }

    fun applySuggestion() {
        val s = editor.value.situation
        val suggestion = AlternativeRoutineCatalog.suggestionFor(s)
        editor.update {
            it.copy(
                oldPattern = suggestion.oldPattern,
                stepsText = suggestion.suggestedSteps.joinToString("\n"),
                error = null
            )
        }
    }

    fun save() {
        val ed = editor.value
        val steps = ed.stepsText
            .lines()
            .map { it.trim().removePrefix("•").removePrefix("-").trim() }
            .filter { it.isNotEmpty() }
        if (ed.situation.isBlank()) {
            editor.update { it.copy(error = "Elige una situación.") }
            return
        }
        if (steps.isEmpty()) {
            editor.update { it.copy(error = "Añade al menos un paso del nuevo ritual.") }
            return
        }
        viewModelScope.launch {
            repository.saveRoutine(
                AlternativeRoutineEntity(
                    id = ed.editingId ?: 0,
                    situation = ed.situation.trim(),
                    oldPattern = ed.oldPattern.trim().ifBlank {
                        "${ed.situation.trim()} → cigarrillo"
                    },
                    stepsCsv = steps.joinToString("|")
                )
            )
            editor.update { it.copy(showEditor = false, error = null) }
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch {
            repository.deleteRoutine(id)
            if (editor.value.editingId == id) closeEditor()
        }
    }

    companion object {
        fun factory(repository: QuitRepository) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return RoutinesViewModel(repository) as T
            }
        }
    }
}
