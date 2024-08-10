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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.nekokami.databinding.FragmentHomeBinding

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
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val textView: TextView = binding.homeTextMessage
        val constraintLayout: ConstraintLayout = binding.root
        val button3: Button = binding.button3

        val messages = arrayOf(
            "こんにちは！\nぼくは猫神様！",
            "生まれたばかりの神様なんだ！",
            "ところで君\n人生変える覚悟ある？？",
            "まあ、選択肢なんて\nないんだけどね♪ﾆｬﾊ",
            "今日から君に一日一つ\n課題を出すよ！",
            "その課題をやればやるほど\nぼくも君も、成長していくんだ！",
            "課題を達成したら、\n「できた」ボタンを押してね！",
            "嘘をついたり、さぼったりしたら\nどうなるかわかってるよね...？",
            "それじゃあ、今日からよろしくね！",
            "下の「契約」ボタンを押して\n僕と契約しよう！",
            "それじゃあ、\n今日の課題を発表するよ！"
        )

        val sharedPrefs = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val isFirstLaunch = sharedPrefs.getBoolean("isFirstLaunch", true)

        if (isFirstLaunch) {
            displayMessage(textView, constraintLayout, button3, messages)
        } else {
            // 初回起動でない場合の処理 (課題表示など)
        }

        return root
    }

    private fun displayMessage(textView: TextView, layout: ConstraintLayout, button: Button, messages: Array<String>) {
        val runnable = object : Runnable {
            var charIndex = 0
            override fun run() {
                if (charIndex < messages[messageIndex].length) {
                    textView.text = messages[messageIndex].substring(0, charIndex + 1)
                    charIndex++
                    handler.postDelayed(this, 50)
                } else {
                    // 現在のセリフの表示が完了

                    if (messageIndex == messages.size - 2) { // "契約"ボタンを押すセリフの場合
                        button.visibility = View.VISIBLE
                        button.setOnClickListener {
                            button.visibility = View.INVISIBLE
                            messageIndex++
                            if (messageIndex < messages.size) { // 範囲チェック
                                charIndex = 0
                                textView.text = ""
                                displayMessage(textView, layout, button, messages) // 次のセリフを表示開始
                            }
                            // すべてのセリフを表示し終わったら、初回起動フラグをfalseに
                            val sharedPrefs = requireActivity().getPreferences(Context.MODE_PRIVATE)
                            sharedPrefs.edit().putBoolean("isFirstLaunch", false).apply()
                        }
                    } else {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        handler.removeCallbacksAndMessages(null)
    }
}