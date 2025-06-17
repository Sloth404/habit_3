package com.moehr.habit_3.ui.overview

import com.moehr.habit_3.DetailActivity
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
import com.moehr.habit_3.EditHabitActivity
import com.moehr.habit_3.R
import com.moehr.habit_3.data.model.Habit
import com.moehr.habit_3.viewmodel.HabitViewModel
import com.moehr.habit_3.data.model.HabitViewModelFactory
import com.moehr.habit_3.data.repository.HabitRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.reflect.KClass

class Overview : Fragment() {

    private lateinit var adapter: HabitAdapter
    private lateinit var viewModel: HabitViewModel
    private var habits: List<Habit> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_overview, container, false)

        // Initialize ViewModel with repository
        val repository = HabitRepository()
        val factory = HabitViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[HabitViewModel::class.java]

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycleView)

        adapter = HabitAdapter(buildList()) {
            val target: KClass<out EditHabitActivity> = EditHabitActivity::class
            startActivity(Intent(requireContext(), target.java))
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Observe ViewModel LiveData
        viewModel.habits.observe(viewLifecycleOwner) { updatedHabits ->
            habits = updatedHabits
            adapter.updateData(buildList())
        }

        val tvMonth = view.findViewById<TextView>(R.id.tvMonth)
        val tvPrevWeekNames = view.findViewById<TextView>(R.id.tvPrevWeekNames)
        val tvPrevWeekNumbers = view.findViewById<TextView>(R.id.tvPrevWeekNumbers)

        // Set current month
        val calendar = Calendar.getInstance()
        val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
        tvMonth.text = monthFormat.format(calendar.time)

        // Set next 7 day abbreviations and numbers
        val dayAbbrevFormat = SimpleDateFormat("EE", Locale.ENGLISH)
        val dayNumberFormat = SimpleDateFormat("dd", Locale.getDefault())

        val dayNames = StringBuilder()
        val dayNumbers = StringBuilder()

        for (i in 0 until 7) {
            val day = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, i) }

            val abbrev = dayAbbrevFormat.format(day.time).take(2).lowercase().replaceFirstChar { it.uppercase() }
            val dayNum = dayNumberFormat.format(day.time)

            dayNames.append(abbrev).append(" ")
            dayNumbers.append(dayNum).append(" ")
        }

        tvPrevWeekNames.text = dayNames.toString().trim()
        tvPrevWeekNumbers.text = dayNumbers.toString().trim()


        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                if (adapter.getItemViewType(position) == HabitAdapter.TYPE_HABIT) {
                    val habit = habits[position]

                    when (direction) {
                        ItemTouchHelper.LEFT -> {
                            // Delete habit via ViewModel
                            viewModel.deleteHabit(habit)
                        }
                        ItemTouchHelper.RIGHT -> {
                            // Open detail activity
                            val target: KClass<out DetailActivity> = DetailActivity::class
                            startActivity(Intent(requireContext(), target.java).apply { putExtra("habit_id", habit.id) })

                            adapter.notifyItemChanged(position)
                        }
                    }
                } else {
                    adapter.notifyItemChanged(position)
                }
            }

            override fun getSwipeDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                return if (adapter.getItemViewType(viewHolder.adapterPosition) == HabitAdapter.TYPE_PLACEHOLDER) {
                    0 // Disable swipe for placeholder
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
                    val path = android.graphics.Path()
                    val paint = Paint().apply {
                        color = if (dX > 0) Color.GREEN else Color.RED
                    }

                    if (dX < 0) {
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
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        })

        itemTouchHelper.attachToRecyclerView(recyclerView)
        return view
    }

    private fun buildList(): List<HabitListItem> {
        val list = mutableListOf<HabitListItem>()
        list.addAll(habits.map { HabitListItem.HabitItem(it) })

        if (habits.size < 3) {
            repeat(3 - habits.size) {
                list.add(HabitListItem.Placeholder)
            }
        }
        return list
    }
}
