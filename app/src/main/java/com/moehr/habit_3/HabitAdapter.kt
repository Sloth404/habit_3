package com.moehr.habit_3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class HabitAdapter(
    private val items: List<HabitListItem>,
    private val onAddHabitClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HABIT = 0
        private const val TYPE_PLACEHOLDER = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when  (items[position]) {
            is HabitListItem.HabitItem -> TYPE_HABIT
            is HabitListItem.Placeholder -> TYPE_PLACEHOLDER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HABIT -> HabitViewHolder(
                inflater.inflate(R.layout.habit_placeholder, parent, false),
            )

            TYPE_PLACEHOLDER -> PlaceholderViewHolder(
                inflater.inflate(R.layout.habit_placeholder, parent, false),
                onAddHabitClick,
            )

            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun getItemCount(): Int = items.size

    class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(habit: Habit) {
            // TODO: Bind habit data to the view
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is HabitListItem.HabitItem -> (holder as HabitViewHolder).bind(item.habit)
            is HabitListItem.Placeholder -> { /* no-op */ }
        }
    }

    class PlaceholderViewHolder(itemView: View, onClick: () -> Unit) :
        RecyclerView.ViewHolder(itemView) {
            init {
                itemView.setOnClickListener { onClick() }
            }
    }
}