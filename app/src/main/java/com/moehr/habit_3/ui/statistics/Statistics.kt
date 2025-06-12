package com.moehr.habit_3.ui.statistics

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moehr.habit_3.R
import com.moehr.habit_3.data.model.Habit
import com.moehr.habit_3.data.model.HabitType
import com.moehr.habit_3.viewmodel.HabitViewModel
import com.moehr.habit_3.data.model.HabitViewModelFactory
import com.moehr.habit_3.data.model.RepeatPattern
import com.moehr.habit_3.data.model.dto.HabitLogEntryDTO
import com.moehr.habit_3.data.repository.HabitRepository
import java.time.LocalDateTime

class Statistics : Fragment() {

    private lateinit var viewModel: HabitViewModel
    private lateinit var adapter: StatisticsAdapter
    private var habits: List<Habit> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_statistics, container, false)

        val rvStatistics = view.findViewById<RecyclerView>(R.id.rvStatistics)
        rvStatistics.layoutManager = LinearLayoutManager(requireContext())

        adapter = StatisticsAdapter()
        rvStatistics.adapter = adapter

        adapter.submitList(padHabits(habits))

        // Initialize ViewModel
        val repository = HabitRepository()
        val factory = HabitViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[HabitViewModel::class.java]

        // Observe habits
        viewModel.habits.observe(viewLifecycleOwner) { updatedHabits ->
            habits = updatedHabits
            adapter.submitList(padHabits(habits))
        }

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
            type = HabitType.BUILD,
            unit = "",
            repeat = RepeatPattern.DAILY,
            reminders = emptyList(),
            createdAt = LocalDateTime.now(),
            motivationalNote = "",
            log = emptyList<HabitLogEntryDTO>(),
            target = 0
        )
    }
}
