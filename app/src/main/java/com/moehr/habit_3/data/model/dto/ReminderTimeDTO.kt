package com.moehr.habit_3.data.model.dto

import java.io.Serializable
import java.time.LocalTime

/**
 * Data Transfer Object representing a reminder time.
 *
 * @property hour The hour part of the reminder (0-23).
 * @property minute The minute part of the reminder (0-59).
 */
data class ReminderTimeDTO(
    val hour: Int,
    val minute: Int,
) : Serializable {

    /**
     * Converts this DTO to a [LocalTime] instance.
     *
     * @return LocalTime corresponding to the stored hour and minute.
     */
    fun toLocalTime(): LocalTime = LocalTime.of(hour, minute)

    companion object {
        /**
         * Creates a [ReminderTimeDTO] from a [LocalTime] instance.
         *
         * @param time LocalTime to convert.
         * @return Corresponding ReminderTimeDTO.
         */
        fun fromLocalTime(time: LocalTime): ReminderTimeDTO {
            return ReminderTimeDTO(time.hour, time.minute)
        }
    }
}
