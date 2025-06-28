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
import com.moehr.habit_3.data.viewmodel.HabitViewModelFactory
import com.moehr.habit_3.data.model.RepeatPattern
import com.moehr.habit_3.data.preferences.PushNotificationKeys
import com.moehr.habit_3.ui.edit.EditItem
import com.moehr.habit_3.data.viewmodel.HabitViewModel
import com.moehr.habit_3.notification.NotificationAlarmManager
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class EditHabitActivity : AppCompatActivity() {

    // UI components
    private lateinit var tvState: TextView
    private lateinit var etName: EditText
    private lateinit var etMessage: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnCancel: Button
    private lateinit var btnSave: Button

    // Data and logic
    private lateinit var app : MainApplication
    private lateinit var habitViewModel: HabitViewModel
    private var currentHabit: Habit? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        // Retrieve Application
        app = application as MainApplication

        // Init UI
        initViews()

        val habitId = intent.getLongExtra("habit_id", -1L)

        // Init ViewModel
        val habitViewModelFactory = HabitViewModelFactory(app.habitRepository)
        habitViewModel = habitViewModelFactory.create(HabitViewModel::class.java)

        // Load habit if editing an existing one
        if (habitId != -1L) {
            lifecycleScope.launch {
                currentHabit = habitViewModel.getHabitById(habitId)
                populateUI(currentHabit)
            }
        } else {
            populateUI(null)
        }

        btnCancel.setOnClickListener { showCancelConfirmationDialog() }
        btnSave.setOnClickListener { checkInputs() }
    }

    /**
     * Initialize UI views.
     */
    private fun initViews() {
        tvState = findViewById(R.id.tvState)
        etName = findViewById(R.id.etHabitName)
        etMessage = findViewById(R.id.etHabitMessage)
        recyclerView = findViewById(R.id.recyclerEditView)
        btnCancel = findViewById(R.id.btnEditCancel)
        btnSave = findViewById(R.id.btnEditSave)
    }

    /**
     * Populate the UI with habit data or show defaults for new habit.
     */
    private fun populateUI(habit: Habit?) {
        // Update labels depending on whether we're editing or creating
        tvState.setText(if (habit == null) R.string.edit_tvState_create else R.string.edit_tvState_edit)
        btnSave.setText(if (habit == null) R.string.edit_btn_create else R.string.edit_btn_save)

        // Prepare UI sections for editing
        val items: MutableList<EditItem> = mutableListOf(
            EditItem.HabitTypeContent(
                habitType = habit?.type ?: HabitType.BUILD,
                repeatPattern = habit?.repeat ?: RepeatPattern.DAILY,
                unit = habit?.unit ?: "",
                target = 1
            ),
            EditItem.ReminderContent(
                pushEnabled = habit?.reminder?.isNotEmpty() ?: false,
                timeOfDay = when (habit?.reminder) {
                    PushNotificationKeys.TIME_MORNING.id -> "MORNING"
                    PushNotificationKeys.TIME_NOON.id -> "NOON"
                    PushNotificationKeys.TIME_EVENING.id -> "EVENING"
                    else -> "CUSTOM"
                }
            )
        )

        etName.setText(habit?.name ?: "")
        etMessage.setText(habit?.motivationalNote ?: "")

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = EditSectionAdapter(items)
    }

    /**
     * Show confirmation dialog when user attempts to cancel editing.
     */
    private fun showCancelConfirmationDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_cancel, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()
            .apply {
                window?.setBackgroundDrawableResource(android.R.color.transparent)
            }

        dialogView.findViewById<Button>(R.id.btnStay).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btnLeave).setOnClickListener {
            dialog.dismiss()
            finish()
        }

        dialog.show()
    }

    /**
     * Validate inputs before saving.
     */
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

    /**
     * Build and persist a habit object based on current UI state.
     */
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
        val reminder : String? = if (reminderContent != null && reminderContent.pushEnabled) {
            when(reminderContent.timeOfDay) {
                "MORNING" -> PushNotificationKeys.TIME_MORNING.id
                "NOON" -> PushNotificationKeys.TIME_NOON.id
                "EVENING" -> PushNotificationKeys.TIME_EVENING.id
                "CUSTOM" -> PushNotificationKeys.TIME_CUSTOM.id
                else -> null
            }
        } else {
            null
        }

        val now = LocalDateTime.now()

        val habitToSave = currentHabit?.copy(
            name = name,
            motivationalNote = message,
            type = habitType,
            repeat = repeatPattern,
            unit = unit,
            target = target,
            reminder = reminder
        ) ?: Habit(
            id = 0L,
            name = name,
            motivationalNote = message,
            type = habitType,
            repeat = repeatPattern,
            unit = unit,
            target = target,
            reminder = reminder,
            createdAt = now,
            log = emptyList()
        )

        // Save habit via ViewModel
        if (currentHabit != null) {
            habitViewModel.updateHabit(habitToSave)
            NotificationAlarmManager(this).scheduleNotificationAlarm(habitToSave)
            Toast.makeText(this, "Habit updated", Toast.LENGTH_SHORT).show()
        } else {
            habitViewModel.addHabit(habitToSave) { id ->
                val habit = Habit(
                    id = id,
                    name = habitToSave.name,
                    motivationalNote = habitToSave.motivationalNote,
                    type = habitToSave.type,
                    repeat = habitToSave.repeat,
                    unit = habitToSave.unit,
                    target = habitToSave.target,
                    reminder = habitToSave.reminder,
                    createdAt = habitToSave.createdAt,
                    log = emptyList()
                )
                NotificationAlarmManager(this).scheduleNotificationAlarm(habit)
            }
            Toast.makeText(this, "Habit created", Toast.LENGTH_SHORT).show()
        }

        finish()
    }
}
