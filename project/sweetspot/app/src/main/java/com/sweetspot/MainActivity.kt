package com.sweetspot

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.textView) // xml에 정의된 TextView를 가져옴

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        RetrofitClient.api.getHome().enqueue(object : Callback<MessageResponse> {
            override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                if (response.isSuccessful) {
                    textView.text = response.body()?.message ?: "응답 내용 없음"
                } else {
                    textView.text = "서버 응답 오류: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                textView.text = "네트워크 실패: ${t.message}"
            }
        })

    }
}
