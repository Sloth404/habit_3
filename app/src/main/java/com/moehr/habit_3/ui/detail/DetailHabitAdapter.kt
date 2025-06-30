package com.moehr.habit_3.ui.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.moehr.habit_3.R
import com.moehr.habit_3.data.model.Habit

/**
 * Adapter for displaying a list of habits in detail view.
 * Supports selecting one habit at a time with visual feedback.
 *
 * @param habits List of Habit objects (nullable) to display.
 */
class DetailHabitAdapter(
    private val habits: List<Habit?>
) : RecyclerView.Adapter<DetailHabitAdapter.DetailHabitViewHolder>() {

    // Tracks the currently selected item position, null if none selected
    private var selectedPosition: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailHabitViewHolder {
        // Inflate the layout for each habit item
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_habit, parent, false)
        return DetailHabitViewHolder(view)
    }

    override fun getItemCount(): Int = habits.size

    override fun onBindViewHolder(holder: DetailHabitViewHolder, position: Int) {
        val habit = habits[position]
        val isSelected = position == selectedPosition

        habit?.let {
            // Bind habit data and selection state to the view holder
            holder.bind(it, isSelected)
        }

        // Handle click events to update selected item and refresh UI
        holder.itemView.setOnClickListener {
            val currentPosition = holder.adapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                selectedPosition = currentPosition
                notifyDataSetChanged() // Refresh list to update selection visuals
            }
        }
    }

    /**
     * ViewHolder class for displaying habit information.
     *
     * @param itemView Root view of the habit item layout.
     */
    class DetailHabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        /**
         * Binds a Habit object and selection state to the UI.
         *
         * @param habit The habit to display.
         * @param isSelected Whether this item is currently selected.
         */
        fun bind(habit: Habit, isSelected: Boolean) {
            val tvHabitName = itemView.findViewById<TextView>(R.id.tvHabitName)
            val tvStreak = itemView.findViewById<TextView>(R.id.tvStreak)

            // Set habit name and current streak count
            tvHabitName.text = habit.name
            tvStreak.text = habit.getCurrentStreak().toString()

            // Change background based on selection state
            val backgroundRes = if (isSelected) {
                R.drawable.bg_habit_item_green
            } else {
                R.drawable.bg_habit_item
            }
            itemView.setBackgroundResource(backgroundRes)
        }
    }
}
