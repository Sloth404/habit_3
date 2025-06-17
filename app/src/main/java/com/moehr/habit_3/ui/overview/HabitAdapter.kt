package com.moehr.habit_3.ui.overview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.moehr.habit_3.R
import com.moehr.habit_3.data.model.Habit

/**
 * RecyclerView Adapter to display a list of habits along with placeholders for adding new habits.
 *
 * @param items List of HabitListItem representing habits or placeholders.
 * @param onAddHabitClick Callback invoked when a placeholder (add new habit) is clicked.
 */
class HabitAdapter(
    private val items: List<HabitListItem>,
    private val onAddHabitClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        // View type constants for differentiating item views
        const val TYPE_HABIT = 0
        const val TYPE_PLACEHOLDER = 1
    }

    // Track the currently selected habit position to update UI
    private var selectedPosition: Int? = null

    /**
     * Returns the view type based on the item at the given position.
     */
    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is HabitListItem.HabitItem -> TYPE_HABIT
            is HabitListItem.Placeholder -> TYPE_PLACEHOLDER
        }
    }

    /**
     * Creates appropriate ViewHolder depending on the view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            TYPE_HABIT -> HabitViewHolder(
                inflater.inflate(R.layout.item_habit, parent, false)
            ) { position ->
                // Update selected position and refresh list to reflect selection change
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

    /**
     * Returns the total number of items in the list.
     */
    override fun getItemCount(): Int = items.size

    /**
     * Binds the data to the ViewHolder depending on the item type.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is HabitListItem.HabitItem -> {
                (holder as HabitViewHolder).bind(
                    habit = item.habit,
                    position = position,
                    isSelected = position == selectedPosition
                )
            }
            is HabitListItem.Placeholder -> {
                // No binding necessary for placeholder view
            }
        }
    }

    /**
     * Updates the adapter data with a new list and refreshes the view.
     *
     * Note: This method assumes `items` is mutable, which may not always be true.
     * Consider refactoring to use a mutable list internally for safer updates.
     */
    fun updateData(newItems: List<HabitListItem>) {
        (items as? MutableList)?.apply {
            clear()
            addAll(newItems)
        }
        notifyDataSetChanged()
    }

    /**
     * ViewHolder for habit items displaying habit info and handling selection.
     */
    class HabitViewHolder(
        itemView: View,
        private val onClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        /**
         * Binds habit data and updates UI, including background based on selection and position.
         */
        fun bind(habit: Habit, position: Int, isSelected: Boolean) {
            val tvHabitName = itemView.findViewById<TextView>(R.id.tvHabitName)
            val tvStreak = itemView.findViewById<TextView>(R.id.tvStreak)

            // Set habit name and current streak count
            tvHabitName.text = habit.name
            tvStreak.text = habit.getCurrentStreak().toString()

            // Choose background drawable depending on selection and position for variety
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

            // Handle item click, notifying the adapter of selected position
            itemView.setOnClickListener { onClick(position) }
        }
    }

    /**
     * ViewHolder for placeholder items representing "Add new habit".
     */
    class PlaceholderViewHolder(itemView: View, onClick: () -> Unit) :
        RecyclerView.ViewHolder(itemView) {

        init {
            // Call the add habit callback when placeholder clicked
            itemView.setOnClickListener { onClick() }
        }
    }
}
