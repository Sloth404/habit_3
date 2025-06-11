package com.moehr.habit_3.ui.overview

import com.moehr.habit_3.ui.detail.DetailActivity
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moehr.habit_3.EditHabitActivity
import com.moehr.habit_3.R
import com.moehr.habit_3.data.model.Habit
import com.moehr.habit_3.data.model.HabitLogEntryDTO
import com.moehr.habit_3.data.model.HabitType
import com.moehr.habit_3.data.model.ReminderTimeDTO
import com.moehr.habit_3.data.model.RepeatPattern
import java.time.LocalDateTime
import kotlin.reflect.KClass

class Overview : Fragment() {

    private lateinit var adapter: HabitAdapter

    private var actualHabits = mutableListOf<Habit>()  // placeholder for now

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_overview, container, false)

        // Mock data
        actualHabits.add(
            Habit(
                id = 1L,
                name = "Morning Jog",
                type = HabitType.BUILD,
                unit = "minutes",
                repeat = RepeatPattern.DAILY,
                reminders = listOf(
                    ReminderTimeDTO(
                        hour = 7,
                        minute = 0
                    )
                ),
                createdAt = LocalDateTime.now().minusDays(10),
                motivationalNote = "Start your day with energy!",
                log = listOf(
                    HabitLogEntryDTO(
                        date = LocalDateTime.now().minusDays(3),
                        success = true
                    ),
                    HabitLogEntryDTO(
                        date = LocalDateTime.now().minusDays(2),
                        success = true
                    ),
                    HabitLogEntryDTO(
                        date = LocalDateTime.now().minusDays(1),
                        success = false
                    )
                )
            )
        )

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycleView)

        adapter = HabitAdapter(buildList()) {
            val target: KClass<out EditHabitActivity> = EditHabitActivity::class
            startActivity(Intent(requireContext(), target.java))
        }

        recyclerView.setLayoutManager(LinearLayoutManager(requireContext()))
        recyclerView.adapter = adapter

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                if (adapter.getItemViewType(position) == HabitAdapter.TYPE_HABIT) {
                    val habit = actualHabits[position]

                    when (direction) {
                        ItemTouchHelper.LEFT -> {
                            actualHabits.removeAt(position)
                            adapter.updateData(buildList())
                        }
                        ItemTouchHelper.RIGHT -> {
                            val target: KClass<out DetailActivity> = DetailActivity::class
                            startActivity(Intent(requireContext(), target.java).apply { putExtra("habit_data", habit) })

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
                    val cornerRadius = itemView.resources.displayMetrics.density * 20 // 20dp
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

                            // top-right corner
                            lineTo(right - cornerRadius, top)
                            quadTo(right, top, right, top + cornerRadius)

                            // bottom-right corner
                            lineTo(right, bottom - cornerRadius)
                            quadTo(right, bottom, right, bottom)

                            // bottom-left corner
                            lineTo(left + cornerRadius, bottom)
                            quadTo(left, bottom, left, bottom )

                            // top-left corner
                            lineTo(left - cornerRadius, top)
                            quadTo(left, top, left, top)

                            close()
                        }

                    } else if (dX > 0) {
                        val left = itemView.left.toFloat()
                        val right = itemView.left.toFloat() + dX

                        path.apply {
                            moveTo(left, top)

                            // top-left corner
                            lineTo(left, top)

                            // bottom-left corner
                            lineTo(left, bottom - cornerRadius)
                            quadTo(left, bottom, left + cornerRadius, bottom)

                            // bottom-right corner
                            lineTo(right + cornerRadius, bottom)

                            // top-right corner
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
        list.addAll(actualHabits.map { HabitListItem.HabitItem(it) })

        if (actualHabits.size < 3) {
            repeat(3 - actualHabits.size) {
                list.add(HabitListItem.Placeholder)
            }
        }
        return list
    }
}
