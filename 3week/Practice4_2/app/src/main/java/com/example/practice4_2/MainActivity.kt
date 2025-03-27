package com.example.practice4_2

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var animalTextView: TextView
    private lateinit var agreeCheckBox: CheckBox
    private lateinit var animalRadioGroup: RadioGroup
    private lateinit var dogRadioBUtton: RadioButton
    private lateinit var catRadioButton: RadioButton
    private lateinit var rabbitRadioButton: RadioButton
    private lateinit var checkButton: Button
    private lateinit var imageImageView: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 위젯을 변수에 대입
        animalTextView = findViewById(R.id.animal_text)
        agreeCheckBox = findViewById(R.id.check_agree)
        animalRadioGroup = findViewById(R.id.animal_group)
        dogRadioBUtton = findViewById(R.id.dog)
        catRadioButton = findViewById(R.id.cat)
        rabbitRadioButton = findViewById(R.id.rabbit)
        checkButton = findViewById(R.id.check)
        imageImageView = findViewById(R.id.image)

        // agreeCheckBox 체크 상태 변경 리스너
        agreeCheckBox.setOnCheckedChangeListener{_, isChecked ->
            if(isChecked){
                // 체크되면 모든 요소를 보이도록 설정
                animalTextView.visibility = View.VISIBLE
                animalRadioGroup.visibility = View.VISIBLE
                checkButton.visibility = View.VISIBLE
                imageImageView.visibility = View.VISIBLE
            } else{
                // 체크 해제되면 모든 요소를 숨김
                animalTextView.visibility = View.INVISIBLE
                animalRadioGroup.visibility = View.INVISIBLE
                checkButton.visibility = View.INVISIBLE
                imageImageView.visibility = View.INVISIBLE
                animalRadioGroup.clearCheck() // 라디오 버튼 선택 해제
                imageImageView.setImageDrawable(null) // 이미지 null
            }
        }

        // checkButton 클릭 리스너
        checkButton.setOnClickListener{
            when(animalRadioGroup.checkedRadioButtonId){ // 선택된 라디오 버튼의 ID를 기준으로 분기 처리
                R.id.dog -> imageImageView.setImageResource(R.drawable.dog)
                R.id.cat -> imageImageView.setImageResource(R.drawable.cat)
                R.id.rabbit -> imageImageView.setImageResource(R.drawable.rabbit)
                else -> Toast.makeText(applicationContext, "동물 먼저 선택하세요.", Toast.LENGTH_SHORT).show() // 아무것도 선택되지 않았다면 사용자에게 경고 메시지 표시
            }
        }
    }
}