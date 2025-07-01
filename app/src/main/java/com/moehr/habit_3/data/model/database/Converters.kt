package com.moehr.habit_3.data.model.database

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Class providing conversion methods.
 * - [LocalDate] to String
 * - [LocalDateTime] to String
 *
 * and vice versa.
 * */
class Converters {
    private val formatterLocalDate = DateTimeFormatter.ISO_LOCAL_DATE
    private val formatterLocalDateTime = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    /**
     * Converts [LocalDate] to String.
     *
     * @param value the [LocalDate] instance
     * @return [String]
     * */
    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? {
        return value?.format(formatterLocalDate)
    }

    /**
     * Converts String to [LocalDate].
     *
     * @param value the [String] to be converted
     * @return [LocalDate]
     * */
    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? {
        return value?.let {
            LocalDate.parse(it, formatterLocalDate)
        }
    }

    /**
     * Converts [LocalDateTime] to String.
     *
     * @param value the [LocalDateTime] instance
     * @return [String]
     * */
    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? {
        return value?.format(formatterLocalDateTime)
    }

    /**
     * Converts String to [LocalDate].
     *
     * @param value the [String] to be converted
     * @return [LocalDate]
     * */
    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let {
            LocalDateTime.parse(it, formatterLocalDateTime)
        }
    }
}