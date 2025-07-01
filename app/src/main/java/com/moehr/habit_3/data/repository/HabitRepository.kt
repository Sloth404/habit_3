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

/**
 * Repository class (last layer of the Model) providing methods to access the databases' daos and translate the raw data
 * to the [Habit] class used by the View-layer.
 *
 * @param habitDao used to access habit data.
 * @param habitLogEntryDao used to access habit log data.
 * */
class HabitRepository(
    private val habitDao: HabitDao,
    private val habitLogEntryDao: HabitLogEntryDao,
) {
    /**
     * [Flow] providing a list of all [HabitEntity]s.
     * */
    private val allHabitEntitiesFlow: Flow<List<HabitEntity>> = habitDao.getAll()

    /**
     * [Flow] providing a list of all [HabitLogEntry]s.
     * */
    private val allLogEntriesFlow: Flow<List<HabitLogEntry>> = habitLogEntryDao.getAll()

    /**
     * [Flow] providing a list of all complete [Habit]s. A complete habit contains the combined
     * [HabitEntity] *and* [HabitLogEntry] data.
     * */
    private val fullHabits: Flow<List<Habit>> =
        combine(allHabitEntitiesFlow, allLogEntriesFlow) { habits, logEntries ->
            habits.map { habit ->
                val habitLog = logEntries
                    .filter { it.uidHabit == habit.uid }
                    .map { it.date }

                habitEntityToHabit(habit, habitLog)
            }
        }

    /**
     * Getter for the [Flow] of complete [Habit]s. A complete habit contains the combined
     * [HabitEntity] *and* [HabitLogEntry] data.
     *
     * @return Flow with list of [Habit] entities.
     * */
    fun getHabits(): Flow<List<Habit>> {
        return fullHabits
    }

    /**
     * Getter for a static (non-[Flow]) list of all complete [Habit]s. A complete habit contains
     * the combined [HabitEntity] *and* [HabitLogEntry] data.
     *
     * @return List of [Habit] entities.
     * */
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

    /**
     * Method to add a new habit.
     *
     * @param item the new habit.
     * @return the habits ID
     * */
    suspend fun addHabit(item: Habit): Long {
        return habitDao.insert(habitToHabitEntity(item))
    }

    /**
     * Method to delete a habit.
     *
     * @param item the habit to delete.
     * */
    suspend fun deleteHabit(item: Habit) {
        habitDao.delete(habitToHabitEntity(item))
    }

    /**
     * Method to update a habit.
     *
     * @param item the habit to update
     * */
    suspend fun updateHabit(item: Habit) {
        // Update the habit table
        habitDao.update(habitToHabitEntity(item))

        if (item.repeat == RepeatPattern.DAILY) {
            updateDailyHabitLogList(item)
        } else {
            updateWeeklyHabitLogList(item)
        }
    }

    /**
     * Method to retrieve a [Habit] by its ID.
     *
     * @param id the habits ID.
     * @return the [Habit] if existent; else `null`
     * */
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

    /**
     * A method to translate a [HabitEntity] instance to a [Habit] instance adding the logEntries.
     *
     * @param habitEntity the [HabitEntity] to be translated.
     * @param logEntries a list of [LocalDate]s representing log entries.
     * @return [Habit]
     * */
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

    /**
     * Method for updating the log list of the given Habit.
     *
     * **IMPORTANT**: the repeat pattern of the habit has to be set to daily.
     *
     * @param item the [Habit] of which to update the log list
     * @throws IllegalStateException if the habits repeat pattern is not set to daily.
     * */
    private suspend fun updateDailyHabitLogList(item: Habit) {
        if (item.repeat == RepeatPattern.DAILY) {
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
        } else {
            throw IllegalStateException("The habit is not set to DAILY. This method is only applicable for DAILY habits.")
        }
    }

    /**
     * Method for updating the log list of the given Habit.
     *
     * **IMPORTANT**: the repeat pattern of the habit has to be set to weekly.
     *
     * @param item the [Habit] of which to update the log list
     * @throws IllegalStateException if the habits repeat pattern is not set to weekly.
     * */
    private suspend fun updateWeeklyHabitLogList(item: Habit) {
        if (item.repeat == RepeatPattern.WEEKLY) {
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
        } else {
            throw IllegalStateException("The habit is not set to WEEKLY. This method is only applicable for WEEKLY habits.")
        }
    }

    /**
     * A method to translate a [Habit] instance to a [HabitEntity] instance and minimize repetition.
     *
     * @param item the Habit
     * */
    private fun habitToHabitEntity(item: Habit) : HabitEntity {
        return HabitEntity(
            name = item.name,
            type = item.type,
            target = item.target,
            unit = item.unit,
            repeat = item.repeat,
            reminder = item.reminder,
            createdAt = item.createdAt,
            motivationalNote = item.motivationalNote
        )
    }
}