package com.example.attendancemanager.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.attendancemanager.R
import com.example.attendancemanager.data.Attendance
import com.example.attendancemanager.data.Student
import com.google.android.material.switchmaterial.SwitchMaterial
import java.util.Locale

class StudentAdapter(
    // 1. This is the full list of all students
    private val allStudents: List<Student>,
    private val attendanceForDate: List<Attendance>,
    private val onDeleteClicked: (Student) -> Unit
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    // 2. This list holds only the students that match the search
    private var filteredStudents: MutableList<Student> = mutableListOf()

    private val attendanceMap = mutableMapOf<Int, Boolean>()

    init {
        // 3. Initialize the filtered list with all students
        filteredStudents.addAll(allStudents)

        // 4. Build the attendance map from the complete list
        allStudents.forEach { student ->
            val savedRecord = attendanceForDate.find { it.studentId == student.id }

            if (savedRecord != null) {
                attendanceMap[student.id] = (savedRecord.status == "Present")
            } else {
                // Default to "Present" if no record exists
                attendanceMap[student.id] = true
            }
        }
    }

    class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val studentName: TextView = itemView.findViewById(R.id.tvStudentNameItem)
        val attendanceSwitch: SwitchMaterial = itemView.findViewById(R.id.attendanceSwitch)
        val deleteIcon: ImageView = itemView.findViewById(R.id.ivDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mark_attendance, parent, false)
        return StudentViewHolder(view)
    }

    // 5. The item count is based on the *filtered* list
    override fun getItemCount(): Int {
        return filteredStudents.size
    }

    // 6. The student to display is from the *filtered* list
    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = filteredStudents[position]
        holder.studentName.text = student.name

        // The map logic works as before, using the student's unique ID
        holder.attendanceSwitch.isChecked = attendanceMap[student.id] ?: true
        holder.attendanceSwitch.text = if (holder.attendanceSwitch.isChecked) "Present" else "Absent"

        holder.attendanceSwitch.setOnCheckedChangeListener { _, isChecked ->
            attendanceMap[student.id] = isChecked
            holder.attendanceSwitch.text = if (isChecked) "Present" else "Absent"
        }

        holder.deleteIcon.setOnClickListener {
            onDeleteClicked(student)
        }
    }

    fun getAttendanceData(): Map<Int, Boolean> {
        return attendanceMap
    }

    // 7. This is the new function that filters the list
    fun filter(query: String) {
        filteredStudents.clear()
        if (query.isEmpty()) {
            // If search is empty, show all students
            filteredStudents.addAll(allStudents)
        } else {
            // Otherwise, filter the 'allStudents' list
            val lowerCaseQuery = query.lowercase(Locale.getDefault())
            for (student in allStudents) {
                if (student.name.lowercase(Locale.getDefault()).contains(lowerCaseQuery) ||
                    student.rollNo.lowercase(Locale.getDefault()).contains(lowerCaseQuery)) {
                    filteredStudents.add(student)
                }
            }
        }
        // Tell the RecyclerView to refresh its view
        notifyDataSetChanged()
    }
}