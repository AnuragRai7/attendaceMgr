package com.example.attendancemanager.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.attendancemanager.R
import com.example.attendancemanager.data.AppDatabase
import com.example.attendancemanager.data.AttendanceReport
import com.example.attendancemanager.data.Subject
import com.example.attendancemanager.databinding.ActivityViewReportBinding
import kotlinx.coroutines.launch

class ViewReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewReportBinding
    private val db by lazy { AppDatabase.getDatabase(this) }

    // Store the lists for adapters and sharing
    private var reportList = listOf<AttendanceReport>()
    private var subjectList = listOf<Subject>()

    private var selectedSubjectId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Setup RecyclerView
        binding.rvReport.layoutManager = LinearLayoutManager(this)

        // Load subjects for the spinner
        setupSubjectSpinner()

        // --- NEW: Listen for report type changes ---
        binding.rgReportType.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rbAllSubjects) {
                // "All Subjects" selected
                binding.spinnerSubjectReport.visibility = View.GONE
                selectedSubjectId = -1 // Use -1 to represent "All"
                loadAttendanceReport()
            } else {
                // "By Subject" selected
                binding.spinnerSubjectReport.visibility = View.VISIBLE
                // Get the currently selected subject from the spinner
                val selectedPosition = binding.spinnerSubjectReport.selectedItemPosition
                if (selectedPosition != AdapterView.INVALID_POSITION && subjectList.isNotEmpty()) {
                    selectedSubjectId = subjectList[selectedPosition].subjectId
                }
                loadAttendanceReport()
            }
        }

        // Load the default report ("All Subjects")
        loadAttendanceReport()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.report_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_share -> {
                shareReport()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupSubjectSpinner() {
        lifecycleScope.launch {
            subjectList = db.attendanceDao().getAllSubjects()
            val subjectNames = subjectList.map { it.subjectName }

            runOnUiThread {
                val adapter = ArrayAdapter(
                    this@ViewReportActivity,
                    android.R.layout.simple_spinner_item,
                    subjectNames
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerSubjectReport.adapter = adapter

                // Listen for subject selection
                binding.spinnerSubjectReport.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        selectedSubjectId = subjectList[position].subjectId
                        // Reload the report for this subject
                        loadAttendanceReport()
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        selectedSubjectId = -1
                        loadAttendanceReport()
                    }
                }

                // If there are no subjects, hide the "By Subject" option
                if(subjectList.isEmpty()) {
                    binding.rbBySubject.visibility = View.GONE
                }
            }
        }
    }

    // --- UPDATED: This function now loads the report based on the selectedSubjectId ---
    private fun loadAttendanceReport() {
        lifecycleScope.launch {
            val tempReportList = mutableListOf<AttendanceReport>()
            val allStudents = db.attendanceDao().getAllStudents()

            for (student in allStudents) {
                val totalCount: Int
                val presentCount: Int

                if (selectedSubjectId == -1) {
                    // Load "All Subjects" data
                    totalCount = db.attendanceDao().getTotalAttendanceCountAllSubjects(student.id)
                    presentCount = db.attendanceDao().getPresentAttendanceCountAllSubjects(student.id)
                } else {
                    // Load data for the selected subject
                    totalCount = db.attendanceDao().getTotalAttendanceCountForSubject(student.id, selectedSubjectId)
                    presentCount = db.attendanceDao().getPresentAttendanceCountForSubject(student.id, selectedSubjectId)
                }

                if (totalCount > 0) {
                    val report = AttendanceReport(
                        studentId = student.id,
                        studentName = student.name,
                        presentCount = presentCount,
                        totalCount = totalCount
                    )
                    tempReportList.add(report)
                }
            }

            reportList = tempReportList

            runOnUiThread {
                if (reportList.isEmpty()) {
                    binding.rvReport.visibility = View.GONE
                    binding.tvEmptyReport.visibility = View.VISIBLE
                } else {
                    binding.rvReport.visibility = View.VISIBLE
                    binding.tvEmptyReport.visibility = View.GONE
                    binding.rvReport.adapter = ReportAdapter(reportList)
                }
            }
        }
    }

    private fun shareReport() {
        if (reportList.isEmpty()) return

        // --- UPDATED: The report title is now dynamic ---
        val reportTitle: String
        if (selectedSubjectId == -1) {
            reportTitle = "Attendance Report (All Subjects)"
        } else {
            val subjectName = subjectList.find { it.subjectId == selectedSubjectId }?.subjectName ?: "Report"
            reportTitle = "Attendance Report ($subjectName)"
        }

        val reportText = StringBuilder()
        reportText.append("$reportTitle\n")
        reportText.append("---------------------\n\n")

        reportList.forEach { report ->
            val percentage = if (report.totalCount > 0) {
                (report.presentCount.toDouble() / report.totalCount.toDouble()) * 100
            } else {
                0.0
            }
            val formattedPercentage = String.format("%.0f", percentage)

            reportText.append("${report.studentName}\n")
            reportText.append("Attended: ${report.presentCount} / ${report.totalCount} days ($formattedPercentage%)\n\n")
        }

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, reportText.toString())
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Share Report Via"))
    }
}