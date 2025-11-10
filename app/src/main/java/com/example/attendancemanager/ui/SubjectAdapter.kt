package com.example.attendancemanager.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.attendancemanager.R
import com.example.attendancemanager.data.Subject

// 1. ADD THE CLICK LISTENER TO THE CONSTRUCTOR
class SubjectAdapter(
    private val subjectList: List<Subject>,
    private val onDeleteClicked: (Subject) -> Unit
) : RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder>() {

    // 2. ADD 'deleteIcon' TO THE VIEWHOLDER
    class SubjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val subjectName: TextView = itemView.findViewById(R.id.tvSubjectName)
        val deleteIcon: ImageView = itemView.findViewById(R.id.ivDeleteSubject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_subject, parent, false)
        return SubjectViewHolder(view)
    }

    override fun getItemCount(): Int {
        return subjectList.size
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val subject = subjectList[position]
        holder.subjectName.text = subject.subjectName

        // 3. SET THE CLICK LISTENER
        holder.deleteIcon.setOnClickListener {
            onDeleteClicked(subject)
        }
    }
}