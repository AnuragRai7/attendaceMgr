package com.example.attendancemanager.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.attendancemanager.data.AppDatabase
import com.example.attendancemanager.data.Attendance
import com.example.attendancemanager.data.Student
import com.example.attendancemanager.data.Subject
import com.example.attendancemanager.databinding.ActivityMarkAttendanceBinding
import com.example.attendancemanager.utils.DateUtils
import kotlinx.coroutines.launch
import java.util.Calendar

class MarkAttendanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMarkAttendanceBinding
    private val db by lazy { AppDatabase.getDatabase(this) }
    private lateinit var studentAdapter: StudentAdapter
    private var allStudentList = mutableListOf<Student>() // Full list of students

    // --- State Variables ---
    private var subjectList = listOf<Subject>()
    private var selectedDate: String = ""
    private var selectedSubjectId: Int = -1 // -1 means no subject is selected

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarkAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set default date
        selectedDate = DateUtils.getCurrentDateForDB() // Use the DB format
        binding.btnPickDate.text = "Date: $selectedDate"

        // Setup RecyclerView
        binding.rvStudentsAttendance.layoutManager = LinearLayoutManager(this)

        // --- Load subjects into the Spinner ---
        setupSubjectSpinner()

        // --- CLICK LISTENERS ---
        binding.btnPickDate.setOnClickListener {
            showDatePickerDialog()
        }

        binding.btnSaveAttendance.setOnClickListener {
            saveAttendance()
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                if (::studentAdapter.isInitialized) {
                    studentAdapter.filter(newText.orEmpty())
                }
                return true
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    // --- This function loads subjects into the Spinner ---
    private fun setupSubjectSpinner() {
        lifecycleScope.launch {
            // Get subjects from DB
            subjectList = db.attendanceDao().getAllSubjects()

            // Get just the names for the adapter
            val subjectNames = subjectList.map { it.subjectName }

            runOnUiThread {
                // Create the adapter for the spinner
                val adapter = ArrayAdapter(
                    this@MarkAttendanceActivity,
                    android.R.layout.simple_spinner_item,
                    subjectNames
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerSubject.adapter = adapter

                // Set a listener to react when a subject is chosen
                binding.spinnerSubject.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        // A subject was selected
                        selectedSubjectId = subjectList[position].subjectId
                        // Load attendance for this subject and the selected date
                        loadAttendanceForDate(selectedDate, selectedSubjectId)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // No subject is selected
                        selectedSubjectId = -1
                        clearStudentList("Please select a subject.")
                    }
                }

                // Handle case where there are no subjects
                if (subjectList.isEmpty()) {
                    clearStudentList("No subjects found. Please add a subject first.")
                } else {
                    // Automatically select the first subject
                    selectedSubjectId = subjectList[0].subjectId
                    loadAttendanceForDate(selectedDate, selectedSubjectId)
                }
            }
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            selectedDate = String.format("%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            binding.btnPickDate.text = "Date: $selectedDate"

            // Reload attendance for the new date (and currently selected subject)
            loadAttendanceForDate(selectedDate, selectedSubjectId)
        }, year, month, day).show()
    }

    // This function now correctly loads data
    private fun loadAttendanceForDate(date: String, subjectId: Int) {
        if (subjectId == -1) {
            clearStudentList("Please select a subject.")
            return
        }

        lifecycleScope.launch {
            allStudentList = db.attendanceDao().getAllStudents().toMutableList()
            val attendanceForThisDate = db.attendanceDao().getAttendanceForDate(date, subjectId)

            runOnUiThread {
                if (allStudentList.isEmpty()) {
                    clearStudentList("No students added yet. Go back to add students.")
                } else {
                    binding.rvStudentsAttendance.visibility = View.VISIBLE
                    binding.tvEmptyList.visibility = View.GONE
                    binding.btnSaveAttendance.isEnabled = true
                }

                studentAdapter = StudentAdapter(allStudentList, attendanceForThisDate) { student ->
                    showDeleteConfirmationDialog(student)
                }
                binding.rvStudentsAttendance.adapter = studentAdapter
                // After loading, apply any existing search filter
                studentAdapter.filter(binding.searchView.query.toString())
            }
        }
    }

    // Helper function to clear the list and show a message
    private fun clearStudentList(message: String) {
        binding.rvStudentsAttendance.visibility = View.GONE
        binding.tvEmptyList.visibility = View.VISIBLE
        binding.btnSaveAttendance.isEnabled = false
        binding.tvEmptyList.text = message
    }

    private fun showDeleteConfirmationDialog(student: Student) {
        AlertDialog.Builder(this)
            .setTitle("Delete Student")
            .setMessage("Are you sure you want to delete ${student.name}? This cannot be undone.")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton("Yes") { _, _ ->
                deleteStudent(student)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteStudent(student: Student) {
        lifecycleScope.launch {
            db.attendanceDao().deleteStudent(student)
            runOnUiThread {
                Toast.makeText(this@MarkAttendanceActivity, "${student.name} deleted", Toast.LENGTH_SHORT).show()
                // Reload data for the current date and subject
                loadAttendanceForDate(selectedDate, selectedSubjectId)
            }
        }
    }

    private fun saveAttendance() {
        if (!::studentAdapter.isInitialized) {
            Toast.makeText(this, "Student list not loaded.", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedSubjectId == -1) {
            Toast.makeText(this, "Please select a subject.", Toast.LENGTH_SHORT).show()
            return
        }

        val attendanceData = studentAdapter.getAttendanceData()

        lifecycleScope.launch {
            for ((studentId, isPresent) in attendanceData) {
                val status = if (isPresent) "Present" else "Absent"

                val attendanceRecord = Attendance(
                    studentId = studentId,
                    date = selectedDate,
                    subjectId = selectedSubjectId,
                    status = status
                )

                db.attendanceDao().upsertAttendance(attendanceRecord)
            }

            runOnUiThread {
                Toast.makeText(this@MarkAttendanceActivity, "Attendance Saved for $selectedDate!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}