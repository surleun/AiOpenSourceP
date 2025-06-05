package com.gen.SweetSpot

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
        PostItem(1L, 101L, "내가 쓴 첫 게시물", "내용1...", "2024-06-01 10:00:00", 10, "나", null, "https://picsum.photos/seed/mypost1/200", 5, 12),
        PostItem(2L, 101L, "내가 쓴 두 번째 이야기", "내용2...", "2024-06-02 11:00:00", 5, "나", null, null, 2, 3),
        PostItem(3L, 202L, "다른 사람의 게시물", "타인내용...", "2024-06-03 12:00:00", 20, "타인", null, "https://picsum.photos/seed/otherpost/200", 10, 20),
        PostItem(4L, 101L, "내가 쓴 또 다른 글 (가장 최근)", "내용4...", "2024-06-04 13:00:00", 8, "나", null, "https://picsum.photos/seed/mypost3/200", 8, 15)
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