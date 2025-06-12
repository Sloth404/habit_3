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

class StatisticsAdapter : RecyclerView.Adapter<StatisticsAdapter.StatisticsViewHolder>() {

    private var habits: List<Habit> = emptyList()

    fun submitList(newHabits: List<Habit>) {
        habits = newHabits
        notifyDataSetChanged()
    }

    inner class StatisticsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvStatisticsHabitName: TextView = itemView.findViewById(R.id.tvStatisticsHabitName)
        val miniCalendar: RecyclerView = itemView.findViewById(R.id.rvTileTracker)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatisticsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_statistics_habit, parent, false)
        return StatisticsViewHolder(view)
    }

    override fun onBindViewHolder(holder: StatisticsViewHolder, position: Int) {
        val habit = habits[position]
        holder.tvStatisticsHabitName.text = habit.name

        val calendarData = if (habit.id == -1L) {
            buildEmptyCalendar()
        } else {
            buildCalendar(habit)
        }

        holder.miniCalendar.layoutManager = GridLayoutManager(holder.itemView.context, 7)
        holder.miniCalendar.adapter = HabitCalendarAdapter(calendarData)
    }

    override fun getItemCount(): Int = habits.size

    private fun buildCalendar(habit: Habit): List<HabitDay> {
        val today = LocalDate.now()
        val startOfMonth = today.withDayOfMonth(1)
        val habitStart = habit.createdAt.toLocalDate()
        val successes = habit.getSuccessfulDates().toSet()

        val firstMonday = startOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        return (0 until 42).map { offset ->
            val date = firstMonday.plusDays(offset.toLong())
            HabitDay(
                date = date,
                isBeforeStart = date.isBefore(habitStart),
                isSuccess = successes.contains(date),
                isToday = date == today,
                isCreatedAt = date == habitStart
            )
        }
    }

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
                isToday = false,
                isCreatedAt = false
            )
        }
    }
}
