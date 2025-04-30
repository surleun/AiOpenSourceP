package com.sweetspot

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sweetspot.api.RetrofitClient
import com.sweetspot.api.SignUpRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var nicknameEditText: EditText
    private lateinit var phoneNumberEditText: EditText
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up)

        emailEditText = findViewById(R.id.email)
        passwordEditText = findViewById(R.id.password)
        nicknameEditText = findViewById(R.id.nickname)
        phoneNumberEditText = findViewById(R.id.phone_number)
        submitButton = findViewById(R.id.submit)

        submitButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val nickname = nicknameEditText.text.toString()
            val phoneNumber = phoneNumberEditText.text.toString()

            val signUpRequest = SignUpRequest(email, password, nickname, phoneNumber)

            RetrofitClient.api.signUp(signUpRequest).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        // 회원가입 성공 처리
                        Toast.makeText(this@SignUpActivity, "회원가입이 완료되었습니다!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish() // 회원가입 후 화면 닫기 (필요시 MainActivity로 이동도 가능)
                    } else {
                        // 오류 처리 (예: 이미 존재하는 이메일, 잘못된 입력 등)
                        Toast.makeText(this@SignUpActivity, "회원가입 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                        Log.e("SignUp", "회원가입 실패 - 상태 코드: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    // 네트워크 실패 처리
                    Toast.makeText(this@SignUpActivity, "서버 연결 실패: ${t.message}", Toast.LENGTH_LONG).show()
                    Log.e("SignUp", "네트워크 오류", t)
                }
            })
        }
    }
}
