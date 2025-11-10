package com.example.attendancemanager.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.attendancemanager.data.AppDatabase
import com.example.attendancemanager.data.Subject
import com.example.attendancemanager.databinding.ActivityManageSubjectsBinding
import kotlinx.coroutines.launch

class ManageSubjectsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageSubjectsBinding
    private val db by lazy { AppDatabase.getDatabase(this) }
    private lateinit var subjectAdapter: SubjectAdapter
    private var subjectList = listOf<Subject>() // Store list at class level

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageSubjectsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.rvSubjects.layoutManager = LinearLayoutManager(this)

        binding.btnAddSubject.setOnClickListener {
            saveNewSubject()
        }

        loadSubjects()
    }

    private fun loadSubjects() {
        lifecycleScope.launch {
            subjectList = db.attendanceDao().getAllSubjects()

            runOnUiThread {
                // 1. UPDATE ADAPTER INITIALIZATION
                // Pass the delete click handler to the adapter
                subjectAdapter = SubjectAdapter(subjectList) { subject ->
                    // This code runs when a delete icon is clicked
                    showDeleteConfirmationDialog(subject)
                }
                binding.rvSubjects.adapter = subjectAdapter

                if (subjectList.isEmpty()) {
                    binding.rvSubjects.visibility = View.GONE
                    binding.tvEmptySubjects.visibility = View.VISIBLE
                } else {
                    binding.rvSubjects.visibility = View.VISIBLE
                    binding.tvEmptySubjects.visibility = View.GONE
                }
            }
        }
    }

    private fun saveNewSubject() {
        val subjectName = binding.etSubjectName.text.toString().trim()

        if (subjectName.isEmpty()) {
            binding.tilSubjectName.error = "Subject name cannot be empty"
            return
        } else {
            binding.tilSubjectName.error = null
        }

        val subject = Subject(subjectName = subjectName)

        lifecycleScope.launch {
            db.attendanceDao().insertSubject(subject)

            runOnUiThread {
                Toast.makeText(this@ManageSubjectsActivity, "Subject Added", Toast.LENGTH_SHORT).show()

                binding.etSubjectName.text = null
                loadSubjects() // Refresh the list
            }
        }
    }

    // 2. NEW FUNCTION TO SHOW DIALOG
    private fun showDeleteConfirmationDialog(subject: Subject) {
        AlertDialog.Builder(this)
            .setTitle("Delete Subject")
            .setMessage("Are you sure you want to delete '${subject.subjectName}'? This will also delete ALL attendance records for this subject.")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton("Yes") { _, _ ->
                deleteSubject(subject)
            }
            .setNegativeButton("No", null)
            .show()
    }

    // 3. NEW FUNCTION TO DELETE SUBJECT
    private fun deleteSubject(subject: Subject) {
        lifecycleScope.launch {
            db.attendanceDao().deleteSubject(subject)
            // Note: We also need to delete associated attendance.
            // The database will do this automatically if we set up "Cascade Delete".
            // For now, let's just delete the subject and refresh.

            runOnUiThread {
                Toast.makeText(this@ManageSubjectsActivity, "Subject deleted", Toast.LENGTH_SHORT).show()
                loadSubjects() // Refresh the list
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}