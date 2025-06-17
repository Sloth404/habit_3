package com.moehr.habit_3.ui.tile_tracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moehr.habit_3.R
import java.time.LocalDate

/**
 * RecyclerView Adapter for displaying habit tracking days in a calendar grid.
 *
 * @param days List of HabitDay objects representing each day in the calendar.
 */
class HabitCalendarAdapter(private val days: List<HabitDay>) :
    RecyclerView.Adapter<HabitCalendarAdapter.DayViewHolder>() {

    /**
     * ViewHolder representing a single day tile in the calendar.
     */
    inner class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayTile: View = itemView.findViewById(R.id.dayTile)
    }

    /**
     * Inflates the day tile layout and creates a ViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_day_tile, parent, false)
        return DayViewHolder(view)
    }

    /**
     * Binds the day data to the ViewHolder, setting background drawable
     * based on the day's status:
     * - Disabled if outside the current month
     * - Highlighted if today
     * - Marked as success if habit was completed
     * - Default background otherwise
     */
    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val habitDay = days[position]

        val startOfMonth = LocalDate.now().withDayOfMonth(1)

        val backgroundRes = when {
            // Day is outside the current month → disabled appearance
            habitDay.date.month != startOfMonth.month -> R.drawable.bg_tile_disabled

            // Day is today → highlight as today
            habitDay.isToday -> R.drawable.bg_tile_today

            // Habit was successful on this day → success appearance
            habitDay.isSuccess -> R.drawable.bg_tile_success

            // Default appearance for normal days
            else -> R.drawable.bg_tile_default
        }

        holder.dayTile.setBackgroundResource(backgroundRes)
    }

    /**
     * Returns the total number of days to display in the calendar.
     */
    override fun getItemCount(): Int = days.size
}
