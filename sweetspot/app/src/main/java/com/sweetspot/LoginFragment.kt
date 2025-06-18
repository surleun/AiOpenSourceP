package com.sweetspot

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sweetspot.client.model.LoginRequest
import com.sweetspot.client.model.LoginResponse
import com.sweetspot.client.api.RetrofitClient
import com.sweetspot.databinding.FragmentLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentLoginBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            // 1) 입력값 읽기
            val userId = binding.etUserId.text.toString().trim()
            val pw     = binding.etPassword.text.toString().trim()

            if (userId.isEmpty() || pw.isEmpty()) {
                Toast.makeText(requireContext(), "ID와 비밀번호를 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2) Retrofit 호출 (LoginRequest: email, password)
            val request = LoginRequest(userId, pw)
            RetrofitClient.userApi.login(request)
                .enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        // 응답 코드가 200 OK 이고, body가 null이 아니면 로그인 성공
                        if (response.isSuccessful) {
                            val body = response.body()
                            if (body != null) {
                                Toast.makeText(requireContext(), "로그인 성공: ${body.nickname}님 환영합니다!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(requireContext(), MypageActivity::class.java).apply {
                                    putExtra("userId", body.userId)
                                    putExtra("nickname", body.nickname)
                                    putExtra("profileImageUrl", body.profileImageUrl)
                                }
                                startActivity(intent)
                                // 예: HomeFragment로 이동 (네비게이션 컴포넌트 사용)
                                findNavController().navigate(R.id.action_login_to_homeActivity)
                            } else {
                                // 서버가 200을 주었지만 body가 비어있다면
                                Toast.makeText(requireContext(), "서버 응답이 없습니다.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            // 401 Unauthorized 등 실패 응답일 경우
                            when (response.code()) {
                                401 -> Toast.makeText(requireContext(), "이메일 또는 비밀번호가 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
                                else -> Toast.makeText(requireContext(), "로그인 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        // 네트워크 연결 실패 등
                        Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        binding.btnSignUp.setOnClickListener {
            // 회원가입 화면으로 이동
            findNavController().navigate(R.id.action_login_to_signUp)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
