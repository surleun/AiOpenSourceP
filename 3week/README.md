### `Toast.makeText` 설명  

#### `Toast.makeText`란?  
`Toast.makeText`는 **Android에서 간단한 메시지를 사용자에게 보여주는 방법** 중 하나입니다.  

#### 주요 특징  
- **잠시 나타났다 사라지는 팝업 메시지**  
- **사용자 입력 없이 자동으로 사라짐**  
- **앱의 현재 화면 위에 표시됨**  

---

### `Toast.makeText` 문법  

```kotlin
Toast.makeText(context, message, duration).show()
```

| 매개변수 | 설명 |
|---------|------|
| `context` | 현재 앱의 실행 환경을 나타내는 객체 (보통 `applicationContext` 또는 `this` 사용) |
| `message` | 사용자에게 표시할 문자열 메시지 |
| `duration` | 메시지 표시 지속 시간 (`Toast.LENGTH_SHORT` = 2초, `Toast.LENGTH_LONG` = 3.5초) |

---

### `Toast.makeText` 예제  

#### 짧은 메시지 표시 (`2초`)  
```kotlin
Toast.makeText(applicationContext, "이 메시지는 2초 동안 표시됩니다.", Toast.LENGTH_SHORT).show()
```

#### 긴 메시지 표시 (`3.5초`)  
```kotlin
Toast.makeText(applicationContext, "이 메시지는 3.5초 동안 표시됩니다.", Toast.LENGTH_LONG).show()
```

#### 버튼 클릭 시 `Toast` 띄우기  
```kotlin
myButton.setOnClickListener {
    Toast.makeText(this, "버튼이 클릭되었습니다!", Toast.LENGTH_SHORT).show()
}
```

---

### `Toast`의 한계  
1. **사용자 입력을 받을 수 없음**  
   - 단순한 메시지 표시만 가능  
   - 버튼을 포함하려면 `Snackbar` 또는 `Dialog` 사용 필요  

2. **스타일 변경이 어려움**  
   - UI 요소 추가 불가능  
   - `Snackbar` 또는 `Custom Toast` 활용 가능  

---

### `Toast` vs `Snackbar` 비교  

| 기능 | Toast | Snackbar |
|------|------|---------|
| 지속 시간 | 2초 / 3.5초 | 3초 / 무제한 (사용자 액션 가능) |
| 위치 조정 | 기본 위치만 가능 | 화면 하단에 표시 |
| 사용자 입력 | 불가능 | 가능 (예: "실행 취소" 버튼) |
| 스타일링 | 제한적 | 더 많은 커스터마이징 가능 |

**사용자 입력이 필요한 경우 `Snackbar` 사용 추천**  
**간단한 알림이면 `Toast` 사용 가능**  

---

### `Snackbar` 예제  
```kotlin
Snackbar.make(view, "삭제되었습니다.", Snackbar.LENGTH_LONG)
    .setAction("실행 취소") { /* 실행 취소 코드 */ }
    .show()
```

---

## `when` 문 설명  

### `when` 문이란?  
- `switch` 문을 대체하는 **더 강력한 조건문**  
- 다양한 타입(`Int`, `String`, `Boolean` 등)과 표현식을 사용할 수 있음  
- 가독성이 뛰어나고 코드가 간결함  

---

### `when` 문 기본 문법  

```kotlin
when (조건값) {
    값1 -> 실행 코드1
    값2 -> 실행 코드2
    값3 -> 실행 코드3
    else -> 기본 실행 코드
}
```

---

### `when` 문 예제  

#### 숫자에 따른 메시지 출력  
```kotlin
val number = 2

when (number) {
    1 -> println("숫자는 1입니다.")
    2 -> println("숫자는 2입니다.") // 실행됨
    3 -> println("숫자는 3입니다.")
    else -> println("1, 2, 3이 아닙니다.")
}
```

---

#### `when`을 조건식으로 사용하기  
```kotlin
val result = when (val score = 85) {
    in 90..100 -> "A 학점"
    in 80..89 -> "B 학점" // 실행됨
    in 70..79 -> "C 학점"
    else -> "F 학점"
}

println(result) // 출력: "B 학점"
```

---

#### `when`을 함수처럼 반환 값으로 사용하기  
```kotlin
fun getAnimalName(id: Int): String {
    return when (id) {
        1 -> "강아지"
        2 -> "고양이"
        3 -> "토끼"
        else -> "알 수 없음"
    }
}

println(getAnimalName(2)) // 출력: "고양이"
```

---

### `when` 문을 활용한 `Toast` 예제  

```kotlin
checkButton.setOnClickListener {
    when (animalRadioGroup.checkedRadioButtonId) {
        R.id.dog -> imageImageView.setImageResource(R.drawable.dog)
        R.id.cat -> imageImageView.setImageResource(R.drawable.cat)
        R.id.rabbit -> imageImageView.setImageResource(R.drawable.rabbit)
        else -> {
            Toast.makeText(applicationContext, "동물 먼저 선택하세요.", Toast.LENGTH_SHORT).show()

            // 라디오 버튼 체크 해제
            animalRadioGroup.clearCheck()

            // 이미지 초기화
            imageImageView.setImageDrawable(null)
        }
    }
}
```

---

## 결론  
- `Toast.makeText`는 **간단한 알림 메시지**를 표시하는 데 유용  
- `when` 문은 **가독성이 뛰어나고 다양한 조건 처리 가능**  
- `when`과 `Toast`를 함께 사용하여 **UI 동작을 개선할 수 있음**  