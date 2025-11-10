package com.example.attendancemanager.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.attendancemanager.data.AppDatabase
import com.example.attendancemanager.data.Student
import com.example.attendancemanager.databinding.ActivityAddStudentBinding
import kotlinx.coroutines.launch

class AddStudentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStudentBinding
    private val db by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Set up the toolbar
        setSupportActionBar(binding.toolbar)
        // 2. Show the back arrow
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnSaveStudent.setOnClickListener {
            saveStudent()
        }
    }

    // 3. This function handles the click on the back arrow
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun saveStudent() {
        val name = binding.etStudentName.text.toString().trim()
        val rollNo = binding.etRollNumber.text.toString().trim()

        if (name.isEmpty()) {
            binding.tilStudentName.error = "Name cannot be empty"
            return
        } else {
            binding.tilStudentName.error = null
        }

        if (rollNo.isEmpty()) {
            binding.tilRollNumber.error = "Roll number cannot be empty"
            return
        } else {
            binding.tilRollNumber.error = null
        }

        val student = Student(name = name, rollNo = rollNo)

        lifecycleScope.launch {
            db.attendanceDao().insertStudent(student)

            runOnUiThread {
                Toast.makeText(this@AddStudentActivity, "Student Added!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}