package com.moehr.habit_3.data.repository

import com.moehr.habit_3.data.model.entity.HabitEntity
import com.moehr.habit_3.data.model.Habit
import com.moehr.habit_3.data.model.dao.HabitDao
import com.moehr.habit_3.data.model.dao.HabitLogEntryDao
import com.moehr.habit_3.data.model.dao.ReminderDao
import com.moehr.habit_3.data.model.dto.HabitLogEntryDTO
import com.moehr.habit_3.data.model.dto.ReminderDTO
import com.moehr.habit_3.data.model.entity.HabitLogEntry
import com.moehr.habit_3.data.model.entity.Reminder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class HabitRepository2(
    private val habitDao : HabitDao,
    private val habitLogEntryDao : HabitLogEntryDao,
    private val reminderDao: ReminderDao
) {
    private val allHabitEntitiesFlow : Flow<List<HabitEntity>> = habitDao.getAll()
    private val allReminderTimesFlow : Flow<List<Reminder>> = reminderDao.getAll()
    private val allLogEntriesFlow : Flow<List<HabitLogEntry>> = habitLogEntryDao.getAll()

    private val fullHabits : Flow<List<Habit>> = combine(allHabitEntitiesFlow, allReminderTimesFlow, allLogEntriesFlow) { habits, reminders, logEntries ->
        habits.map { habit ->
            val habitReminderTimes = reminders
                .filter { it.uidHabit == habit.uid }
                .map { it.toDto() }

            val habitLog = logEntries
                .filter { it.uidHabit == habit.uid }
                .map { it.toDto() }

            habitEntityToHabit(habit, habitReminderTimes, habitLog)
        }
    }

    fun getHabits() : Flow<List<Habit>> {
        return fullHabits
    }

    suspend fun addHabit(item : Habit) {
        val habit = HabitEntity(
            name = item.name,
            type = item.type,
            target = item.target,
            unit = item.unit,
            repeat = item.repeat,
            createdAt = item.createdAt,
            motivationalNote = item.motivationalNote
        )
        val habitId = habitDao.insert(habit).toInt()

        item.reminders.forEach { reminder ->
            val newReminder = Reminder(
                uidHabit = habitId,
                hour = reminder.hour,
                minute = reminder.minute
            )
            reminderDao.insert(newReminder)
        }
    }

    suspend fun deleteHabit(item : Habit) {
        val habit = HabitEntity(
            uid = item.id.toInt(),
            name = item.name,
            type = item.type,
            target = item.target,
            unit = item.unit,
            repeat = item.repeat,
            createdAt = item.createdAt,
            motivationalNote = item.motivationalNote
        )
        habitDao.delete(habit)
    }

    suspend fun updateHabit(item : Habit) {
        // Update the habit table
        val habit = HabitEntity(
            uid = item.id.toInt(),
            name = item.name,
            type = item.type,
            target = item.target,
            unit = item.unit,
            repeat = item.repeat,
            createdAt = item.createdAt,
            motivationalNote = item.motivationalNote
        )
        habitDao.update(habit)

        // Update the reminder table.
        // 1. Get current reminders.
        val currentReminders : List<Reminder> = reminderDao.getAllByHabitUid(item.id.toInt())

        // 2. Get list of new reminders.
        val newReminders = item.reminders.map { dto ->
            Reminder(
                uidHabit = item.id.toInt(),
                hour = dto.hour,
                minute = dto.minute
            )
        }

        // 3.1 Filter for deprecated reminders.
        val remindersToDelete = currentReminders.filter { old ->
            newReminders.none {
                it.hour == old.hour && it.minute == old.minute
            }
        }

        // 3.2 Delete deprecated reminders.
        remindersToDelete.forEach { reminderDao.delete(it) }

        // 4.1 Filter for reminders to add.
        val remindersToInsert = newReminders.filter { new ->
            currentReminders.none {
                it.hour == new.hour && it.minute == new.minute
            }
        }

        // 4.2 Insert the reminders that are not already in the DB
        remindersToInsert.forEach { reminderDao.insert(it) }

        // Update the log table.
        // 1. Get current logs
        val currentLogs : List<HabitLogEntry> = habitLogEntryDao.getAllByHabitUid(item.id.toInt())

        // 2. Get list of new logs
        val newLogs = item.log.map { dto ->
            HabitLogEntry(
                uidHabit = item.id.toInt(),
                date = dto.date
            )
        }

        // 3.1 Filter for deprecated logs.
        val logsToDelete = currentLogs.filter { old ->
            newLogs.none {
                it.date == old.date
            }
        }

        // 3.2 Delete deprecated logs.
        logsToDelete.forEach { habitLogEntryDao.delete(it) }

        // 4.1 Filter for logs to add.
        val logsToInsert = newLogs.filter { new ->
            currentLogs.none {
                it.date == new.date
            }
        }

        // 4.2 Insert the logs that are not already in the DB
        logsToInsert.forEach { habitLogEntryDao.insert(it) }
    }

    suspend fun getHabitById(id : Long) : Habit {
        val habitEntity = habitDao.getById(id.toInt())
        val reminders = reminderDao.getAllByHabitUid(habitEntity.uid)
        val reminderDtos = reminders.map { it.toDto() }
        val habitLogs = habitLogEntryDao.getAllByHabitUid(habitEntity.uid)
        val habitLogDtos = habitLogs.map { it.toDto() }

        return habitEntityToHabit(habitEntity, reminderDtos, habitLogDtos)
    }

    private fun habitEntityToHabit(habit : HabitEntity, reminderDtos : List<ReminderDTO>, logEntryDtos : List<HabitLogEntryDTO>) : Habit {
        return Habit(
            id = habit.uid.toLong(),
            name = habit.name,
            type = habit.type,
            target = habit.target,
            unit = habit.unit,
            repeat = habit.repeat,
            createdAt = habit.createdAt,
            motivationalNote = habit.motivationalNote,
            reminders = reminderDtos,
            log = logEntryDtos
        )
    }
}