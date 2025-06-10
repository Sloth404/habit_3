package com.moehr.habit_3.ui.edit

import com.moehr.habit_3.data.model.HabitType
import com.moehr.habit_3.data.model.RepeatPattern

sealed class EditItem {
    data class HabitTypeContent(
        var habitType: HabitType,
        var repeatPattern: RepeatPattern,
        var unit: String,
        var target: Int,
    ) : EditItem()

    data class ReminderContent(
        var pushEnabled: Boolean = true,
        var timesOfDay: List<String> = listOf("MORNING")
    ) : EditItem()
}