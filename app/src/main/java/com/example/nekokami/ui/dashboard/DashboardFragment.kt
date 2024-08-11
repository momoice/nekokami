package com.example.nekokami.ui.dashboard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.nekokami.databinding.FragmentDashboardBinding
import java.text.SimpleDateFormat
import java.util.*

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val calendarView: CalendarView = binding.calendarView
        val selectedDateTextView: TextView = binding.textViewSelectedDate
        val taskTextView: TextView = binding.textViewTask
        val feedbackTextView: TextView = binding.textViewFeedback

        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            val formattedDate = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(selectedDate.time)
            selectedDateTextView.text = formattedDate

            val feedbackData = loadFeedbackForDate(formattedDate)
            taskTextView.text = "課題: ${feedbackData?.task ?: ""}"
            feedbackTextView.text = "感想: ${feedbackData?.feedback ?: ""}"
        }

        return root
    }

    private fun loadFeedbackForDate(date: String): FeedbackData? {
        val sharedPrefs = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val feedbackKey = "feedback_$date"
        val feedback = sharedPrefs.getString(feedbackKey, null)

        return if (feedback != null) {
            val parts = feedback.split("\n")
            val task = parts[0]
            val feedbackText = parts.drop(1).joinToString("\n")
            FeedbackData(date, task, feedbackText)
        } else {
            null
        }
    }

    data class FeedbackData(val date: String, val task: String, val feedback: String)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}