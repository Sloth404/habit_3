package com.moehr.habit_3

import EditSectionAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moehr.habit_3.ui.edit.EditItem
import com.moehr.habit_3.ui.edit.SectionType

class EditHabitActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerEditView)
        val items: MutableList<EditItem> = mutableListOf(
            EditItem.Header("Habit Type", SectionType.HABIT_TYPE, false),
            EditItem.Header("Reminder", SectionType.REMINDER, false),
        )

        val adapter = EditSectionAdapter(items)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }
}
