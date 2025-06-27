package com.moehr.habit_3.ui.overview

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moehr.habit_3.DetailActivity
import com.moehr.habit_3.EditHabitActivity
import com.moehr.habit_3.MainApplication
import com.moehr.habit_3.R
import com.moehr.habit_3.data.model.Habit
import com.moehr.habit_3.data.model.HabitViewModelFactory
import com.moehr.habit_3.viewmodel.HabitViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.reflect.KClass

/**
 * Fragment showing an overview of habits.
 * Displays habit list with swipe actions for deleting or viewing details.
 * Shows current month and next 7 days with abbreviations.
 */
class Overview : Fragment() {

    private lateinit var adapter: HabitAdapter
    private lateinit var viewModel: HabitViewModel
    private lateinit var app : MainApplication
    private var habits: List<Habit> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_overview, container, false)

        // Retrieve application
        app = requireActivity().application as MainApplication

        // Initialize ViewModel with repository and factory
        val factory = HabitViewModelFactory(app.habitRepository)
        viewModel = ViewModelProvider(this, factory)[HabitViewModel::class.java]

        // Setup RecyclerView and its adapter
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycleView)
        adapter = HabitAdapter(buildList()) {
            // Open EditHabitActivity on item click
            val target: KClass<out EditHabitActivity> = EditHabitActivity::class
            startActivity(Intent(requireContext(), target.java))
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Observe habits LiveData from ViewModel and update UI on changes
        viewModel.habits.observe(viewLifecycleOwner) { updatedHabits ->
            habits = updatedHabits
            adapter.updateData(buildList())
        }

        // Setup UI text views for month and days display
        setupDateDisplays(view)

        // Attach swipe actions to RecyclerView items
        setupSwipeActions(recyclerView)

        return view
    }

    /**
     * Builds a list of HabitListItems from habits data,
     * padding with placeholders if less than 3 items.
     */
    private fun buildList(): List<HabitListItem> {
        val list = mutableListOf<HabitListItem>()
        list.addAll(habits.map { HabitListItem.HabitItem(it) })

        // Add placeholders to ensure minimum 3 list items for consistent UI
        if (habits.size < 3) {
            repeat(3 - habits.size) {
                list.add(HabitListItem.Placeholder)
            }
        }
        return list
    }

    /**
     * Initializes and sets the current month and next 7 days with abbreviated names and numbers.
     */
    private fun setupDateDisplays(view: View) {
        val tvMonth = view.findViewById<TextView>(R.id.tvMonth)
        val tvPrevWeekNames = view.findViewById<TextView>(R.id.tvPrevWeekNames)
        val tvPrevWeekNumbers = view.findViewById<TextView>(R.id.tvPrevWeekNumbers)

        val calendar = Calendar.getInstance()
        val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
        tvMonth.text = monthFormat.format(calendar.time)

        val dayAbbrevFormat = SimpleDateFormat("EE", Locale.ENGLISH)  // e.g. Mo, Tu
        val dayNumberFormat = SimpleDateFormat("dd", Locale.getDefault())

        val dayNames = StringBuilder()
        val dayNumbers = StringBuilder()

        // Loop through next 7 days including today
        for (i in 0 until 7) {
            val day = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, i) }

            // Format day abbreviation: take first 2 letters, capitalize first
            val abbrev = dayAbbrevFormat.format(day.time)
                .take(2)
                .lowercase()
                .replaceFirstChar { it.uppercase() }

            val dayNum = dayNumberFormat.format(day.time)

            dayNames.append(abbrev).append(" ")
            dayNumbers.append(dayNum).append(" ")
        }

        tvPrevWeekNames.text = dayNames.toString().trim()
        tvPrevWeekNumbers.text = dayNumbers.toString().trim()
    }

    /**
     * Sets up swipe gestures on RecyclerView items.
     * Left swipe deletes a habit, right swipe opens detail view.
     * Swipe disabled on placeholder items.
     * Custom background colors for swipe feedback.
     */
    private fun setupSwipeActions(recyclerView: RecyclerView) {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false  // No drag & drop support

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                // Handle only if the item is a real habit, not a placeholder
                if (adapter.getItemViewType(position) == HabitAdapter.TYPE_HABIT) {
                    val habit = habits[position]

                    when (direction) {
                        ItemTouchHelper.LEFT -> {
                            // Delete habit via ViewModel
                            viewModel.deleteHabit(habit)
                        }
                        ItemTouchHelper.RIGHT -> {
                            // Open DetailActivity to show habit details
                            val target: KClass<out DetailActivity> = DetailActivity::class
                            startActivity(Intent(requireContext(), target.java).apply {
                                putExtra("habit_id", habit.id)
                            })

                            // Reset swiped item to avoid removal from list
                            adapter.notifyItemChanged(position)
                        }
                    }
                } else {
                    // Reset placeholder item swipe state
                    adapter.notifyItemChanged(position)
                }
            }

            override fun getSwipeDirs(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                // Disable swipe gestures on placeholder items
                return if (adapter.getItemViewType(viewHolder.adapterPosition) == HabitAdapter.TYPE_PLACEHOLDER) {
                    0
                } else {
                    super.getSwipeDirs(recyclerView, viewHolder)
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    val itemView = viewHolder.itemView
                    val cornerRadius = itemView.resources.displayMetrics.density * 20

                    val top = itemView.top.toFloat()
                    val bottom = itemView.bottom.toFloat()
                    val paint = Paint().apply {
                        color = if (dX > 0) Color.GREEN else Color.RED
                    }
                    val path = android.graphics.Path()

                    if (dX < 0) {
                        // Swipe left — draw red background with rounded corners
                        val left = itemView.right.toFloat() + dX
                        val right = itemView.right.toFloat()

                        path.apply {
                            moveTo(left, top)
                            lineTo(right - cornerRadius, top)
                            quadTo(right, top, right, top + cornerRadius)
                            lineTo(right, bottom - cornerRadius)
                            quadTo(right, bottom, right, bottom)
                            lineTo(left + cornerRadius, bottom)
                            quadTo(left, bottom, left, bottom)
                            lineTo(left - cornerRadius, top)
                            quadTo(left, top, left, top)
                            close()
                        }
                    } else if (dX > 0) {
                        // Swipe right — draw green background with rounded corners
                        val left = itemView.left.toFloat()
                        val right = itemView.left.toFloat() + dX

                        path.apply {
                            moveTo(left, top)
                            lineTo(left, top)
                            lineTo(left, bottom - cornerRadius)
                            quadTo(left, bottom, left + cornerRadius, bottom)
                            lineTo(right + cornerRadius, bottom)
                            lineTo(right, top)
                            close()
                        }
                    }

                    c.drawPath(path, paint)
                }

                // Draw default swipe behavior (translation)
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        })

        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
}
