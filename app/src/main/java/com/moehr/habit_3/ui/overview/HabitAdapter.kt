package com.moehr.habit_3.ui.overview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.moehr.habit_3.R
import com.moehr.habit_3.data.model.Habit

class HabitAdapter(
    private val items: List<HabitListItem>,
    private val onAddHabitClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_HABIT = 0
        const val TYPE_PLACEHOLDER = 1
    }

    private var selectedPosition: Int? = null

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is HabitListItem.HabitItem -> TYPE_HABIT
            is HabitListItem.Placeholder -> TYPE_PLACEHOLDER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HABIT -> HabitViewHolder(
                inflater.inflate(R.layout.item_habit, parent, false)
            ) { position ->
                selectedPosition = position
                notifyDataSetChanged()
            }

            TYPE_PLACEHOLDER -> PlaceholderViewHolder(
                inflater.inflate(R.layout.item_habit_placeholder, parent, false),
                onAddHabitClick
            )

            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is HabitListItem.HabitItem -> {
                (holder as HabitViewHolder).bind(item.habit, position, position == selectedPosition)
            }
            is HabitListItem.Placeholder -> { /* no-op */ }
        }
    }

    fun updateData(newItems: List<HabitListItem>) {
        (items as? MutableList)?.clear()
        (items as? MutableList)?.addAll(newItems)
        notifyDataSetChanged()
    }

    class HabitViewHolder(
        itemView: View,
        private val onClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(habit: Habit, position: Int, isSelected: Boolean) {
            val tvHabitName = itemView.findViewById<TextView>(R.id.tvHabitName)
            val tvStreak = itemView.findViewById<TextView>(R.id.tvStreak)

            tvHabitName.text = habit.name
            tvStreak.text = habit.getCurrentStreak().toString()

            // Set background based on selection
            val backgroundRes = if (isSelected) {
                when (position) {
                    0 -> R.drawable.bg_habit_item_green
                    1 -> R.drawable.bg_habit_item_yellow
                    else -> R.drawable.bg_habit_item_blue
                }
            } else {
                R.drawable.bg_habit_item
            }

            itemView.setBackgroundResource(backgroundRes)

            itemView.setOnClickListener {
                onClick(position)
            }
        }
    }

    class PlaceholderViewHolder(itemView: View, onClick: () -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener { onClick() }
        }
    }
}
