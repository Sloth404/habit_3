package com.moehr.habit_3.ui.overview

import com.moehr.habit_3.data.model.Habit

sealed class HabitListItem {
    data class HabitItem(val habit: Habit) : HabitListItem()
    object Placeholder : HabitListItem()
}