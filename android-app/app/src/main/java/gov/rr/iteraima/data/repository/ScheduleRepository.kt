package gov.rr.iteraima.data.repository

import gov.rr.iteraima.data.model.Appointment
import gov.rr.iteraima.data.model.AppointmentRequest
import gov.rr.iteraima.data.model.Department
import gov.rr.iteraima.data.model.Schedule
import gov.rr.iteraima.network.AuthResponse
import gov.rr.iteraima.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ScheduleRepository {
    private val apiService = RetrofitClient.apiService
    private val authenticatedApiService get() = RetrofitClient.getAuthenticatedApiService()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("pt", "BR"))

    // Admin Authentication
    suspend fun login(email: String, password: String): Result<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.login(mapOf(
                "email" to email,
                "password" to password
            ))

            if (response.isSuccessful && response.body() != null) {
                // Store the token for subsequent authenticated requests
                response.body()?.token?.let { RetrofitClient.setAuthToken(it) }
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Authentication failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Schedule Management (Admin)
    suspend fun getSchedules(department: Department? = null, date: Date? = null): Result<List<Schedule>> = 
        withContext(Dispatchers.IO) {
            try {
                val dateStr = date?.let { dateFormat.format(it) }
                val response = authenticatedApiService.getSchedules(department, dateStr)

                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to fetch schedules: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun createSchedule(schedule: Schedule): Result<Schedule> = withContext(Dispatchers.IO) {
        try {
            val response = authenticatedApiService.createSchedule(schedule)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to create schedule: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateSchedule(schedule: Schedule): Result<Schedule> = withContext(Dispatchers.IO) {
        try {
            val response = authenticatedApiService.updateSchedule(schedule.id, schedule)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to update schedule: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteSchedule(scheduleId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = authenticatedApiService.deleteSchedule(scheduleId)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete schedule: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Appointments (User)
    suspend fun getAppointments(department: Department? = null): Result<List<Appointment>> = 
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAppointments(department)

                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to fetch appointments: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun requestAppointment(request: AppointmentRequest): Result<Appointment> = 
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.requestAppointment(request)

                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to request appointment: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun updateAppointmentStatus(
        appointmentId: Long, 
        status: String
    ): Result<Appointment> = withContext(Dispatchers.IO) {
        try {
            val response = authenticatedApiService.updateAppointmentStatus(
                appointmentId,
                mapOf("status" to status)
            )

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to update appointment status: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        @Volatile
        private var instance: ScheduleRepository? = null

        fun getInstance() = instance ?: synchronized(this) {
            instance ?: ScheduleRepository().also { instance = it }
        }
    }
}
