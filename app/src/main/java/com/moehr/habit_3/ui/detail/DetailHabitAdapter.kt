package com.moehr.habit_3.ui.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.moehr.habit_3.R
import com.moehr.habit_3.data.model.Habit

class DetailHabitAdapter(
    private val habits: List<Habit?>
) : RecyclerView.Adapter<DetailHabitAdapter.DetailHabitViewHolder>() {

    private var selectedPosition: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailHabitViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_habit, parent, false)
        return DetailHabitViewHolder(view)
    }

    override fun getItemCount(): Int = habits.size

    override fun onBindViewHolder(holder: DetailHabitViewHolder, position: Int) {
        val habit = habits[position]
        val isSelected = position == selectedPosition
        if (habit != null) {
            holder.bind(habit, isSelected)
        }

        holder.itemView.setOnClickListener {
            val currentPosition = holder.adapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                selectedPosition = currentPosition
                notifyDataSetChanged()
            }
        }
    }


    class DetailHabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(habit: Habit, isSelected: Boolean) {
            val tvHabitName = itemView.findViewById<TextView>(R.id.tvHabitName)
            val tvStreak = itemView.findViewById<TextView>(R.id.tvStreak)

            tvHabitName.text = habit.name
            tvStreak.text = habit.getCurrentStreak().toString()

            itemView.setBackgroundResource(
                if (isSelected) R.drawable.bg_habit_item_green
                else R.drawable.bg_habit_item
            )
        }
    }
}
