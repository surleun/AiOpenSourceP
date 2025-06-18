package com.sweetspot

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class UserPostListActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_USER_ID = "extra_user_id"
    }

    private lateinit var rvUserPosts: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private var userId: Long = 0L

    private val allSampleData = listOf(
        PostItem(1L, 1, "내가 쓴 첫 게시물", "내용1...", "2024-06-01 10:00:00", 10, "나", null, "https://picsum.photos/seed/mypost1/200", 5, 12),
        PostItem(2L, 1, "내가 쓴 두 번째 이야기", "내용2...", "2024-06-02 11:00:00", 5, "나", null, null, 2, 3),
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_post_list)

        userId = intent.getLongExtra(EXTRA_USER_ID, 0L)
        rvUserPosts = findViewById(R.id.rvUserPostsList)

        setupRecyclerView()
        loadUserPosts()
    }

    private fun setupRecyclerView() {
        postAdapter = PostAdapter(emptyList(), object : PostAdapter.OnPostItemClickListener {
            override fun onPostItemClicked(post: PostItem) {
                val intent = Intent(this@UserPostListActivity, PostViewActivity::class.java)
                intent.putExtra(PostViewActivity.EXTRA_POST_ID, post.id)
                startActivity(intent)
            }
        })
        rvUserPosts.layoutManager = LinearLayoutManager(this)
        rvUserPosts.adapter = postAdapter
    }

    private fun loadUserPosts() {
        val userPosts = allSampleData.filterIsInstance<PostItem>().filter { it.userId == userId }.sortedByDescending { it.id }
        postAdapter.updateData(userPosts)
    }
}