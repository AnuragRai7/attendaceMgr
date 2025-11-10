package com.example.attendancemanager.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import com.example.attendancemanager.data.AppDatabase
import com.example.attendancemanager.data.Subject
import com.example.attendancemanager.databinding.ActivityMainBinding
import com.example.attendancemanager.utils.DateUtils
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val db by lazy { AppDatabase.getDatabase(this) }

    private lateinit var sharedPrefs: SharedPreferences
    private val PREFS_NAME = "AttendancePrefs"
    private val KEY_DEFAULT_SUBJECT_ID = "defaultSubjectId"
    private val KEY_DEFAULT_SUBJECT_NAME = "defaultSubjectName"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // *** NEW: Set the date and day on the dashboard ***
        binding.tvCurrentDay.text = DateUtils.getCurrentDayOfWeek()
        binding.tvCurrentDate.text = DateUtils.getFriendlyDate()

        // --- Set up button click listeners ---
        binding.btnManageSubjects.setOnClickListener {
            startActivity(Intent(this, ManageSubjectsActivity::class.java))
        }
        binding.btnAddStudent.setOnClickListener {
            startActivity(Intent(this, AddStudentActivity::class.java))
        }
        binding.btnMarkAttendance.setOnClickListener {
            startActivity(Intent(this, MarkAttendanceActivity::class.java))
        }
        binding.btnViewReport.setOnClickListener {
            startActivity(Intent(this, ViewReportActivity::class.java))
        }

        binding.ivSelectSubject.setOnClickListener {
            showSubjectSelectorDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        loadDashboardStats()
    }

    private fun loadDashboardStats() {
        lifecycleScope.launch {
            val totalStudents = db.attendanceDao().getAllStudents().size
            val todayDate = DateUtils.getCurrentDateForDB()

            val defaultSubjectId = sharedPrefs.getInt(KEY_DEFAULT_SUBJECT_ID, -1)
            val defaultSubjectName = sharedPrefs.getString(KEY_DEFAULT_SUBJECT_NAME, "All Subjects")

            val presentCount: Int

            if (defaultSubjectId == -1) {
                presentCount = db.attendanceDao().getPresentCountForDate(todayDate)
            } else {
                presentCount = db.attendanceDao().getPresentCountForSubjectAndDate(todayDate, defaultSubjectId)
            }

            runOnUiThread {
                binding.tvStudentCount.text = totalStudents.toString()
                binding.tvTodayAttendance.text = "$presentCount / $totalStudents"

                if (defaultSubjectId == -1) {
                    binding.tvTodayLabel.text = "Present Today (All)"
                } else {
                    binding.tvTodayLabel.text = "Present ($defaultSubjectName)"
                }
            }
        }
    }

    private fun showSubjectSelectorDialog() {
        lifecycleScope.launch {
            val allSubjects = db.attendanceDao().getAllSubjects()
            val subjectNames = allSubjects.map { it.subjectName }.toTypedArray()

            val savedSubjectId = sharedPrefs.getInt(KEY_DEFAULT_SUBJECT_ID, -1)
            val checkedItem = allSubjects.indexOfFirst { it.subjectId == savedSubjectId }

            runOnUiThread {
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("Select Dashboard Subject")
                    .setSingleChoiceItems(subjectNames, checkedItem) { dialog, which ->
                        val selectedSubject = allSubjects[which]
                        saveDefaultSubject(selectedSubject.subjectId, selectedSubject.subjectName)
                        loadDashboardStats()
                        dialog.dismiss()
                    }
                    .setNeutralButton("Show All") { _, _ ->
                        saveDefaultSubject(-1, "All Subjects")
                        loadDashboardStats()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        }
    }

    private fun saveDefaultSubject(subjectId: Int, subjectName: String) {
        sharedPrefs.edit {
            putInt(KEY_DEFAULT_SUBJECT_ID, subjectId)
            putString(KEY_DEFAULT_SUBJECT_NAME, subjectName)
        }
    }
}