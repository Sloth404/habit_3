package com.moehr.habit_3.ui.edit

import com.moehr.habit_3.data.model.HabitType
import com.moehr.habit_3.data.model.RepeatPattern

/**
 * Represents different types of editable items in the habit editing UI.
 */
sealed class EditItem {

    /**
     * Represents the habit type section with properties
     * for habit type, repeat pattern, unit, and target value.
     *
     * @property habitType The type of habit (Build or Break).
     * @property repeatPattern The repeat frequency (Daily or Weekly).
     * @property unit The unit of measurement for the habit (e.g., "min", "pages").
     * @property target The numeric target value for the habit.
     */
    data class HabitTypeContent(
        var habitType: HabitType,
        var repeatPattern: RepeatPattern,
        var unit: String,
        var target: Int,
    ) : EditItem()

    /**
     * Represents the reminder settings for the habit.
     *
     * @property pushEnabled Whether push notifications are enabled.
     * @property timeOfDay Selected time for reminders (e.g., "MORNING").
     */
    data class ReminderContent(
        var pushEnabled: Boolean = true,
        var timeOfDay: String = "MORNING"
    ) : EditItem()
}
