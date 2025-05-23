package gov.rr.iteraima.data.model

import java.util.Date

data class Schedule(
    val id: Long = 0,
    val department: Department,
    val title: String,
    val description: String,
    val date: Date,
    val startTime: String,
    val endTime: String,
    val isAvailable: Boolean = true,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) {
    fun isTimeSlotAvailable(): Boolean {
        if (!isAvailable) return false
        
        // Add additional business logic here
        // For example, check if the current time is before the schedule time
        val currentDate = Date()
        return date.after(currentDate)
    }

    companion object {
        const val MAX_APPOINTMENTS_PER_DAY = 8
        const val MIN_SCHEDULE_NOTICE_HOURS = 24
    }
}
