package com.example.attendancemanager.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {

    /**
     * Returns the date in yyyy-MM-dd format for the database.
     */
    fun getCurrentDateForDB(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    /**
     * Returns the full day name, e.g., "Saturday"
     */
    fun getCurrentDayOfWeek(): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }

    /**
     * NEW: Returns a friendly date, e.g., "November 08, 2025"
     */
    fun getFriendlyDate(): String {
        val sdf = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        return sdf.format(Date())
    }
}