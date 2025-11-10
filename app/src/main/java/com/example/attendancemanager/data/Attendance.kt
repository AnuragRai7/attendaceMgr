package com.example.attendancemanager.data
import androidx.room.Entity

// The primary key is now a "composite" key of all three fields.
// This ensures one student can be marked once per day, PER SUBJECT.
@Entity(tableName = "attendance", primaryKeys = ["studentId", "date", "subjectId"])
data class Attendance(
    val studentId: Int,
    val date: String,
    val subjectId: Int, // <-- THIS IS THE NEW FIELD
    val status: String // "Present" or "Absent"
)