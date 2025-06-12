package com.moehr.habit_3

import EditSectionAdapter
import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moehr.habit_3.data.model.Habit
import com.moehr.habit_3.data.model.HabitType
import com.moehr.habit_3.data.model.HabitViewModelFactory
import com.moehr.habit_3.data.model.RepeatPattern
import com.moehr.habit_3.data.model.dto.ReminderTimeDTO
import com.moehr.habit_3.data.repository.HabitRepository
import com.moehr.habit_3.ui.edit.EditItem
import com.moehr.habit_3.viewmodel.HabitViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class EditHabitActivity : AppCompatActivity() {

    private lateinit var tvState: TextView
    private lateinit var etName: EditText
    private lateinit var etMessage: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnCancel: Button
    private lateinit var btnSave: Button

    private lateinit var habitRepository: HabitRepository
    private lateinit var habitViewModel: HabitViewModel
    private var currentHabit: Habit? = null  // Keep reference to loaded habit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        initViews()

        val habitId = intent.getLongExtra("habit_id", -1L)
        habitRepository = HabitRepository()
        val factory = HabitViewModelFactory(habitRepository)
        habitViewModel = factory.create(HabitViewModel::class.java)

        if (habitId != -1L) {
            // We're editing an existing habit → load it
            lifecycleScope.launch {
                currentHabit = habitRepository.getHabitById(habitId)
                populateUI(currentHabit)
            }
        } else {
            // We're creating a new habit
            populateUI(null)
        }

        btnCancel.setOnClickListener { showCancelConfirmationDialog() }
        btnSave.setOnClickListener { checkInputs() }
    }

    private fun initViews() {
        tvState = findViewById(R.id.tvState)
        etName = findViewById(R.id.etHabitName)
        etMessage = findViewById(R.id.etHabitMessage)
        recyclerView = findViewById(R.id.recyclerEditView)
        btnCancel = findViewById(R.id.btnEditCancel)
        btnSave = findViewById(R.id.btnEditSave)
    }

    private fun populateUI(habit: Habit?) {
        if (habit == null) {
            tvState.setText(R.string.edit_tvState_create)
            btnSave.setText(R.string.edit_btn_create)
        } else {
            tvState.setText(R.string.edit_tvState_edit)
            btnSave.setText(R.string.edit_btn_save)
        }

        val items: MutableList<EditItem> = mutableListOf(
            EditItem.HabitTypeContent(
                habitType = habit?.type ?: HabitType.BUILD,
                repeatPattern = habit?.repeat ?: RepeatPattern.DAILY,
                unit = habit?.unit ?: "",
                target = 1  // Default for now
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
    }

    private fun showCancelConfirmationDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_cancel, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val btnStay = dialogView.findViewById<Button>(R.id.btnStay)
        val btnLeave = dialogView.findViewById<Button>(R.id.btnLeave)

        btnStay.setOnClickListener { dialog.dismiss() }
        btnLeave.setOnClickListener {
            dialog.dismiss()
            finish()
        }

        dialog.show()
    }

    private fun checkInputs() {
        val name = etName.text.toString()
        val message = etMessage.text.toString()

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
        val adapter = recyclerView.adapter as? EditSectionAdapter ?: return
        val habitTypeContent = adapter.items.find { it is EditItem.HabitTypeContent } as? EditItem.HabitTypeContent
        val reminderContent = adapter.items.find { it is EditItem.ReminderContent } as? EditItem.ReminderContent

        if (habitTypeContent == null) {
            Toast.makeText(this, "Habit type data missing", Toast.LENGTH_SHORT).show()
            return
        }

        // Extract data
        val habitType = habitTypeContent.habitType
        val repeatPattern = habitTypeContent.repeatPattern
        val unit = habitTypeContent.unit
        val target = habitTypeContent.target

        // Build reminders list using ReminderTimeDTO
        val reminders = mutableListOf<ReminderTimeDTO>()
        if (reminderContent != null && reminderContent.pushEnabled) {
            reminderContent.timesOfDay.forEach { timeLabel ->
                val hour = when (timeLabel) {
                    "MORNING" -> 7
                    "NOON" -> 12
                    "EVENING" -> 19
                    else -> 0 // CUSTOM or default
                }
                val minute = 0
                reminders.add(ReminderTimeDTO(hour, minute))
            }
        }

        val now = LocalDateTime.now()

        val habitToSave = currentHabit?.copy(
            name = name,
            motivationalNote = message,
            type = habitType,
            repeat = repeatPattern,
            unit = unit,
            target = target,
            reminders = reminders
        ) ?: Habit(
            id = 0L, // Assuming 0 means new and will be auto-assigned
            name = name,
            motivationalNote = message,
            type = habitType,
            repeat = repeatPattern,
            unit = unit,
            target = target,
            reminders = reminders,
            createdAt = now,
            log = emptyList()
        )

        if (currentHabit != null) {
            habitViewModel.updateHabit(habitToSave)
            Toast.makeText(this, "Habit updated", Toast.LENGTH_SHORT).show()
        } else {
            habitViewModel.addHabit(habitToSave)
            Toast.makeText(this, "Habit created", Toast.LENGTH_SHORT).show()
        }

        finish()
    }
}
