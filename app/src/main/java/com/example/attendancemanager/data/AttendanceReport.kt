package com.example.attendancemanager.data

data class AttendanceReport(
    val studentId: Int, // <-- ADD THIS
    val studentName: String,
    val presentCount: Int,
    val totalCount: Int
)