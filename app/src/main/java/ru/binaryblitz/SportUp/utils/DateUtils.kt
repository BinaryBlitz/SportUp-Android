package ru.binaryblitz.SportUp.utils

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun isAfterToday(date: Date): Boolean {
        val today = Calendar.getInstance()
        return !date.before(today.time)
    }

    fun getDateStringRepresentationWithoutTime(date: Date?): String {
        if (date == null) return ""
        val format = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        format.timeZone = TimeZone.getDefault()
        return format.format(date)
    }

    fun getTimeStringRepresentation(date: Date?): String {
        if (date == null) return ""
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("UTC")
        return format.format(date)
    }

    fun isAfter(first: Date, second: Date): Boolean {
        return !first.before(second)
    }

    fun parse(input: String): Date {
        try {
            return DateTime(input, DateTimeZone.getDefault()).toDate()
        } catch (e: Exception) {
            return Date()
        }
    }

    fun dateToISO(input: Date): String {
        val tz = TimeZone.getTimeZone("UTC")
        val df = SimpleDateFormat("yyyy-MM-dd")
        df.timeZone = tz
        return df.format(input)
    }
}
