package com.example.nekokami.ui.home

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.nekokami.R
import com.example.nekokami.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val handler = Handler(Looper.getMainLooper())
    private var messageIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val textView: TextView = binding.homeTextMessage
        val constraintLayout: ConstraintLayout = binding.root
        val button3: Button = binding.button3
        val button: Button = binding.button // 「できた」ボタンを取得

        // ボタンを最初に非表示にする
        button.visibility = View.GONE

        val messages = arrayOf(
            "こんにちは！\nぼくは猫神様！",
            "生まれたばかりの神様なんだ！",
            "ところで君\n人生変える覚悟ある？？",
            "まあ、選択肢なんて\nないんだけどね♪ﾆｬﾊ",
            "今日から君に一日一つ\n課題を出すよ！",
            "その課題をやればやるほど\nぼくも君も、\n成長していくんだ！",
            "課題を達成したら、\n「できた」ボタンを押してね！",
            "嘘をついたり、\nさぼったりしたら、\nどうなるかわかってるよね...？",
            "下の「契約」ボタンを押して\n僕と契約しよう！",
            "ありがとう！\n今日からよろしくね！",
            "それじゃあ、\n今日の課題を発表するね！",
            "今日の課題は\n【${getDailyTask()}】\nだよ！達成できるかな？"
        )

        val sharedPrefs = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val isFirstLaunch = sharedPrefs.getBoolean("isFirstLaunch", true)

        if (isFirstLaunch) {
            displayMessage(textView, constraintLayout, button3, button, messages, isFirstLaunch)
        } else {
            displayDailyTask(textView, constraintLayout, button)
        }

        button.setOnClickListener {
            showFeedbackDialog()
        }

        return root
    }

    private fun displayMessage(textView: TextView, layout: ConstraintLayout, button3: Button, button: Button, messages: Array<String>, isFirstLaunch: Boolean = false) {
        val runnable = object : Runnable {
            var charIndex = 0
            override fun run() {
                if (charIndex < messages[messageIndex].length) {
                    // \n を <br> に置換してから HtmlCompat.fromHtml() に渡す
                    textView.text = HtmlCompat.fromHtml(
                        messages[messageIndex].substring(0, charIndex + 1).replace("\n", "<br>"),
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
                    charIndex++
                    handler.postDelayed(this, 50)
                    layout.setOnClickListener(null)
                } else {
                    // 現在のセリフの表示が完了

                    // 初回起動時 かつ 契約ボタンを押す直前のセリフの場合のみボタンを表示
                    if (isFirstLaunch && messageIndex == 8) {
                        button3.visibility = View.VISIBLE
                        button3.setOnClickListener {
                            button3.visibility = View.INVISIBLE
                            messageIndex++
                            if (messageIndex < messages.size) {
                                charIndex = 0
                                textView.text = ""
                                displayMessage(textView, layout, button3, button, messages, false)
                            }
                            // すべてのセリフを表示し終わったら、初回起動フラグをfalseに
                            val sharedPrefs = requireActivity().getPreferences(Context.MODE_PRIVATE)
                            sharedPrefs.edit().putBoolean("isFirstLaunch", false).apply()
                        }
                    } else if (messageIndex > 10) { // 初回起動時の最後のセリフの場合
                        handler.postDelayed({
                            button.visibility = View.VISIBLE
                        }, 0) // 1秒後に表示
                    } else { // それ以外の場合は画面タップで次に進む
                        layout.setOnClickListener {
                            messageIndex++
                            if (messageIndex < messages.size) {
                                charIndex = 0
                                textView.text = ""
                                handler.post(this)
                            }
                        }
                    }
                }
            }
        }
        handler.post(runnable)
    }

    private fun displayDailyTask(textView: TextView, layout: ConstraintLayout, button: Button) {
        val dailyTask = getDailyTask()

        val messages = arrayOf(
            "やっほー！\nまた会ったね！",
            "今日の課題は\n【${dailyTask}】\nだよ！達成できるかな？"
        )
        messageIndex = 0
        displayMessage(textView, layout, binding.button3, button, messages, false)

        // 課題発表の後にボタンを表示
        handler.postDelayed({
            button.visibility = View.VISIBLE
        }, 0) // 1秒後に表示 (アニメーション表示が終わるのを待つ)
    }

    private fun getDailyTask(): String {
        val sharedPrefs = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val lastTaskDate = sharedPrefs.getString("lastTaskDate", "") ?: ""
        val lastThreeTasks = sharedPrefs.getStringSet("lastThreeTasks", mutableSetOf()) ?: mutableSetOf()

        val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        if (lastTaskDate == today) {
            // 今日の課題はすでに表示済みなので、保存されている課題を返す
            return sharedPrefs.getString("dailyTask", "") ?: ""
        } else {
            // 新しい課題を選択
            val availableTasks = resources.getStringArray(R.array.tasks).toMutableList()
            availableTasks.removeAll(lastThreeTasks) // 過去3日間の課題を除外

            val randomTask = availableTasks.random() // ランダムに課題を選択

            // 今日の課題と日付を保存
            sharedPrefs.edit()
                .putString("dailyTask", randomTask)
                .putString("lastTaskDate", today)
                .putStringSet("lastThreeTasks", (lastThreeTasks + randomTask).toMutableSet())
                .apply()

            return randomTask
        }
    }

    private fun showFeedbackDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_feedback, null)
        val editTextFeedback = dialogLayout.findViewById<EditText>(R.id.editTextFeedback)

        with(builder) {
            setTitle("感想")
            setView(dialogLayout)
            setPositiveButton("OK") { _, _ ->
                val feedback = editTextFeedback.text.toString()
                // TODO: 入力された感想を処理する (例: ログ出力、保存など)
            }
            setNegativeButton("キャンセル") { _, _ ->
                // キャンセルボタンを押した時の処理
            }
            show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        handler.removeCallbacksAndMessages(null)
    }
}