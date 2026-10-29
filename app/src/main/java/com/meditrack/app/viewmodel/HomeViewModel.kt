package com.meditrack.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meditrack.app.data.DoseEntity
import com.meditrack.app.data.MediTrackRepository
import com.meditrack.app.data.MedicineEntity
import com.meditrack.app.util.DateTimeUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// UI state for the home dashboard
data class HomeUiState(
    val greeting: String = "",
    val todayDate: String = "",
    val medicines: List<MedicineEntity> = emptyList(),
    val todayDoses: List<DoseEntity> = emptyList(),
    val displayName: String = "",
    val takenCount: Int = 0,
    val remainingCount: Int = 0,
    val isLoading: Boolean = true
)

class HomeViewModel(
    private val repository: MediTrackRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            // Combine medicines and today's doses into a single UI state
            combine(
                repository.getAllMedicines(),
                repository.getDosesForDate(DateTimeUtils.todayIso()),
                repository.displayName
            ) { medicines, doses, name ->
                val activeMedicines = medicines.filter { med ->
                    med.endDate == null || med.endDate!! >= DateTimeUtils.todayIso()
                }
                // Build today's schedule: for each active medicine, for each reminder time,
                // check if a dose was already recorded
                val todaySchedule = activeMedicines.flatMap { med ->
                    med.reminderTimes.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        .map { time ->
                            val dose = doses.find { it.medicineId == med.id && it.reminderTime == time }
                            med to (time to dose)
                        }
                }

                val taken = doses.count { it.status == "TAKEN" }
                val totalScheduled = todaySchedule.size
                val remaining = totalScheduled - taken

                HomeUiState(
                    greeting = DateTimeUtils.getGreeting(),
                    todayDate = DateTimeUtils.formatDisplayDate(DateTimeUtils.todayIso()),
                    medicines = activeMedicines,
                    todayDoses = doses,
                    displayName = name,
                    takenCount = taken,
                    remainingCount = remaining.coerceAtLeast(0),
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    // Mark a dose as Taken or Skipped, with deduplication
    fun recordDose(medicine: MedicineEntity, time: String, status: String) {
        viewModelScope.launch {
            val date = DateTimeUtils.todayIso()
            // Check if already recorded
            val existing = repository.getDose(medicine.id, time, date)
            if (existing != null) return@launch // Already recorded, prevent duplicates

            repository.recordDose(
                DoseEntity(
                    medicineId = medicine.id,
                    medicineName = medicine.name,
                    reminderTime = time,
                    date = date,
                    status = status
                )
            )
        }
    }

    fun loadDemoData() {
        viewModelScope.launch {
            val today = DateTimeUtils.todayIso()
            val samples = listOf(
                MedicineEntity(
                    name = "Paracetamol",
                    dosage = "500mg",
                    instructions = "After meals",
                    startDate = today,
                    endDate = null,
                    reminderTimes = "08:00,20:00"
                ),
                MedicineEntity(
                    name = "Vitamin D",
                    dosage = "1 tablet",
                    instructions = "With breakfast",
                    startDate = today,
                    endDate = null,
                    reminderTimes = "09:00"
                ),
                MedicineEntity(
                    name = "Amoxicillin",
                    dosage = "250mg",
                    instructions = "Every 8 hours",
                    startDate = today,
                    endDate = null,
                    reminderTimes = "08:00,16:00,00:00"
                )
            )
            for (med in samples) {
                repository.insertMedicine(med)
            }
        }
    }
}
