package com.gen.SweetSpot

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PinViewActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PIN_ID = "pin_id_long"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_view)

        val pinId = intent.getLongExtra(EXTRA_PIN_ID, 0L)

        if (pinId == 0L) {
            Toast.makeText(this, "핀 ID가 없습니다.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        if (savedInstanceState == null) {
            val fragment = PinViewFragment.newInstance(pinId)
            supportFragmentManager.beginTransaction()
                .replace(R.id.pin_view_fragment_container, fragment)
                .commit()
        }
    }
}