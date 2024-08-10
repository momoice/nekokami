package com.example.nekokami.ui.home

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        val constraintLayout: ConstraintLayout = binding.root // ConstraintLayoutを取得

        val messages = arrayOf(
            "こんにちは！\nぼくは猫神様！",
            "生まれたばかりの神様なんだ！",
            "ところで君\n人生変える覚悟ある？？",
            "まあ、選択肢なんて\nないんだけどね♪ﾆｬﾊ",
            "今日から君に一日一つ\n課題を出すよ！",
            "その課題をやればやるほど\nぼくも君も、成長していくんだ！",
            "課題を達成したら、\n「できた」ボタンを押してね！",
            "嘘をついたり、さぼったりしたら\nどうなるかわかってるよね...？",
            "それじゃあ、今日からよろしくね！"
        )

        val sharedPrefs = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val isFirstLaunch = sharedPrefs.getBoolean("isFirstLaunch", true)

        if (isFirstLaunch) {
            displayMessage(textView, constraintLayout, messages) // ConstraintLayoutを渡す
        } else {
            // 初回起動でない場合の処理 (課題表示など)
        }

        return root
    }

    private fun displayMessage(textView: TextView, layout: ConstraintLayout, messages: Array<String>) {
        val runnable = object : Runnable {
            var charIndex = 0
            override fun run() {
                if (charIndex < messages[messageIndex].length) {
                    textView.text = messages[messageIndex].substring(0, charIndex + 1)
                    charIndex++
                    handler.postDelayed(this, 50) // 50ミリ秒ごとに次の文字を表示
                } else {
                    // 現在のセリフの表示が完了
                    layout.setOnClickListener {
                        messageIndex++
                        if (messageIndex < messages.size) {
                            charIndex = 0 // 次のセリフの表示を初期化
                            textView.text = "" // TextViewをクリア
                            handler.post(this) // 次のセリフの表示を開始
                        } else {
                            // すべてのセリフを表示し終わったら、初回起動フラグをfalseに
                            val sharedPrefs = requireActivity().getPreferences(Context.MODE_PRIVATE)
                            sharedPrefs.edit().putBoolean("isFirstLaunch", false).apply()
                            layout.setOnClickListener(null) // タップリスナーを解除
                            // 以降の処理 (課題表示など)
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
        handler.removeCallbacksAndMessages(null) // Handlerの処理を停止
    }
}