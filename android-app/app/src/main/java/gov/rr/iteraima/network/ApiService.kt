package gov.rr.iteraima.network

import gov.rr.iteraima.data.model.Appointment
import gov.rr.iteraima.data.model.AppointmentRequest
import gov.rr.iteraima.data.model.Department
import gov.rr.iteraima.data.model.Schedule
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // Admin Authentication
    @POST("auth/login")
    suspend fun login(
        @Body credentials: Map<String, String>
    ): Response<AuthResponse>

    // Schedule Management (Admin)
    @GET("schedules")
    suspend fun getSchedules(
        @Query("department") department: Department? = null,
        @Query("date") date: String? = null
    ): Response<List<Schedule>>

    @POST("schedules")
    suspend fun createSchedule(
        @Body schedule: Schedule
    ): Response<Schedule>

    @PUT("schedules/{id}")
    suspend fun updateSchedule(
        @Path("id") id: Long,
        @Body schedule: Schedule
    ): Response<Schedule>

    @DELETE("schedules/{id}")
    suspend fun deleteSchedule(
        @Path("id") id: Long
    ): Response<Unit>

    // Appointments (User)
    @GET("appointments")
    suspend fun getAppointments(
        @Query("department") department: Department? = null,
        @Query("status") status: String? = null
    ): Response<List<Appointment>>

    @POST("appointments")
    suspend fun requestAppointment(
        @Body request: AppointmentRequest
    ): Response<Appointment>

    @PUT("appointments/{id}/status")
    suspend fun updateAppointmentStatus(
        @Path("id") id: Long,
        @Body status: Map<String, String>
    ): Response<Appointment>
}

data class AuthResponse(
    val token: String,
    val user: AdminUser
)

data class AdminUser(
    val id: Long,
    val name: String,
    val email: String,
    val department: Department,
    val role: String
)
