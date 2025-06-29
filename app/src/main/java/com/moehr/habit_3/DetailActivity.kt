package com.moehr.habit_3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moehr.habit_3.data.model.Habit
import com.moehr.habit_3.data.viewmodel.HabitViewModelFactory
import com.moehr.habit_3.ui.tile_tracker.HabitCalendarAdapter
import com.moehr.habit_3.ui.tile_tracker.HabitDay
import com.moehr.habit_3.data.viewmodel.HabitViewModel
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

class DetailActivity : AppCompatActivity() {

    // UI Components
    private lateinit var rvHabitCalendar: RecyclerView
    private lateinit var tvDetailHabitName: TextView
    private lateinit var tvDetailMessage: TextView
    private lateinit var tvDetailMonth: TextView
    private lateinit var editButton: Button

    // Data and ViewModel
    private lateinit var app : MainApplication
    private lateinit var habitViewModel: HabitViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // Retrieve habit id from intent
        val habitId = intent.getLongExtra("habit_id", -1L)
        if (habitId == -1L) {
            throw IllegalArgumentException("Habit ID is missing!")
        }

        // Retrieve application
        app = application as MainApplication

        // Init UI
        initViews()

        // Init ViewModel
        val habitViewModelFactory = HabitViewModelFactory(app.habitRepository)
        habitViewModel = habitViewModelFactory.create(HabitViewModel::class.java)

        // Load Habit from repository (ideally ViewModel would do this directly)
        lifecycleScope.launch {
            val habit = habitViewModel.getHabitById(habitId)
            updateUI(habit)
        }
    }

    /**
     * Finds views by their IDs to prepare UI.
     */
    private fun initViews() {
        rvHabitCalendar = findViewById(R.id.rvHabitCalendar)
        tvDetailHabitName = findViewById(R.id.tvDetailHabitName)
        tvDetailMessage = findViewById(R.id.tvDetailMessage)
        tvDetailMonth = findViewById(R.id.tvDetailMonth)
        editButton = findViewById(R.id.btnDetailEdit)
    }

    /**
     * Update UI with habit data, setup calendar and edit button.
     */
    private fun updateUI(habit: Habit) {
        tvDetailHabitName.text = habit.name
        tvDetailMessage.text = habit.motivationalNote
        tvDetailMonth.text = LocalDate.now().month.toString()

        // Prepare calendar data for display
        val calendarData = buildCalendar(habit)
        rvHabitCalendar.layoutManager = GridLayoutManager(this, 7)
        rvHabitCalendar.adapter = HabitCalendarAdapter(calendarData)

        // Navigate to EditHabitActivity on edit button click
        editButton.setOnClickListener {
            val intent = Intent(this, EditHabitActivity::class.java).apply {
                putExtra("habit_id", habit.id)
            }
            startActivity(intent)
        }
    }

    /**
     * Build calendar data showing days in a 6-week grid,
     * marking successes, today, habit start, and days before habit start.
     */
    private fun buildCalendar(habit: Habit): List<HabitDay> {
        val today = LocalDate.now()
        val startOfMonth = today.withDayOfMonth(1)
        val habitStart = habit.createdAt.toLocalDate()
        val successes = habit.getSuccessfulDates().toSet()
        val pendingDates = habit.getPendingDates().toSet()

        // Find the first Monday on or before the first of the month to align calendar grid
        val firstMonday = startOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

        // Generate 42 days for 6 rows x 7 columns calendar
        return (0 until 42).map { offset ->
            val date = firstMonday.plusDays(offset.toLong())
            HabitDay(
                date = date,
                isBeforeStart = date.isBefore(habitStart),
                isSuccess = successes.contains(date),
                isPending = pendingDates.contains(date),
                isToday = date == today,
                isCreatedAt = date == habitStart
            )
        }
    }
}
