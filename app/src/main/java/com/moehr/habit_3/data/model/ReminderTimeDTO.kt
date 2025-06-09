package com.moehr.habit_3.data.model

import java.time.LocalTime

data class ReminderTimeDTO(
    val hour: Int,
    val minute: Int,
) {
    fun toLocalTime(): LocalTime = LocalTime.of(hour, minute)

    companion object {
        fun fromLocalTime(time: LocalTime): ReminderTimeDTO {
            return ReminderTimeDTO(time.hour, time.minute)
        }
    }
}