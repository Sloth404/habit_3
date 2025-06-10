package com.moehr.habit_3

import EditSectionAdapter
import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moehr.habit_3.data.model.Habit
import com.moehr.habit_3.ui.edit.EditItem

class EditHabitActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        val tvState = findViewById<TextView>(R.id.tvState)
        val etName = findViewById<EditText>(R.id.etHabitName)
        val etMessage = findViewById<EditText>(R.id.etHabitMessage)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerEditView)
        val btnCancel = findViewById<Button>(R.id.btnEditCancel)
        val btnSave = findViewById<Button>(R.id.btnEditSave)

        val habit = intent.getSerializableExtra("habit_data", Habit::class.java)

        when (habit) {
            null -> {
                tvState.setText(R.string.edit_tvState_create)
                btnSave.setText(R.string.edit_btn_create)
            }
            else -> {
                tvState.setText(R.string.edit_tvState_edit)
                btnSave.setText(R.string.edit_btn_save)
            }
        }

        // Initialize default or habit-based items
        val items: MutableList<EditItem> = mutableListOf(
            EditItem.HabitTypeContent(
                habitType = habit?.type ?: com.moehr.habit_3.data.model.HabitType.BUILD,
                repeatPattern = habit?.repeat ?: com.moehr.habit_3.data.model.RepeatPattern.DAILY,
                unit = habit?.unit ?: "",
                target = 1, // Default, unless you add target to Habit model
            ),
            EditItem.ReminderContent(
                pushEnabled = habit?.reminders?.isNotEmpty() ?: false,
                timesOfDay = habit?.reminders?.map {
                    when {
                        it.hour in 5..9 -> "MORNING"
                        it.hour in 10..13 -> "NOON"
                        it.hour in 17..21 -> "EVENING"
                        else -> "CUSTOM"
                    }
                } ?: listOf("MORNING")
            )
        )

        etName.setText(habit?.name ?: "")
        etMessage.setText(habit?.motivationalNote ?: "")

        val adapter = EditSectionAdapter(items)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        btnCancel.setOnClickListener { showCancelConfirmationDialog() }
        btnSave.setOnClickListener { checkInputs() }
    }

    private fun showCancelConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Discard Changes?")
            .setMessage("Are you sure you want to discard your changes?")
            .setPositiveButton("Leave") { _, _ ->
                finish()
            }
            .setNegativeButton("Stay") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun checkInputs() {
        val name = findViewById<EditText>(R.id.etHabitName).text.toString()
        val message = findViewById<EditText>(R.id.etHabitMessage).text.toString()

        when {
            name.isEmpty() -> {
                Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show()
            }
            message.isEmpty() -> {
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
            }
            else -> {
                saveHabit(name, message)
            }
        }
    }

    private fun saveHabit(name: String, message: String) {
        finish()
    }
}
