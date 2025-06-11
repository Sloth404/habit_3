package com.moehr.habit_3.ui.tile_tracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moehr.habit_3.R
import java.time.LocalDate

class HabitCalendarAdapter(private val days: List<HabitDay>) :
    RecyclerView.Adapter<HabitCalendarAdapter.DayViewHolder>() {

    inner class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayTile: View = itemView.findViewById(R.id.dayTile)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_day_tile, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val habitDay = days[position]

        val startOfMonth = LocalDate.now().withDayOfMonth(1)

        when {
            habitDay.date.month != startOfMonth.month -> {
                holder.dayTile.setBackgroundResource(R.drawable.bg_tile_disabled)
            }
            habitDay.isToday -> {
                holder.dayTile.setBackgroundResource(R.drawable.bg_tile_today)
            }
            habitDay.isSuccess -> {
                holder.dayTile.setBackgroundResource(R.drawable.bg_tile_success)
            }
            else -> {
                holder.dayTile.setBackgroundResource(R.drawable.bg_tile_default)
            }
        }
    }

    override fun getItemCount(): Int = days.size
}
