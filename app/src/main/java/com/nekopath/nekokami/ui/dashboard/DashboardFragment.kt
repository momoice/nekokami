package com.nekopath.nekokami.ui.dashboard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.nekopath.nekokami.databinding.FragmentDashboardBinding
import java.text.SimpleDateFormat
import java.util.*

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    data class FeedbackData(val date: String, val task: String, val feedback: String, val completionStatus: String)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val calendarView: CalendarView = binding.calendarView
        val selectedDateTextView: TextView = binding.textViewSelectedDate
        val taskTextView: TextView = binding.textViewTask
        val completionStatusTextView: TextView = binding.textViewCompletionStatus
        val feedbackTextView: TextView = binding.textViewFeedback

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            val formattedDate = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(selectedDate.time)
            selectedDateTextView.text = formattedDate

            val feedbackData = loadFeedbackForDate(formattedDate)
            taskTextView.text = "課題: ${feedbackData?.task ?: "課題なし"}"

            // 達成状況の表示
            if (!feedbackData?.completionStatus.isNullOrEmpty()) {
                completionStatusTextView.text = "達成状況: ${feedbackData?.completionStatus}"
                completionStatusTextView.visibility = View.VISIBLE
            } else {
                completionStatusTextView.visibility = View.GONE
            }

            // 感想の表示
            if (!feedbackData?.feedback.isNullOrEmpty()) {
                feedbackTextView.text = "感想: ${feedbackData?.feedback}"
                feedbackTextView.visibility = View.VISIBLE
            } else {
                feedbackTextView.visibility = View.GONE
            }
        }

        return root
    }

    private fun loadFeedbackForDate(date: String): FeedbackData? {
        val sharedPrefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val feedbackKey = "feedback_$date"
        val feedback = sharedPrefs.getString(feedbackKey, null)
        val taskKey = "task_$date"
        val task = sharedPrefs.getString(taskKey, "課題なし") ?: "課題なし"

        val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val selectedDate = sdf.parse(date)
        val todayDate = sdf.parse(sdf.format(Date()))

        if (selectedDate != null) {
            when {
                selectedDate.after(todayDate) -> {
                    // 未来の日付の場合、達成状況と感想を空白にする
                    return FeedbackData(date, task, "", "")
                }
                selectedDate.before(todayDate) -> {
                    // 過去の日付の場合
                    val completionStatus = if (feedback != null) "達成！" else "達成できず..."
                    val feedbackText = feedback ?: ""
                    return FeedbackData(date, task, feedbackText, completionStatus)
                }
                else -> {
                    // 今日の日付の場合
                    val completionStatus = if (feedback != null) "達成！" else "課題進行中"
                    val feedbackText = feedback ?: ""
                    return FeedbackData(date, task, feedbackText, completionStatus)
                }
            }
        }
        return null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
