package com.example.attendancemanager.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.attendancemanager.data.AppDatabase
import com.example.attendancemanager.databinding.ActivityStudentDetailBinding
import kotlinx.coroutines.launch

class StudentDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentDetailBinding
    private val db by lazy { AppDatabase.getDatabase(this) }
    private var studentId: Int = -1
    private var studentName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Get the data passed from the ReportAdapter
        studentId = intent.getIntExtra("STUDENT_ID", -1)
        studentName = intent.getStringExtra("STUDENT_NAME")

        // 2. Set up the toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Set the toolbar title to the student's name
        supportActionBar?.title = studentName ?: "Student Detail"

        // 3. Set up the RecyclerView
        binding.rvStudentHistory.layoutManager = LinearLayoutManager(this)

        // 4. Load the student's history
        loadStudentHistory()
    }

    // 5. This function handles the click on the back arrow
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun loadStudentHistory() {
        // Make sure we have a valid ID
        if (studentId == -1) return

        lifecycleScope.launch {
            // 6. Fetch the attendance history and the list of all subjects
            val history = db.attendanceDao().getAttendanceForStudent(studentId)
            val subjects = db.attendanceDao().getAllSubjects()

            // 7. Display the history in the RecyclerView
            runOnUiThread {
                if (history.isEmpty()) {
                    binding.rvStudentHistory.visibility = View.GONE
                    binding.tvEmptyHistory.visibility = View.VISIBLE
                } else {
                    binding.rvStudentHistory.visibility = View.VISIBLE
                    binding.tvEmptyHistory.visibility = View.GONE

                    // Pass BOTH lists to the adapter
                    val adapter = StudentDetailAdapter(history, subjects)
                    binding.rvStudentHistory.adapter = adapter
                }
            }
        }
    }
}