package com.moehr.habit_3.ui.statistics

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moehr.habit_3.MainApplication
import com.moehr.habit_3.R
import com.moehr.habit_3.data.model.Habit
import com.moehr.habit_3.data.model.HabitType
import com.moehr.habit_3.data.viewmodel.HabitViewModelFactory
import com.moehr.habit_3.data.model.RepeatPattern
import com.moehr.habit_3.data.model.dto.HabitLogEntryDTO
import com.moehr.habit_3.data.viewmodel.HabitViewModel
import java.time.LocalDateTime

/**
 * Fragment displaying statistics for habits in a RecyclerView.
 */
class Statistics : Fragment() {

    private lateinit var viewModel: HabitViewModel
    private lateinit var adapter: StatisticsAdapter
    private lateinit var app : MainApplication

    // Current list of habits displayed
    private var habits: List<Habit> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate fragment layout
        val view = inflater.inflate(R.layout.fragment_statistics, container, false)

        // Retrieve application
        app = requireActivity().application as MainApplication

        // Setup RecyclerView with LinearLayoutManager and adapter
        val rvStatistics = view.findViewById<RecyclerView>(R.id.rvStatistics)
        rvStatistics.layoutManager = LinearLayoutManager(requireContext())
        adapter = StatisticsAdapter()
        rvStatistics.adapter = adapter

        // Initially submit padded habits list (may be empty)
        adapter.submitList(padHabits(habits))

        // Initialize ViewModel with repository and factory
        val factory = HabitViewModelFactory(app.habitRepository)
        viewModel = ViewModelProvider(this, factory)[HabitViewModel::class.java]

        // Observe LiveData habits list and update adapter on changes
        viewModel.habits.observe(viewLifecycleOwner) { updatedHabits ->
            habits = updatedHabits
            adapter.submitList(padHabits(habits))
        }

        return view
    }

    /**
     * Ensures the habit list has at least 3 items by adding placeholders if needed.
     */
    private fun padHabits(habits: List<Habit>): List<Habit> {
        val padded = habits.toMutableList()
        while (padded.size < 3) {
            padded.add(createPlaceholderHabit())
        }
        return padded
    }

    /**
     * Creates a placeholder habit shown as "Coming Soon" in the UI.
     */
    private fun createPlaceholderHabit(): Habit = Habit(
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
