package com.sweetspot

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sweetspot.api.MessageResponse
import com.sweetspot.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 엣지 투 엣지 모드 활성화
        enableEdgeToEdge()

        // NavHostFragment 레이아웃 설정
        setContentView(R.layout.activity_main)

        // TextView 바인딩
        textView = findViewById(R.id.textView)

        // 시스템 바(insets) 처리
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        // Retrofit으로 서버 메시지 받아오기
        RetrofitClient.homeApi.getHome().enqueue(object : Callback<MessageResponse> {
            override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                textView.text = response.body()?.message ?: "응답 내용 없음"
            }
            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                textView.text = "네트워크 실패: ${t.message}"
            }
        })
    }
}