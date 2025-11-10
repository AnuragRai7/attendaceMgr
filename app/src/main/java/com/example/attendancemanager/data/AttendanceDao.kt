package com.example.attendancemanager.data
import androidx.room.*

@Dao
interface AttendanceDao {

    // --- Student Queries ---
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertStudent(student: Student)

    @Query("SELECT * FROM students ORDER BY name ASC")
    suspend fun getAllStudents(): List<Student>

    @Delete
    suspend fun deleteStudent(student: Student)

    // --- Subject Queries ---
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSubject(subject: Subject)

    @Query("SELECT * FROM subjects ORDER BY subjectName ASC")
    suspend fun getAllSubjects(): List<Subject>

    @Delete
    suspend fun deleteSubject(subject: Subject)

    // --- Attendance Queries ---
    @Query("SELECT * FROM attendance WHERE date = :date AND subjectId = :subjectId")
    suspend fun getAttendanceForDate(date: String, subjectId: Int): List<Attendance>

    /**
     * This uses the composite primary key (studentId, date, subjectId)
     * to either insert a new record or update an existing one.
     */
    @Upsert
    suspend fun upsertAttendance(attendance: Attendance)

    // --- Report: By Subject ---
    @Query("SELECT COUNT(*) FROM attendance WHERE studentId = :studentId AND subjectId = :subjectId")
    suspend fun getTotalAttendanceCountForSubject(studentId: Int, subjectId: Int): Int

    @Query("SELECT COUNT(*) FROM attendance WHERE studentId = :studentId AND status = 'Present' AND subjectId = :subjectId")
    suspend fun getPresentAttendanceCountForSubject(studentId: Int, subjectId: Int): Int

    // --- Report: All Subjects ---
    @Query("SELECT COUNT(*) FROM attendance WHERE studentId = :studentId")
    suspend fun getTotalAttendanceCountAllSubjects(studentId: Int): Int

    @Query("SELECT COUNT(*) FROM attendance WHERE studentId = :studentId AND status = 'Present'")
    suspend fun getPresentAttendanceCountAllSubjects(studentId: Int): Int

    // --- Report: Student Detail ---
    @Query("SELECT * FROM attendance WHERE studentId = :studentId ORDER BY date DESC")
    suspend fun getAttendanceForStudent(studentId: Int): List<Attendance>

    // --- User / Login Queries ---
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    // --- Dashboard Stats Queries ---
    /**
     * Counts the number of UNIQUE students marked 'Present' on a specific date,
     * regardless of the subject.
     */
    @Query("SELECT COUNT(DISTINCT studentId) FROM attendance WHERE date = :date AND status = 'Present'")
    suspend fun getPresentCountForDate(date: String): Int

    /**
     * Counts the number of students marked 'Present' for a specific subject on a specific date.
     */
    @Query("SELECT COUNT(*) FROM attendance WHERE date = :date AND subjectId = :subjectId AND status = 'Present'")
    suspend fun getPresentCountForSubjectAndDate(date: String, subjectId: Int): Int


}