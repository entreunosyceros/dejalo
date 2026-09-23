package com.dejalo.app.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dejalo.app.data.QuitRepository
import com.dejalo.app.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId

data class OnboardingUiState(
    val quitDateTime: LocalDateTime = LocalDateTime.now(),
    val cigarettesPerDay: String = "20",
    val packPrice: String = "5.50",
    val cigarettesPerPack: String = "20",
    val selectedMotivators: Set<String> = setOf("Salud"),
    val customMotivator: String = "",
    val savingsGoalLabel: String = "",
    val savingsGoalEuros: String = "",
    val saving: Boolean = false,
    val error: String? = null
)

class OnboardingViewModel(
    private val repository: QuitRepository
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingUiState())
    val state: StateFlow<OnboardingUiState> = _state.asStateFlow()

    fun update(block: (OnboardingUiState) -> OnboardingUiState) {
        _state.update(block)
    }

    fun toggleMotivator(label: String) {
        _state.update { current ->
            val next = current.selectedMotivators.toMutableSet()
            if (!next.add(label)) next.remove(label)
            current.copy(selectedMotivators = next)
        }
    }

    fun submit(onDone: () -> Unit) {
        val s = _state.value
        val cigsDay = s.cigarettesPerDay.toDoubleOrNull()
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
            _state.update { it.copy(saving = false) }
            onDone()
        }
    }

    companion object {
        val motivatorOptions = listOf(
            "Salud",
            "Ahorro",
            "Familia",
            "Ser un ejemplo",
            "Rendimiento",
            "Libertad"
        )

        fun factory(repository: QuitRepository) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return OnboardingViewModel(repository) as T
            }
        }
    }
}
