package com.moehr.habit_3.ui.overview

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moehr.habit_3.EditHabitActivity
import com.moehr.habit_3.R
import com.moehr.habit_3.data.model.Habit
import com.moehr.habit_3.data.model.HabitLogEntryDTO
import com.moehr.habit_3.data.model.HabitType
import com.moehr.habit_3.data.model.ReminderTimeDTO
import com.moehr.habit_3.data.model.RepeatPattern
import java.time.LocalDateTime
import kotlin.reflect.KClass

class Overview : Fragment() {

    private lateinit var adapter: HabitAdapter

    private var actualHabits = mutableListOf<Habit>()  // placeholder for now

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_overview, container, false)

        // Mock data
        actualHabits.add(
            Habit(
                id = 1L,
                name = "Morning Jog",
                type = HabitType.BULD,
                unit = "minutes",
                repeat = RepeatPattern.DAILY,
                reminders = listOf(
                    ReminderTimeDTO(
                        hour = 7,
                        minute = 0
                    )
                ),
                createdAt = LocalDateTime.now().minusDays(10),
                motivationalNote = "Start your day with energy!",
                log = listOf(
                    HabitLogEntryDTO(
                        date = LocalDateTime.now().minusDays(3),
                        success = true
                    ),
                    HabitLogEntryDTO(
                        date = LocalDateTime.now().minusDays(2),
                        success = true
                    ),
                    HabitLogEntryDTO(
                        date = LocalDateTime.now().minusDays(1),
                        success = false
                    )
                )
            )
        )

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycleView)
        adapter = HabitAdapter(buildList()) {
            val target: KClass<out EditHabitActivity> = EditHabitActivity::class
            startActivity(Intent(requireContext(), target.java))
        }

        recyclerView.setLayoutManager(LinearLayoutManager(requireContext()))
        recyclerView.adapter = adapter

        return view
    }

    private fun buildList(): List<HabitListItem> {
        val list = mutableListOf<HabitListItem>()
        list.addAll(actualHabits.map { HabitListItem.HabitItem(it) })

        if (actualHabits.size < 3) {
            repeat(3 - actualHabits.size) {
                list.add(HabitListItem.Placeholder)
            }
        }
        return list
    }
}
