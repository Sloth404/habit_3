package com.moehr.habit_3

sealed class HabitListItem {
    data class HabitItem(val habit: Habit) : HabitListItem()
    object Placeholder : HabitListItem()
}