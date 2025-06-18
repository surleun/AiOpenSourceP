package com.sweetspot

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PostViewActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_POST_ID = "post_id_long"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_view)

        val postIdString = intent.getStringExtra(EXTRA_POST_ID)
        val postId: Long

        if (postIdString == null) {
            Toast.makeText(this, "게시물 ID가 없습니다.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        try {
            postId = postIdString.toLong()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "잘못된 게시물 ID 형식입니다.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        if (postId == 0L && postIdString != "0") {
            Toast.makeText(this, "유효하지 않은 게시물 ID 입니다.", Toast.LENGTH_LONG).show()
            finish()
            return
        }


        if (savedInstanceState == null) {
            val fragment = PostViewFragment.newInstance(postId)
            supportFragmentManager.beginTransaction()
                .replace(R.id.post_view_fragment_container, fragment)
                .commit()
        }
    }
}