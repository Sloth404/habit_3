package com.moehr.habit_3.data.repository

import com.moehr.habit_3.data.model.entity.HabitEntity
import com.moehr.habit_3.data.model.Habit
import com.moehr.habit_3.data.model.dao.HabitDao
import com.moehr.habit_3.data.model.dao.HabitLogEntryDao
import com.moehr.habit_3.data.model.dto.HabitLogEntryDTO
import com.moehr.habit_3.data.model.entity.HabitLogEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class HabitRepository(
    private val habitDao : HabitDao,
    private val habitLogEntryDao : HabitLogEntryDao,
) {
    private val allHabitEntitiesFlow : Flow<List<HabitEntity>> = habitDao.getAll()
    private val allLogEntriesFlow : Flow<List<HabitLogEntry>> = habitLogEntryDao.getAll()

    private val fullHabits : Flow<List<Habit>> = combine(allHabitEntitiesFlow, allLogEntriesFlow) { habits, logEntries ->
        habits.map { habit ->
            val habitLog = logEntries
                .filter { it.uidHabit == habit.uid }
                .map { it.toDto() }

            habitEntityToHabit(habit, habitLog)
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
            reminder = item.reminder,
            createdAt = item.createdAt,
            motivationalNote = item.motivationalNote
        )
        habitDao.insert(habit).toInt()
    }

    suspend fun deleteHabit(item : Habit) {
        val habit = HabitEntity(
            uid = item.id.toInt(),
            name = item.name,
            type = item.type,
            target = item.target,
            unit = item.unit,
            repeat = item.repeat,
            reminder = item.reminder,
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
            reminder = item.reminder,
            createdAt = item.createdAt,
            motivationalNote = item.motivationalNote
        )
        habitDao.update(habit)

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
        val habitLogs = habitLogEntryDao.getAllByHabitUid(habitEntity.uid)
        val habitLogDtos = habitLogs.map { it.toDto() }

        return habitEntityToHabit(habitEntity, habitLogDtos)
    }

    private fun habitEntityToHabit(habit : HabitEntity, logEntryDtos : List<HabitLogEntryDTO>) : Habit {
        return Habit(
            id = habit.uid.toLong(),
            name = habit.name,
            type = habit.type,
            target = habit.target,
            unit = habit.unit,
            repeat = habit.repeat,
            createdAt = habit.createdAt,
            motivationalNote = habit.motivationalNote,
            reminder = habit.reminder,
            log = logEntryDtos
        )
    }
}