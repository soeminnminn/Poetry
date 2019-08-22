package com.s16.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
object ISO8601DateParser {

    private val zoneOffsetCodes = hashMapOf(
        "Y" to "GMT-12:00", "X" to "GMT-11:00", "W" to "GMT-10:00", "V†" to "GMT-09:30", "V" to "GMT-09:00",
        "U" to "GMT-08:00", "T" to "GMT-07:00", "S" to "GMT-06:00", "R" to "GMT-05:00", "Q" to "GMT-04:00",
        "P†" to "GMT-03:30", "P" to "GMT-03:00", "O" to "GMT-02:00", "N" to "GMT-01:00", "Z" to "GMT-00:00",
        "A" to "GMT+01:00", "B" to "GMT+02:00", "C" to "GMT+03:00", "C†" to "GMT+03:30", "D" to "GMT+04:00",
        "D†" to "GMT+04:30", "E" to "GMT+05:00", "E†" to "GMT+05:30", "E*" to "GMT+05:45", "F" to "GMT+06:00",
        "F†" to "GMT+06:30", "G" to "GMT+07:00", "H" to "GMT+08:00", "H†" to "GMT+08:30", "H*" to "GMT+08:45",
        "I" to "GMT+09:00", "I†" to "GMT+09:30", "K" to "GMT+10:00", "K†" to "GMT+10:30", "L" to "GMT+11:00",
        "M" to "GMT+12:00", "M*" to "GMT+12:45", "M†" to "GMT+13:00")

    fun parse(dateStr: String): Date {
        var pattern: String? = null
        var buffer = StringBuffer(dateStr.trim { it <= ' ' })

        if ("^([\\d]{4})-([\\d]{2})-([\\d]{2})$".toRegex().matches(buffer)) {
            pattern = "yyyy-MM-dd"

        } else {
            val matchResult = "^(\\d{4}-\\d{2}-\\d{2})([0:T\\s]+)(\\d{1,2}:[\\d.:]{2,9})(.*)\$".toRegex().find(buffer)
            if (matchResult != null) {
                buffer = StringBuffer("${matchResult.groups[1]} ")
                pattern = "yyyy-MM-dd HH:mm:ss.SSSz"

                val time = "${matchResult.groups[3]}"
                if ("^(\\d{1,2}):(\\d{1,2})\$".toRegex().matches(time)) {
                    buffer.append("$time:00.000")

                } else if ("^(\\d{1,2}):(\\d{1,2}):(\\d{1,2})\$".toRegex().matches(time)) {
                    buffer.append("$time.000")
                }

                var timeZone = "${matchResult.groups[4]}"
                timeZone = when {
                    timeZone.isBlank() -> zoneOffsetCodes["Z"]!!
                    zoneOffsetCodes.keys.contains(timeZone) -> zoneOffsetCodes[timeZone]!!
                    else -> "^(GMT|)([\\-+])(\\d{1,2})(\\d{2,})\$".toRegex().replace(timeZone, "GMT\$2\$3:\$4")
                }
                buffer.append(timeZone)
            }
        }

        var parsedDate = Date()
        if (pattern != null) {
            val format = SimpleDateFormat(pattern, Locale.ENGLISH)

            try {
                parsedDate = format.parse(buffer.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return parsedDate
    }

    fun format(date: Date): String {
        val timeZone = TimeZone.getDefault()
        val utc = TimeZone.getTimeZone("UTC") == timeZone || TimeZone.getTimeZone("GMT") == timeZone

        val pattern = if (utc) "yyyy-MM-dd'T'HH:mm:ss'Z'" else "yyyy-MM-dd'T'HH:mm:ssZ"
        val format = SimpleDateFormat(pattern)
        format.timeZone = timeZone

        val buffer = StringBuffer(format.format(date))
        if (!utc) {
            buffer.insert(buffer.length - 2, ':')
        }

        return buffer.toString()
    }

}

/**
 * Date Extensions
 */
fun Date.add(field: Int, amount: Int): Date{
    val cal = Calendar.getInstance()
    cal.time=this
    cal.add(field, amount)

    this.time = cal.time.time

    cal.clear()

    return this
}

fun Date.addYears(years: Int): Date {
    return add(Calendar.YEAR, years)
}
fun Date.addMonths(months: Int): Date {
    return add(Calendar.MONTH, months)
}
fun Date.addDays(days: Int): Date{
    return add(Calendar.DAY_OF_MONTH, days)
}
fun Date.addHours(hours: Int): Date{
    return add(Calendar.HOUR_OF_DAY, hours)
}
fun Date.addMinutes(minutes: Int): Date{
    return add(Calendar.MINUTE, minutes)
}
fun Date.addSeconds(seconds: Int): Date{
    return add(Calendar.SECOND, seconds)
}

fun Date.format(pattern: String): String {
    val sdf = SimpleDateFormat(pattern, Locale.getDefault())
    return sdf.format(this)
}

infix fun Date.formatTo(pattern: String): String {
    val sdf = SimpleDateFormat(pattern, Locale.getDefault())
    return sdf.format(this)
}

fun Date.toCalendar(): Calendar {
    val cal = Calendar.getInstance()
    cal.time = this
    return cal
}

/**
 * Calendar Extensions
 */
fun Calendar.set(date: Date) {
    set(date.year + 1900, date.month, date.date)
}

fun Calendar.format(pattern: String): String {
    val sdf = SimpleDateFormat(pattern, Locale.getDefault())
    return sdf.format(this.time)
}

infix fun Calendar.formatTo(pattern: String): String {
    val sdf = SimpleDateFormat(pattern, Locale.getDefault())
    return sdf.format(this.time)
}