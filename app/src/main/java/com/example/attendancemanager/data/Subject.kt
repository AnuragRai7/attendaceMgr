package com.example.attendancemanager.data
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class Subject(
    @PrimaryKey(autoGenerate = true) val subjectId: Int = 0,
    val subjectName: String
)