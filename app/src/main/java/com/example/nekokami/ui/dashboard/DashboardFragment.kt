package com.example.nekokami.ui.dashboard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        val textView: TextView = binding.textViewFeedbackList
        textView.text = loadFeedbacks()

        return root
    }

    private fun loadFeedbacks(): String {
        val sharedPrefs = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val allFeedbacks = mutableListOf<String>()

        // 過去7日分の感想を取得
        for (i in 0..6) {
            val date = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date(System.currentTimeMillis() - i * 24 * 60 * 60 * 1000))
            val feedbackKey = "feedback_$date"
            val feedback = sharedPrefs.getString(feedbackKey, null)
            if (feedback != null) {
                allFeedbacks.add(feedback)
            }
        }

        return allFeedbacks.joinToString("\n\n") // 感想を改行で結合
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}