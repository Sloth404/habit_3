package com.moehr.habit_3.ui.statistics

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moehr.habit_3.R
import com.moehr.habit_3.data.model.Habit
import com.moehr.habit_3.ui.tile_tracker.HabitCalendarAdapter
import com.moehr.habit_3.ui.tile_tracker.HabitDay
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

/**
 * RecyclerView Adapter to display a list of Habits along with
 * their mini calendar showing habit completion statistics.
 */
class StatisticsAdapter : RecyclerView.Adapter<StatisticsAdapter.StatisticsViewHolder>() {

    // List holding the current habits to display
    private var habits: List<Habit> = emptyList()

    /**
     * Updates the list of habits and refreshes the RecyclerView.
     */
    fun submitList(newHabits: List<Habit>) {
        habits = newHabits
        notifyDataSetChanged()
    }

    /**
     * ViewHolder representing each habit item with its name and mini calendar.
     */
    inner class StatisticsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvStatisticsHabitName: TextView = itemView.findViewById(R.id.tvStatisticsHabitName)
        val miniCalendar: RecyclerView = itemView.findViewById(R.id.rvTileTracker)
    }

    /**
     * Inflates the habit item layout and creates a ViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatisticsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_statistics_habit, parent, false)
        return StatisticsViewHolder(view)
    }

    /**
     * Binds habit data and calendar to the ViewHolder.
     * If habit ID is -1, shows an empty calendar.
     */
    override fun onBindViewHolder(holder: StatisticsViewHolder, position: Int) {
        val habit = habits[position]

        // Set habit name
        holder.tvStatisticsHabitName.text = habit.name

        // Build calendar data depending on habit ID
        val calendarData = if (habit.id == -1L) {
            buildEmptyCalendar()
        } else {
            buildCalendar(habit)
        }

        // Setup RecyclerView for mini calendar with 7 columns (one week)
        holder.miniCalendar.layoutManager = GridLayoutManager(holder.itemView.context, 7)
        holder.miniCalendar.adapter = HabitCalendarAdapter(calendarData)
    }

    /**
     * Returns the number of habits in the list.
     */
    override fun getItemCount(): Int = habits.size

    /**
     * Builds calendar data for a habit showing 6 weeks (42 days),
     * starting from the Monday on or before the first day of the current month.
     */
    private fun buildCalendar(habit: Habit): List<HabitDay> {
        val today = LocalDate.now()
        val startOfMonth = today.withDayOfMonth(1)
        val habitStart = habit.createdAt.toLocalDate()
        val successes = habit.getSuccessfulDates().toSet()
        val pendingDates = habit.getPendingDatesOfWeek().toSet()

        val firstMonday = startOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

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

    /**
     * Builds an empty calendar with no habit data, useful for placeholder display.
     */
    private fun buildEmptyCalendar(): List<HabitDay> {
        val today = LocalDate.now()
        val startOfMonth = today.withDayOfMonth(1)
        val firstMonday = startOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

        return (0 until 42).map { offset ->
            val date = firstMonday.plusDays(offset.toLong())
            HabitDay(
                date = date,
                isBeforeStart = false,
                isSuccess = false,
                isPending = false,
                isToday = false,
                isCreatedAt = false
            )
        }
    }
}
