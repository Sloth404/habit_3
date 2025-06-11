package com.moehr.habit_3.ui.tile_tracker

import java.time.LocalDate

data class HabitDay(
    val date: LocalDate,
    val isBeforeStart: Boolean,
    val isSuccess: Boolean,
    val isToday: Boolean,
    val isCreatedAt: Boolean
)
