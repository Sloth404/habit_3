package com.moehr.habit_3.data.model.dto

import java.io.Serializable
import java.time.LocalTime

data class ReminderTimeDTO(
    val hour: Int,
    val minute: Int,
) : Serializable {
    fun toLocalTime(): LocalTime = LocalTime.of(hour, minute)

    companion object {
        fun fromLocalTime(time: LocalTime): ReminderTimeDTO {
            return ReminderTimeDTO(time.hour, time.minute)
        }
    }
}