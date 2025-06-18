package com.sweetspot

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sweetspot.client.api.RetrofitClient
import com.sweetspot.databinding.FragmentSignUpBinding
import com.sweetspot.client.model.CheckEmailRequest
import com.sweetspot.client.model.CheckEmailResponse
import com.sweetspot.client.model.RegisterRequest
import com.sweetspot.client.model.RegisterResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private var isEmailUnique = false
    private var selectedImageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1001

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1) 이메일 중복 확인
        binding.btnCheckEmail.setOnClickListener {
            val email = binding.etSignUpEmail.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(requireContext(), "이메일을 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            RetrofitClient.userApi.checkEmail(CheckEmailRequest(email))
                .enqueue(object : Callback<CheckEmailResponse> {
                    override fun onResponse(
                        call: Call<CheckEmailResponse>,
                        response: Response<CheckEmailResponse>
                    ) {
                        if (response.isSuccessful) {
                            val body = response.body()
                            if (body != null && body.available) {
                                Toast.makeText(requireContext(), "사용 가능한 이메일입니다", Toast.LENGTH_SHORT).show()
                                isEmailUnique = true
                            } else {
                                Toast.makeText(requireContext(), "이미 사용 중인 이메일입니다", Toast.LENGTH_SHORT).show()
                                isEmailUnique = false
                            }
                        } else {
                            Toast.makeText(requireContext(), "서버 오류: ${response.code()}", Toast.LENGTH_SHORT).show()
                            isEmailUnique = false
                        }
                    }
                    override fun onFailure(call: Call<CheckEmailResponse>, t: Throwable) {
                        Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                        isEmailUnique = false
                    }
                })
        }

        // 2) 프로필 사진 선택
        binding.ivProfilePhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // 3) 회원가입
        binding.btnSubmitSignUp.setOnClickListener {
            val nickname = binding.etSignUpNickname.text.toString().trim()
            val email = binding.etSignUpEmail.text.toString().trim()
            val password = binding.etSignUpPassword.text.toString()
            val confirmPassword = binding.etSignUpConfirmPassword.text.toString()

            // 유효성 검사
            if (nickname.isEmpty()) {
                Toast.makeText(requireContext(), "닉네임을 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                Toast.makeText(requireContext(), "이메일을 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!isEmailUnique) {
                Toast.makeText(requireContext(), "이메일 중복 확인을 해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.length < 4) {
                Toast.makeText(requireContext(), "비밀번호는 4자 이상이어야 합니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                Toast.makeText(requireContext(), "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 3-1) JSON 데이터를 RequestBody로 변환
            val json = """{
                "email":"$email",
                "password":"$password",
                "confirmPassword":"$confirmPassword",
                "nickname":"$nickname"
            }""".trimIndent()
            val dataPart: RequestBody = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            // 3-2) 이미지 파일이 선택되지 않았다면 profilePart = null
            var profilePart: MultipartBody.Part? = null
            selectedImageUri?.let { uri ->
                val file = uriToFile(uri)
                file?.let {
                    val mime = requireContext().contentResolver.getType(uri)
                        ?: "image/*"
                    val requestFile = it.asRequestBody(mime.toMediaTypeOrNull())
                    val filename = it.name
                    profilePart = MultipartBody.Part.createFormData("profileImage", filename, requestFile)
                }
            }

            // 3-3) Retrofit 호출
            RetrofitClient.userApi.registerWithImage(dataPart, profilePart)
                .enqueue(object : Callback<RegisterResponse> {
                    override fun onResponse(
                        call: Call<RegisterResponse>,
                        response: Response<RegisterResponse>
                    ) {
                        if (response.isSuccessful) {
                            val body = response.body()
                            if (body != null) {
                                Toast.makeText(
                                    requireContext(),
                                    "회원가입 성공! 환영합니다, ${body.nickname}님.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                findNavController().navigateUp()
                            } else {
                                Toast.makeText(requireContext(), "서버 응답이 없습니다.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            when (response.code()) {
                                400 -> Toast.makeText(requireContext(), "비밀번호 확인이 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                                409 -> Toast.makeText(requireContext(), "이미 사용 중인 이메일입니다.", Toast.LENGTH_SHORT).show()
                                else -> Toast.makeText(requireContext(), "회원가입 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                        Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    // 갤러리에서 선택한 URI를 실제 File로 변환하는 유틸 함수
    private fun uriToFile(uri: Uri): File? {
        val context = requireContext()
        val returnCursor = context.contentResolver.query(uri, null, null, null, null)
        val nameIndex = returnCursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME) ?: -1
        returnCursor?.moveToFirst()
        val filename = if (nameIndex >= 0 && returnCursor != null) {
            returnCursor.getString(nameIndex)
        } else {
            "temp_image"
        }
        returnCursor?.close()

        val tempFile = File(context.cacheDir, filename)
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(tempFile)
            inputStream?.copyTo(outputStream)
            outputStream.close()
            inputStream?.close()
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                selectedImageUri = uri
                binding.ivProfilePhoto.setImageURI(uri)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
