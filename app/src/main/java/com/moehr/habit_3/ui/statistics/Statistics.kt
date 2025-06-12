package com.moehr.habit_3.ui.statistics

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moehr.habit_3.R
import com.moehr.habit_3.data.model.Habit
import com.moehr.habit_3.data.model.dto.HabitLogEntryDTO
import com.moehr.habit_3.data.model.HabitType
import com.moehr.habit_3.data.model.dto.ReminderTimeDTO
import com.moehr.habit_3.data.model.RepeatPattern
import java.time.LocalDateTime

class Statistics : Fragment() {

    private var actualHabits = mutableListOf<Habit>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_statistics, container, false)

        val rvStatistics = view.findViewById<RecyclerView>(R.id.rvStatistics)
        rvStatistics.layoutManager = LinearLayoutManager(requireContext())

        actualHabits.add(
            Habit(
                id = 1L,
                name = "Morning Jog",
                type = HabitType.BUILD,
                unit = "minutes",
                repeat = RepeatPattern.DAILY,
                reminders = listOf(
                    ReminderTimeDTO(hour = 7, minute = 0)
                ),
                createdAt = LocalDateTime.now().minusDays(10),
                motivationalNote = "Start your day with energy!",
                log = listOf(
                    HabitLogEntryDTO(date = LocalDateTime.now().minusDays(3), success = true),
                    HabitLogEntryDTO(date = LocalDateTime.now().minusDays(2), success = true),
                    HabitLogEntryDTO(date = LocalDateTime.now().minusDays(1), success = false)
                )
            )
        )

        val displayHabits = padHabits(actualHabits)

        rvStatistics.adapter = StatisticsAdapter(displayHabits)

        return view
    }

    private fun padHabits(habits: List<Habit>): List<Habit> {
        val padded = habits.toMutableList()
        while (padded.size < 3) {
            padded.add(createPlaceholderHabit())
        }
        return padded
    }

    private fun createPlaceholderHabit(): Habit {
        return Habit(
            id = -1L,
            name = "Coming Soon",
            type = HabitType.BUILD, // type doesn't matter here
            unit = "",
            repeat = RepeatPattern.DAILY,
            reminders = emptyList(),
            createdAt = LocalDateTime.now(),  // today
            motivationalNote = "",
            log = emptyList()
        )
    }
}

