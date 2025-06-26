package com.moehr.habit_3.data.model.dto

import java.io.Serializable
import java.time.LocalDateTime

/**
 * Data Transfer Object representing a single log entry for a habit.
 *
 * @property date The date and time when the habit was logged.
 */
data class HabitLogEntryDTO(
    val date: LocalDateTime,
) : Serializable

