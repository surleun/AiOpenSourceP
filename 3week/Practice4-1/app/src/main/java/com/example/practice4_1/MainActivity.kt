package com.example.practice4_1  // 패키지 선언

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

// MainActivity 클래스: AppCompatActivity를 상속받아 메인 액티비티로 사용됨
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // 액티비티 생성 시 호출됨
        enableEdgeToEdge() // 엣지 투 엣지(Edge-to-Edge) UI 적용 (상태 바와 내비게이션 바를 포함한 전체 화면 사용)

        setContentView(R.layout.activity_main) // activity_main.xml 레이아웃을 화면에 표시

        // 시스템 바(상태 바, 내비게이션 바) 영역을 고려하여 레이아웃 패딩 적용
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars()) // 시스템 바의 여백 가져오기
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom) // 해당 여백만큼 패딩 적용
            insets // 변경된 insets 반환
        }

        // UI 요소 가져오기
        val number1 = findViewById<EditText>(R.id.number1)
        val number2 = findViewById<EditText>(R.id.number2)
        val resultView = findViewById<TextView>(R.id.result)

        val addButton = findViewById<Button>(R.id.add)
        val subtractButton = findViewById<Button>(R.id.subtract)
        val multiplyButton = findViewById<Button>(R.id.multiply)
        val divideButton = findViewById<Button>(R.id.divide)

        // 연산 함수 정의
        // operation: (Double, Double) -> Double 형식의 람다 함수(연산 방식)를 매개변수로 받음
        fun calculate(operation: (Double, Double) -> Double) {
            // 첫 번째 입력 필드에서 사용자가 입력한 문자열을 가져와 Double로 변환
            // 숫자가 아닌 경우(null 반환) 예외를 방지하기 위해 toDoubleOrNull() 사용
            val num1 = number1.text.toString().toDoubleOrNull()
            val num2 = number2.text.toString().toDoubleOrNull()

            if (num1 != null && num2 != null) {
                resultView.text = getString(R.string.result, operation(num1, num2))
            } else {
                resultView.text = getString(R.string.input_error)
            }
        }

        // 버튼 클릭 리스너 설정
        addButton.setOnClickListener { calculate { a, b -> a + b } } // 덧셈 버튼 눌렀을 때, 덧셈 연산 수행하는 람다 함수 전달
        subtractButton.setOnClickListener { calculate { a, b -> a - b } } // 뺄셈 버튼 눌렀을 때, 뺄셈 연산 수행하는 람다 함수 전달
        multiplyButton.setOnClickListener { calculate { a, b -> a * b } }
        divideButton.setOnClickListener {
            calculate { a, b -> if (b != 0.0) a / b else Double.NaN } // 분모(b)가 0일 경우 NaN을 반환하여 오류 방지
        }
    }
}

