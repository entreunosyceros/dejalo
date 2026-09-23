package com.dejalo.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dejalo.app.data.QuitRepository
import com.dejalo.app.data.local.entity.UserProfileEntity
import com.dejalo.app.ui.onboarding.OnboardingViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

data class SettingsUiState(
    val loading: Boolean = true,
    val quitDateTime: LocalDateTime = LocalDateTime.now(),
    val cigarettesPerDay: String = "",
    val packPrice: String = "",
    val cigarettesPerPack: String = "",
    val selectedMotivators: Set<String> = emptySet(),
    val customMotivator: String = "",
    val savingsGoalLabel: String = "",
    val savingsGoalEuros: String = "",
    val saving: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false
)

class SettingsViewModel(
    private val repository: QuitRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val profile = repository.observeProfile().first()
            if (profile == null) {
                _state.update { it.copy(loading = false, error = "No hay perfil guardado.") }
                return@launch
            }
            _state.update { profile.toUiState() }
        }
    }

    fun update(block: (SettingsUiState) -> SettingsUiState) {
        _state.update { block(it.copy(saved = false, error = null)) }
    }

    fun toggleMotivator(label: String) {
        _state.update { current ->
            val next = current.selectedMotivators.toMutableSet()
            if (!next.add(label)) next.remove(label)
            current.copy(selectedMotivators = next, saved = false, error = null)
        }
    }

    fun save(onDone: () -> Unit) {
        val s = _state.value
        val cigsDay = s.cigarettesPerDay.replace(',', '.').toDoubleOrNull()
        val price = s.packPrice.replace(',', '.').toDoubleOrNull()
        val perPack = s.cigarettesPerPack.toIntOrNull()
        if (cigsDay == null || cigsDay <= 0 || price == null || price <= 0 || perPack == null || perPack <= 0) {
            _state.update { it.copy(error = "Revisa las métricas de consumo y precio.") }
            return
        }
        if (s.selectedMotivators.isEmpty() && s.customMotivator.isBlank()) {
            _state.update { it.copy(error = "Elige al menos un motivador.") }
            return
        }
        if (s.quitDateTime.isAfter(LocalDateTime.now())) {
            _state.update { it.copy(error = "La fecha/hora no puede ser futura.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(saving = true, error = null) }
            val quitAt = s.quitDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            repository.saveOnboarding(
                UserProfileEntity(
                    quitAtMillis = quitAt,
                    cigarettesPerDay = cigsDay,
                    packPriceEuros = price,
                    cigarettesPerPack = perPack,
                    motivatorsCsv = s.selectedMotivators.joinToString("|"),
                    customMotivator = s.customMotivator.trim(),
                    savingsGoalEuros = s.savingsGoalEuros.replace(',', '.').toDoubleOrNull() ?: 0.0,
                    savingsGoalLabel = s.savingsGoalLabel.trim(),
                    onboardingCompleted = true
                )
            )
            _state.update { it.copy(saving = false, saved = true) }
            onDone()
        }
    }

    companion object {
        val motivatorOptions = OnboardingViewModel.motivatorOptions

        fun factory(repository: QuitRepository) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SettingsViewModel(repository) as T
            }
        }
    }
}

private fun UserProfileEntity.toUiState(): SettingsUiState {
    val known = OnboardingViewModel.motivatorOptions.toSet()
    val fromCsv = motivatorsCsv.split('|')
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .map { if (it == "Ejemplo") "Ser un ejemplo" else it }
    val selected = fromCsv.filter { it in known }.toSet()
    val customFromCsv = fromCsv.filter { it !in known }
    val custom = customMotivator.ifBlank { customFromCsv.joinToString(", ") }
    return SettingsUiState(
        loading = false,
        quitDateTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(quitAtMillis),
            ZoneId.systemDefault()
        ),
        cigarettesPerDay = formatNumber(cigarettesPerDay),
        packPrice = formatNumber(packPriceEuros),
        cigarettesPerPack = cigarettesPerPack.toString(),
        selectedMotivators = selected.ifEmpty { setOf("Salud") },
        customMotivator = custom,
        savingsGoalLabel = savingsGoalLabel,
        savingsGoalEuros = if (savingsGoalEuros > 0) formatNumber(savingsGoalEuros) else ""
    )
}

private fun formatNumber(value: Double): String {
    return if (value == value.toLong().toDouble()) {
        value.toLong().toString()
    } else {
        value.toString().trimEnd('0').trimEnd('.')
    }
}
