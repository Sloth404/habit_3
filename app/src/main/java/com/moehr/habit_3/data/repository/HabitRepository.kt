package com.moehr.habit_3.data.repository

import com.moehr.habit_3.data.model.entity.HabitEntity
import com.moehr.habit_3.data.model.Habit
import com.moehr.habit_3.data.model.RepeatPattern
import com.moehr.habit_3.data.model.dao.HabitDao
import com.moehr.habit_3.data.model.dao.HabitLogEntryDao
import com.moehr.habit_3.data.model.entity.HabitLogEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

class HabitRepository(
    private val habitDao: HabitDao,
    private val habitLogEntryDao: HabitLogEntryDao,
) {
    private val allHabitEntitiesFlow: Flow<List<HabitEntity>> = habitDao.getAll()
    private val allLogEntriesFlow: Flow<List<HabitLogEntry>> = habitLogEntryDao.getAll()

    private val fullHabits: Flow<List<Habit>> =
        combine(allHabitEntitiesFlow, allLogEntriesFlow) { habits, logEntries ->
            habits.map { habit ->
                val habitLog = logEntries
                    .filter { it.uidHabit == habit.uid }
                    .map { it.date }

                habitEntityToHabit(habit, habitLog)
            }
        }

    fun getHabits(): Flow<List<Habit>> {
        return fullHabits
    }

    suspend fun getHabitsStatic(): List<Habit> {
        val habits = habitDao.getAllStatic()
        val logEntries = habitLogEntryDao.getAllStatic()

        return habits.map { habit ->
            val habitLog = logEntries
                .filter { it.uidHabit == habit.uid }
                .map { it.date }

            habitEntityToHabit(habit, habitLog)
        }
    }

    suspend fun addHabit(item: Habit): Long {
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
        return habitDao.insert(habit)
    }

    suspend fun deleteHabit(item: Habit) {
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

    suspend fun updateHabit(item: Habit) {
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

        if (item.repeat == RepeatPattern.DAILY) {
            updateDailyHabitLogList(item)
        } else {
            updateWeeklyHabitLogList(item)
        }
    }

    suspend fun getHabitById(id: Long): Habit? {
        val habitEntity = habitDao.getById(id.toInt())
        if (habitEntity != null) {
            val habitLogEntries = habitLogEntryDao.getAllByHabitUid(habitEntity.uid)
            val habitLogList = habitLogEntries.map { it.date }

            return habitEntityToHabit(habitEntity, habitLogList)
        } else {
            return null
        }
    }

    private fun habitEntityToHabit(habitEntity: HabitEntity, logEntries: List<LocalDate>): Habit {
        return Habit(
            id = habitEntity.uid.toLong(),
            name = habitEntity.name,
            type = habitEntity.type,
            target = habitEntity.target,
            unit = habitEntity.unit,
            repeat = habitEntity.repeat,
            createdAt = habitEntity.createdAt,
            motivationalNote = habitEntity.motivationalNote,
            reminder = habitEntity.reminder,
            log = logEntries
        )
    }

    private suspend fun updateDailyHabitLogList(item: Habit) {
        // Update the log table.
        // 1. Get current logs
        val currentLogs: List<HabitLogEntry> = habitLogEntryDao.getAllByHabitUid(item.id.toInt())

        // 2.1 Get list of new logs
        val newLogs = item.log.map { date ->
            HabitLogEntry(
                uidHabit = item.id.toInt(),
                date = date
            )
        }

        // 3.1 Filter for deprecated logs.
        val logsToDelete = currentLogs.filter { old ->
            newLogs.none {
                it.date == old.date
            }
        }

        // 3.2.1 only one entry is allowed to be edited and
        // 3.2.2 only today's entry is allowed to be edited
        if (logsToDelete.isNotEmpty() && (logsToDelete.size > 1 || logsToDelete[0].date != LocalDate.now())) {
            throw IllegalArgumentException("Only today's log entry is allowed to be updated")
        }

        // 3.3 Delete deprecated logs.
        logsToDelete.forEach { habitLogEntryDao.delete(it) }

        // 4.1 Filter for logs to add.
        val logsToInsert = newLogs.filter { new ->
            currentLogs.none {
                it.date == new.date
            }
        }

        // 4.2.1 only one entry is allowed to be edited and
        // 4.2.2 only today's entry is allowed to be edited
        if (logsToInsert.isNotEmpty() && (logsToInsert.size > 1 || logsToInsert[0].date != LocalDate.now())) {
            throw IllegalArgumentException("Only today's log entry is allowed to be updated")
        }

        // 4.3 Insert the logs that are not already in the DB
        logsToInsert.forEach { habitLogEntryDao.insert(it) }
    }

    private suspend fun updateWeeklyHabitLogList(item: Habit) {
        // Update the log table.
        // 1. Get current logs
        val currentLogs: List<HabitLogEntry> = habitLogEntryDao.getAllByHabitUid(item.id.toInt())

        val today = LocalDate.now()
        val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

        // filter for logs from this week
        val weeksLogs: List<HabitLogEntry> = currentLogs.filter { log ->
            log.date in startOfWeek..endOfWeek
        }

        if (weeksLogs.isEmpty()) {
            // when this week has no entries -> add entry of today -> habit is done for this week
            habitLogEntryDao.insert(HabitLogEntry(
                uidHabit = item.id.toInt(),
                date = today
            ))
        } else {
            // when logs exist for this week -> remove all entries for this week to un-log the habit.
            // removes all week-entries to ensure if a habit is changed from daily to weekly and has
            // potentially more than one log entry, they are cleared too, to not falsely return
            // 'true' with Habit.isThisWeekSuccessful()
            weeksLogs.forEach { log ->
                habitLogEntryDao.delete(log)
            }
        }
    }
}