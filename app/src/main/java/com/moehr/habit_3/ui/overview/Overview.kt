package com.moehr.habit_3.ui.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moehr.habit_3.R
import com.moehr.habit_3.data.model.Habit

class Overview : Fragment() {

    private lateinit var adapter: HabitAdapter

    private val actualHabits = listOf<Habit>()  // placeholder for now

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_overview, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycleView)
        adapter = HabitAdapter(buildList()) {
            // Handle "add habit" clicked
        }

        recyclerView.setLayoutManager(LinearLayoutManager(requireContext()))
        recyclerView.adapter = adapter

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
