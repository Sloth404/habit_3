package com.moehr.habit_3.ui.detail

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moehr.habit_3.EditHabitActivity
import com.moehr.habit_3.R
import com.moehr.habit_3.data.model.Habit
import com.moehr.habit_3.ui.tile_tracker.HabitCalendarAdapter
import com.moehr.habit_3.ui.tile_tracker.HabitDay
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val habit = intent.getSerializableExtra("habit_data", Habit::class.java)

        val tvDetailHabitName = findViewById<TextView>(R.id.tvDetailHabitName)
        val tvDetailMessage = findViewById<TextView>(R.id.tvDetailMessage)
        val tvDetailMonth = findViewById<TextView>(R.id.tvDetailMonth)
        val rvHabitCalendar = findViewById<RecyclerView>(R.id.rvHabitCalendar)
        val editButton = findViewById<Button>(R.id.btnDetailEdit)
        val rvDetailHabits = findViewById<RecyclerView>(R.id.rvDetailHabits)

        // Basic data binding
        tvDetailHabitName.text = habit?.name
        tvDetailMessage.text = habit?.motivationalNote

        val currMonth = LocalDate.now().month.toString()
        tvDetailMonth.text = currMonth

        // Set up RecyclerView
        rvDetailHabits.layoutManager = LinearLayoutManager(this)
        rvDetailHabits.adapter = DetailHabitAdapter(listOf(habit))

        editButton.setOnClickListener {
            val intent = Intent(this, EditHabitActivity::class.java)
            intent.putExtra("habit_data", habit)
            startActivity(intent)
        }

        // Populate calendar:
        habit?.let {
            val calendarData = buildCalendar(it)
            rvHabitCalendar.layoutManager = GridLayoutManager(this, 7) // 7 days (Mo-Su)
            rvHabitCalendar.adapter = HabitCalendarAdapter(calendarData)
        }
    }

    fun buildCalendar(habit: Habit): List<HabitDay> {
        val today = LocalDate.now()
        val startOfMonth = today.withDayOfMonth(1)
        val habitStart = habit.createdAt.toLocalDate() as LocalDate
        val successes = habit.getSuccessfulDates().toSet()

        // Calculate first Monday of the calendar view
        val firstMonday = startOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

        // We always show 42 days (6 weeks)
        val calendarDays = (0 until 42).map { offset ->
            val date = firstMonday.plusDays(offset.toLong())
            HabitDay(
                date = date,
                isBeforeStart = date.isBefore(habitStart),
                isSuccess = successes.contains(date),
                isToday = date == today,
                isCreatedAt = date == habitStart
            )
        }

        return calendarDays
    }
}
