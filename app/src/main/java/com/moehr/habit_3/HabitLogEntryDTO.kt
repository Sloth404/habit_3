package com.moehr.habit_3

import java.time.LocalDateTime

data class HabitLogEntryDTO (
    val date: LocalDateTime,
    val success: Boolean,
)