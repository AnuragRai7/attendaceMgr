package com.example.attendancemanager.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.attendancemanager.R
import com.example.attendancemanager.data.Attendance
import com.example.attendancemanager.data.Subject

class StudentDetailAdapter(
    private val history: List<Attendance>,
    private val subjects: List<Subject> // <-- 1. ADD SUBJECT LIST
) : RecyclerView.Adapter<StudentDetailAdapter.HistoryViewHolder>() {

    // 2. CREATE A MAP FOR QUICK SUBJECT LOOKUP
    private val subjectMap = subjects.associateBy({it.subjectId}, {it.subjectName})

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateText: TextView = itemView.findViewById(R.id.tvDate)
        val statusText: TextView = itemView.findViewById(R.id.tvStatus)
        val subjectText: TextView = itemView.findViewById(R.id.tvSubjectName) // <-- 3. ADD SUBJECT TEXTVIEW
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student_detail, parent, false)
        return HistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return history.size
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val record = history[position]
        holder.dateText.text = record.date
        holder.statusText.text = record.status

        // 4. FIND AND SET THE SUBJECT NAME
        holder.subjectText.text = subjectMap[record.subjectId] ?: "Unknown Subject"

        if (record.status == "Present") {
            holder.statusText.setTextColor(Color.parseColor("#006400")) // Dark Green
        } else {
            holder.statusText.setTextColor(Color.parseColor("#8B0000")) // Dark Red
        }
    }
}