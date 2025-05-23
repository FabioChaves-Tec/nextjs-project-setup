package gov.rr.iteraima.ui.admin.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gov.rr.iteraima.data.model.Department
import gov.rr.iteraima.data.model.Schedule
import gov.rr.iteraima.data.repository.ScheduleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date

class AdminDashboardViewModel(
    private val repository: ScheduleRepository = ScheduleRepository.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState

    private val _selectedDepartment = MutableLiveData<Department>()
    val selectedDepartment: LiveData<Department> = _selectedDepartment

    private val _schedules = MutableLiveData<Map<Department, List<Schedule>>>()
    val schedules: LiveData<Map<Department, List<Schedule>>> = _schedules

    init {
        loadSchedules()
    }

    fun selectDepartment(department: Department) {
        _selectedDepartment.value = department
        loadSchedules(department)
    }

    fun loadSchedules(department: Department? = null) {
        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading

            try {
                val result = repository.getSchedules(department)
                result.fold(
                    onSuccess = { scheduleList ->
                        val groupedSchedules = scheduleList.groupBy { it.department }
                        _schedules.value = groupedSchedules
                        _uiState.value = if (scheduleList.isEmpty()) {
                            DashboardUiState.Empty
                        } else {
                            DashboardUiState.Success(groupedSchedules)
                        }
                    },
                    onFailure = { exception ->
                        _uiState.value = DashboardUiState.Error(
                            exception.message ?: "Erro ao carregar agendas"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = DashboardUiState.Error(
                    "Erro de conexão. Verifique sua internet."
                )
            }
        }
    }

    fun createSchedule(schedule: Schedule) {
        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading

            try {
                val result = repository.createSchedule(schedule)
                result.fold(
                    onSuccess = {
                        loadSchedules(schedule.department)
                    },
                    onFailure = { exception ->
                        _uiState.value = DashboardUiState.Error(
                            exception.message ?: "Erro ao criar agenda"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = DashboardUiState.Error(
                    "Erro de conexão. Verifique sua internet."
                )
            }
        }
    }

    fun updateSchedule(schedule: Schedule) {
        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading

            try {
                val result = repository.updateSchedule(schedule)
                result.fold(
                    onSuccess = {
                        loadSchedules(schedule.department)
                    },
                    onFailure = { exception ->
                        _uiState.value = DashboardUiState.Error(
                            exception.message ?: "Erro ao atualizar agenda"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = DashboardUiState.Error(
                    "Erro de conexão. Verifique sua internet."
                )
            }
        }
    }

    fun deleteSchedule(schedule: Schedule) {
        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading

            try {
                val result = repository.deleteSchedule(schedule.id)
                result.fold(
                    onSuccess = {
                        loadSchedules(schedule.department)
                    },
                    onFailure = { exception ->
                        _uiState.value = DashboardUiState.Error(
                            exception.message ?: "Erro ao excluir agenda"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = DashboardUiState.Error(
                    "Erro de conexão. Verifique sua internet."
                )
            }
        }
    }

    sealed class DashboardUiState {
        object Loading : DashboardUiState()
        object Empty : DashboardUiState()
        data class Success(val schedules: Map<Department, List<Schedule>>) : DashboardUiState()
        data class Error(val message: String) : DashboardUiState()
    }
}
