package com.moehr.habit_3.data.model

import java.io.Serializable
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters

/**
 * Data class representing a Habit entity.
 *
 * @property id Unique identifier for the habit.
 * @property name The name/title of the habit.
 * @property type The type of habit (BUILD or BREAK).
 * @property target Target value to achieve (e.g., 10).
 * @property unit Unit of the target (e.g., "times", "minutes").
 * @property repeat How often the habit should be repeated (daily, weekly).
 * @property reminders List of reminder times for this habit.
 * @property createdAt Timestamp when the habit was created.
 * @property motivationalNote A custom motivational message for the habit.
 * @property log List of habit log entries representing past tracking data.
 */
data class Habit(
    val id: Long,
    val name: String,
    val type: HabitType,
    val target: Int,
    val unit: String,
    val repeat: RepeatPattern,
    val reminder: String?,
    val createdAt: LocalDateTime,
    val motivationalNote: String,
    val log: List<LocalDate>
) : Serializable {

    /**
     * Calculates the current streak of successful completions depending on the repeat pattern
     * (either daily or weekly) and habit type (either to be built or broken).
     *
     * @return Number of consecutive successful log entries.
     */
    fun getCurrentStreak(): Int {
        return if (repeat == RepeatPattern.DAILY) {
            if (type == HabitType.BUILD) {
                getDailyBuildStreak()
            } else {
                getDailyBreakStreak()
            }
        } else {
            getWeeklyStreak()
        }
    }

    /**
     * Calculate the build streak for a daily habit.
     *
     * @return Number of consecutive successfully logged days
     * */
    private fun getDailyBuildStreak(): Int {
        val logList: Set<LocalDate> = log.sortedByDescending { it }.toSet()
        var indexDate = if (log.contains(LocalDate.now())) {
            LocalDate.now()
        } else {
            LocalDate.now().minusDays(1)
        }
        var streak = 0

        for (date in logList) {
            if (indexDate.isEqual(date)) {
                streak++
                indexDate = indexDate.minusDays(1)
            } else {
                break
            }
        }

        return streak
    }

    /**
     * Calculate the break streak for a daily habit.
     *
     * Note: breaking habits only logged when done - goal is not to do them. Therefore a logged day
     * is bad.
     *
     * @return Number of consecutive not logged days
     * */
    private fun getDailyBreakStreak(): Int {
        var indexDate = LocalDate.now()
        var streak = 0

        while (indexDate.isAfter(createdAt.toLocalDate()) || indexDate.isEqual(createdAt.toLocalDate())) {
            if (!log.contains(indexDate)) {
                streak++
                indexDate = indexDate.minusDays(1)
            } else {
                break
            }
        }
        return streak
    }

    /**
     * Calculate the build/break streak of a habit that has to be done once weekly.
     *
     * Note: a logged week is a week in which at least one day is logged.
     *
     * @return Number of consecutive (not) logged weeks
     * */
    private fun getWeeklyStreak(): Int {
        var indexDate = if (isWeekSuccessful(LocalDate.now())) {
            LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
        } else {
            if (type == HabitType.BUILD) {
                LocalDate.now().with(TemporalAdjusters.previous(DayOfWeek.SUNDAY))
            } else {
                LocalDate.of(1970, 1, 1)
            }
        }
        var streak = 0

        while (indexDate.isAfter(createdAt.toLocalDate()) || indexDate.isEqual(createdAt.toLocalDate())) {
            if (isWeekSuccessful(indexDate)) {
                streak++
                indexDate = indexDate.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY))
            } else {
                break
            }
        }

        return streak
    }

    /**
     * Retrieves a list of dates where the habit was logged.
     *
     * @return List of [LocalDate] objects representing dates of logged days.
     */
    fun getSuccessfulDates(): List<LocalDate> {
        val startOfMonth = LocalDate.now().withDayOfMonth(1)
        if (repeat == RepeatPattern.DAILY) {
            // DAILY
            // get all days from the beginning of the month that were successful
            val successfulDates = log.filter { date ->
                (date.isAfter(startOfMonth.minusDays(1)) && date.isBefore(
                    LocalDate.now().plusDays(1)
                ))
            }
            return if (type == HabitType.BUILD) {
                successfulDates
            } else {
                invertSuccessfulDates(successfulDates, startOfMonth)
            }
        } else {
            // WEEKLY
            val dateList: MutableList<LocalDate> = mutableListOf()
            var date = startOfMonth
            var nextStartOfWeek = date.with(TemporalAdjusters.next(DayOfWeek.MONDAY))
            while (nextStartOfWeek.isBefore(
                    LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.TUESDAY))
                )
            ) {
                if (isWeekSuccessful(date)) {
                    dateList += getDatesOfWeek(date, pending = false).filter { day ->
                        (day.isAfter(createdAt.toLocalDate()) || day.isEqual(createdAt.toLocalDate()))
                    }
                }
                date = nextStartOfWeek
                nextStartOfWeek = date.with(TemporalAdjusters.next(DayOfWeek.MONDAY))
            }

            return dateList.toList()
        }
    }

    /**
     * Calculates remaining days of the current week the user has either to wait until he has to do
     * the habit again, or has left to do the habit.
     *
     * @return List of remaining days of the week
     * */
    fun getPendingDatesOfWeek(): List<LocalDate> {
        return if (repeat == RepeatPattern.WEEKLY) {
            getDatesOfWeek(LocalDate.now(), pending = true)
        } else {
            listOf()
        }
    }

    /**
     * Method to check if the current day is already logged.
     *
     * @return true if today is logged; else false
     * */
    fun isTodaySuccessful(): Boolean =
        if (type == HabitType.BUILD) log.contains(LocalDate.now()) else !log.contains(LocalDate.now())

    /**
     * Method to check if (at least) one day was already logged this week.
     *
     * @return true if any day of the week was logged; else false
     * */
    fun isThisWeekSuccessful(): Boolean {
        if (repeat == RepeatPattern.WEEKLY) {
            val today = LocalDate.now()
            val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

            return if (type == HabitType.BUILD) {
                log.any { it in startOfWeek..endOfWeek }
            } else {
                !log.any { it in startOfWeek..endOfWeek }
            }
        } else {
            throw IllegalStateException("The habit is not set to WEEKLY. This method is only applicable for WEEKLY habits.")
        }
    }

    /**
     * Method to check if the week of the passed date is/was logged. Determined by checking if
     * any day of the week was logged.
     *
     *
     * @param date any [LocalDate]
     * @return true if any day of the week was logged; else false
     * @throws IllegalStateException when the habits type is set to daily and this method is called.
     * */
    private fun isWeekSuccessful(date: LocalDate): Boolean {
        if (repeat == RepeatPattern.WEEKLY) {
            // get the start of the week of the given date
            val startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val endOfWeek = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

            return if (type == HabitType.BUILD) {
                log.any { it in startOfWeek..endOfWeek }
            } else {
                !log.any { it in startOfWeek..endOfWeek }
            }
        } else {
            throw IllegalStateException("The habit is not set to WEEKLY. This method is only applicable for WEEKLY habits.")
        }
    }

    /**
     * Method to get the dates of all days of the current week up to the current day or form the
     * current day on (determined by the [pending] parameter). Start and end of the week are
     * determined by the [date] parameter.
     *
     * @param date any [LocalDate]
     * @param pending filter between days of week up until today, or from today until the end of the week.
     * @return list of either past or future days of the current week
     * */
    private fun getDatesOfWeek(date: LocalDate, pending: Boolean): List<LocalDate> {
        val startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val endOfWeek = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

        var i = startOfWeek
        val datesOfWeek: ArrayList<LocalDate> = arrayListOf()
        while (i in startOfWeek..endOfWeek) {
            if (!pending && (i.isBefore(LocalDate.now()) || i.isEqual(LocalDate.now()))) {
                datesOfWeek.add(i)
            } else if (pending && (i.isAfter(LocalDate.now()))) {
                datesOfWeek.add(i)
            }
            i = i.plusDays(1)
        }

        return datesOfWeek
    }

    /**
     * Inverts the successful [dates] - if nothing was logged, the day is successful. Used for
     * habit breaking mode.
     *
     * @param dates list of logged days
     * @param startOfMonth the start date of the current month
     * @return inverted logged days from the beginning of the month
     * */
    private fun invertSuccessfulDates(
        dates: List<LocalDate>,
        startOfMonth: LocalDate
    ): List<LocalDate> {
        var i =
            if (startOfMonth.isAfter(createdAt.toLocalDate())) startOfMonth else createdAt.toLocalDate()
        val successfulDates: ArrayList<LocalDate> = arrayListOf()
        while (i.isBefore(LocalDate.now())) {
            if (!dates.any { it == i }) {
                successfulDates.add(i)
            }
            i = i.plusDays(1)
        }
        return successfulDates
    }
}
