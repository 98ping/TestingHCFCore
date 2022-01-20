package me.ninetyeightping.hcf.util

import org.apache.commons.lang.time.DurationFormatUtils
import java.util.regex.Matcher
import java.util.regex.Pattern


object TimeUtils {

    private val mmssBuilder = ThreadLocal.withInitial { StringBuilder() }

    @JvmStatic
    fun formatIntoAbbreviatedString(secs: Int): String {
        if (secs == 0) {
            return "0s"
        }

        val remainder = secs % 86400
        val days = secs / 86400
        val hours = remainder / 3600
        val minutes = remainder / 60 - hours * 60
        val seconds = remainder % 3600 - minutes * 60

        val fDays = if (days > 0) {
            " " + days + "d"
        } else {
            ""
        }

        val fHours = if (hours > 0) {
            " " + hours + "h"
        } else {
            ""
        }

        val fMinutes = if (minutes > 0) {
            " " + minutes + "m"
        } else {
            ""
        }

        val fSeconds = if (seconds > 0) {
            " " + seconds + "s"
        } else {
            ""
        }

        return (fDays + fHours + fMinutes + fSeconds).trim { it <= ' ' }
    }

    @JvmStatic
    fun formatIntoDetailedString(secs: Int): String {
        if (secs == 0) {
            return "0 seconds"
        }
        val remainder = secs % 86400
        val days = secs / 86400
        val hours = remainder / 3600
        val minutes = remainder / 60 - hours * 60
        val seconds = remainder % 3600 - minutes * 60
        val fDays = if (days > 0) " " + days + " day" + if (days > 1) "s" else "" else ""
        val fHours = if (hours > 0) " " + hours + " hour" + if (hours > 1) "s" else "" else ""
        val fMinutes = if (minutes > 0) " " + minutes + " minute" + if (minutes > 1) "s" else "" else ""
        val fSeconds = if (seconds > 0) " " + seconds + " second" + if (seconds > 1) "s" else "" else ""
        return (fDays + fHours + fMinutes + fSeconds).trim { it <= ' ' }
    }

    @JvmStatic
    fun formatIntoMMSS(secs: Int): String? {
        val seconds = secs % 60
        val minutesCount = ((secs - seconds) / 60).toLong()
        val minutes = minutesCount % 60L
        val hours = (minutesCount - minutes) / 60L
        val result: StringBuilder = mmssBuilder.get()
        result.setLength(0)
        if (hours > 0L) {
            if (hours < 10L) {
                result.append("0")
            }
            result.append(hours)
            result.append(":")
        }
        if (minutes < 10L) {
            result.append("0")
        }
        result.append(minutes)
        result.append(":")
        if (seconds < 10) {
            result.append("0")
        }
        result.append(seconds)
        return result.toString()
    }

    fun formatDuration(time: Long): String? {
        return DurationFormatUtils.formatDurationWords(time, true, true)
    }

    @JvmStatic
    fun parseTime(time: String): Int {
        if (time == "0" || time == "") {
            return 0
        }

        val lifeMatch = arrayOf("y", "w", "d", "h", "m", "s")
        val lifeInterval = intArrayOf(31_536_000, 604800, 86400, 3600, 60, 1)

        var seconds = -1
        for (i in lifeMatch.indices) {
            val matcher = Pattern.compile("([0-9]+)" + lifeMatch[i]).matcher(time)
            while (matcher.find()) {
                if (seconds == -1) {
                    seconds = 0
                }
                seconds += Integer.parseInt(matcher.group(1)) * lifeInterval[i]
            }
        }

        if (seconds == -1) {
            throw IllegalArgumentException("Invalid time provided.")
        }

        return seconds
    }

}