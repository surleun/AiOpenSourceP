package com.sweetspot

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class UserPinListActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_USER_ID = "extra_user_id_for_pins"
    }

    private lateinit var rvUserPins: RecyclerView
    private lateinit var pinAdapter: PinAdapter
    private var targetUserId: Long = 0L

    private val allSampleData = listOf(
        PinItem(201L, 1, "내가 저장한 첫 핀", "핀내용1...", 37.0, 127.0, "2024-05-10 14:30:00", "나(핀)", null, "https://picsum.photos/seed/mypin1/200", 1, 2),
        PinItem(202L, 1, "나의 두 번째 핀", "핀내용2...", 37.1, 127.1, "2024-05-11 15:00:00", "나(핀)", null, "https://picsum.photos/seed/mypin2/200", 0, 0),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_pin_list)

        targetUserId = intent.getLongExtra(EXTRA_USER_ID, 0L)
        rvUserPins = findViewById(R.id.rvUserPinsList)

        setupRecyclerView()
        loadUserPins()
    }

    private fun setupRecyclerView() {
        pinAdapter = PinAdapter(emptyList(), object : PinAdapter.OnPinItemClickListener {
            override fun onPinItemClicked(pin: PinItem) {
                val intent = Intent(this@UserPinListActivity, PinViewActivity::class.java)
                intent.putExtra(PinViewActivity.EXTRA_PIN_ID, pin.id)
                startActivity(intent)
            }
        })
        rvUserPins.layoutManager = LinearLayoutManager(this)
        rvUserPins.adapter = pinAdapter
    }

    private fun loadUserPins() {
        val userPins = allSampleData.filter { it.userId == targetUserId }.sortedByDescending { it.id }
        pinAdapter.updateData(userPins)
    }
}