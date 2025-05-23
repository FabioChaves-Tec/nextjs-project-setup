package gov.rr.iteraima.data.model

import java.util.Date

data class Appointment(
    val id: Long = 0,
    val scheduleId: Long,
    val department: Department,
    val userName: String,
    val userEmail: String,
    val userPhone: String,
    val appointmentDate: Date,
    val startTime: String,
    val endTime: String,
    val subject: String,
    val description: String,
    val status: AppointmentStatus = AppointmentStatus.PENDING,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

enum class AppointmentStatus {
    PENDING,    // Aguardando aprovação
    APPROVED,   // Aprovado
    REJECTED,   // Rejeitado
    CANCELLED,  // Cancelado
    COMPLETED   // Concluído
}

data class AppointmentRequest(
    val userName: String,
    val userEmail: String,
    val userPhone: String,
    val department: Department,
    val subject: String,
    val description: String,
    val preferredDate: Date,
    val preferredTime: String
)
