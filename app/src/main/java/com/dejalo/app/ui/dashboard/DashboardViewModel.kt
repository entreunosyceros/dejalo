package com.dejalo.app.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dejalo.app.data.QuitRepository
import com.dejalo.app.data.local.entity.BadgeEntity
import com.dejalo.app.data.local.entity.UserProfileEntity
import com.dejalo.app.domain.LiveQuitStats
import com.dejalo.app.domain.craving.CravingAnalyzer
import com.dejalo.app.domain.craving.CravingRiskDetector
import com.dejalo.app.domain.craving.CravingSummary
import com.dejalo.app.domain.craving.RiskAlert
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DashboardUiState(
    val profile: UserProfileEntity? = null,
    val stats: LiveQuitStats? = null,
    val badges: List<BadgeEntity> = emptyList(),
    val cravingSummary: CravingSummary? = null,
    val riskAlert: RiskAlert? = null,
    val loading: Boolean = true
)

class DashboardViewModel(
    private val repository: QuitRepository
) : ViewModel() {

    val state: StateFlow<DashboardUiState> = combine(
        repository.observeProfile(),
        repository.observeLiveStats(),
        repository.observeBadges(),
        repository.observeCravings()
    ) { profile, stats, badges, cravings ->
        DashboardUiState(
            profile = profile,
            stats = stats,
            badges = badges,
            cravingSummary = CravingAnalyzer.summarize(cravings),
            riskAlert = CravingRiskDetector.alertForNow(cravings),
            loading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DashboardUiState()
    )

    init {
        viewModelScope.launch {
            repository.observeLiveStats()
                .distinctUntilChangedBy { stats ->
                    stats?.let {
                        listOf(it.days, it.moneySavedEuros.toInt() / 10, it.cigarettesAvoided.toInt() / 50)
                    }
                }
                .collect { stats ->
                    stats?.let { repository.evaluateBadges(it) }
                }
        }
    }

    companion object {
        fun factory(repository: QuitRepository) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DashboardViewModel(repository) as T
            }
        }
    }
}
