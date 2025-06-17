package com.moehr.habit_3.ui.overview

import com.moehr.habit_3.data.model.Habit

/**
 * Represents an item in the habits list.
 * Used by the adapter to distinguish between actual habit data and placeholder items.
 */
sealed class HabitListItem {

    /**
     * Represents a list item wrapping a Habit object.
     *
     * @property habit The habit data to display.
     */
    data class HabitItem(val habit: Habit) : HabitListItem()

    /**
     * Represents a placeholder list item used to fill the list
     * when there are fewer than the minimum required habits to show.
     */
    object Placeholder : HabitListItem()
}
