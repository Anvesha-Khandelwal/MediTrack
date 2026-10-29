package com.meditrack.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meditrack.app.data.DoseEntity
import com.meditrack.app.data.MediTrackRepository
import com.meditrack.app.util.DateTimeUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HistoryUiState(
    val doses: List<DoseEntity> = emptyList(),
    val adherencePercent: Int = 0,
    val takenCount: Int = 0,
    val totalCount: Int = 0,
    val selectedDate: String = DateTimeUtils.todayIso(),
    val isLoading: Boolean = true
)

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModel(
    private val repository: MediTrackRepository
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(DateTimeUtils.todayIso())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    // Use flatMapLatest to switch to the new date's data when the date changes.
    // This properly cancels the previous Flow collection when switching dates.
    private val dosesForDate: Flow<List<DoseEntity>> = _selectedDate
        .flatMapLatest { date -> repository.getDosesForDate(date) }

    // Adherence is computed from the last 7 days of data.
    private val adherenceData: Flow<List<DoseEntity>> = flow {
        // Emit periodically — this is a snapshot of the last 7 days
        val startDate = DateTimeUtils.getNDaysAgoIso(7)
        val endDate = DateTimeUtils.todayIso()
        repository.getDosesForRange(startDate, endDate).collect { emit(it) }
    }

    val uiState: StateFlow<HistoryUiState> = combine(dosesForDate, adherenceData) { doses, allDoses ->
        val taken = allDoses.count { it.status == "TAKEN" }
        val total = allDoses.size
        val percent = if (total > 0) (taken * 100 / total) else 0
        HistoryUiState(
            doses = doses,
            adherencePercent = percent,
            takenCount = taken,
            totalCount = total,
            selectedDate = _selectedDate.value,
            isLoading = false
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        HistoryUiState()
    )

    fun setDate(date: String) {
        _selectedDate.value = date
    }

    fun changeDateByDays(delta: Int) {
        val current = DateTimeUtils.parseDate(_selectedDate.value)
        val cal = java.util.Calendar.getInstance()
        cal.time = current
        cal.add(java.util.Calendar.DAY_OF_YEAR, delta)
        _selectedDate.value = DateTimeUtils.formatDate(cal.time)
    }
}
