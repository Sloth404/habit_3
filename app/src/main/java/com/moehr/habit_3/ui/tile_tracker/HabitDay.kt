package com.moehr.habit_3.ui.tile_tracker

import java.time.LocalDate

/**
 * Represents a single day in the habit tracking calendar.
 *
 * @property date The calendar date for this day.
 * @property isBeforeStart True if the date is before the habit's creation date.
 * @property isSuccess True if the habit was successfully completed on this date.
 * @property isToday True if the date is the current day.
 * @property isCreatedAt True if the date matches the habit's creation date.
 */
data class HabitDay(
    val date: LocalDate,
    val isBeforeStart: Boolean,
    val isSuccess: Boolean,
    val isPending: Boolean,
    val isToday: Boolean,
    val isCreatedAt: Boolean
)
