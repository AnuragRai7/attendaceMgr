package com.example.attendancemanager.ui

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.attendancemanager.R
import com.example.attendancemanager.data.AttendanceReport

class ReportAdapter(private val reportList: List<AttendanceReport>) :
    RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val studentName: TextView = itemView.findViewById(R.id.tvStudentNameReport)
        val attendanceFraction: TextView = itemView.findViewById(R.id.tvAttendanceFraction)
        val percentage: TextView = itemView.findViewById(R.id.tvPercentage)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_report, parent, false)
        return ReportViewHolder(view)
    }

    override fun getItemCount(): Int {
        return reportList.size
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = reportList[position]
        holder.studentName.text = report.studentName
        holder.attendanceFraction.text = "Attended: ${report.presentCount} / ${report.totalCount} days"

        val percentage = if (report.totalCount > 0) {
            (report.presentCount.toDouble() / report.totalCount.toDouble()) * 100
        } else {
            0.0
        }

        holder.percentage.text = "${String.format("%.0f", percentage)}%"
        holder.progressBar.progress = percentage.toInt()

        // *** ADD THIS CLICK LISTENER ***
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            // Create an intent to open the StudentDetailActivity
            val intent = Intent(context, StudentDetailActivity::class.java).apply {
                // Pass the student's ID and Name to the new activity
                putExtra("STUDENT_ID", report.studentId)
                putExtra("STUDENT_NAME", report.studentName)
            }
            context.startActivity(intent)
        }
    }
}