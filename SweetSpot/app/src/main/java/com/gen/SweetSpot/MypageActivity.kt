package com.gen.SweetSpot

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView

class MypageActivity : AppCompatActivity() {

    private lateinit var mypageProfileImage: ShapeableImageView
    private lateinit var mypageNickname: TextView
    private lateinit var rvMyPosts: RecyclerView
    private lateinit var rvMyPins: RecyclerView
    private lateinit var headerMyPostsTitle: TextView
    private lateinit var headerMyPinsTitle: TextView

    private val currentUserId = 101L
    private lateinit var myPostsAdapter: PostAdapter
    private lateinit var myPinsAdapter: PinAdapter

    private val allSampleData = listOf(
        PostItem(1L, 101L, "내가 쓴 첫 게시물", "내용1...", "2024-06-01 10:00:00", 10, "나", null, "https://picsum.photos/seed/mypost1/200", 5, 12),
        PostItem(2L, 101L, "내가 쓴 두 번째 이야기", "내용2...", "2024-06-02 11:00:00", 5, "나", null, null, 2, 3),
        PostItem(3L, 202L, "다른 사람의 게시물", "타인내용...", "2024-06-03 12:00:00", 20, "타인", null, "https://picsum.photos/seed/otherpost/200", 10, 20),
        PinItem(201L, 101L, "내가 저장한 첫 핀", "핀내용1...", 37.0, 127.0, "2024-05-10 14:30:00", "나(핀)", null, "https://picsum.photos/seed/mypin1/200", 1, 2),
        PinItem(202L, 101L, "나의 두 번째 핀", "핀내용2...", 37.1, 127.1, "2024-05-11 15:00:00", "나(핀)", null, "https://picsum.photos/seed/mypin2/200", 0, 0),
        PinItem(203L, 404L, "다른 사람의 핀", "타인핀내용...", 37.2, 127.2, "2024-05-12 16:00:00", "타인(핀)", null, "https://picsum.photos/seed/otherpin/200", 3, 5),
        PostItem(4L, 101L, "내가 쓴 또 다른 글 (가장 최근)", "내용4...", "2024-06-04 13:00:00", 8, "나", null, "https://picsum.photos/seed/mypost3/200", 8, 15)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        mypageProfileImage = findViewById(R.id.mypage_profile_image)
        mypageNickname = findViewById(R.id.mypage_nickname)
        rvMyPosts = findViewById(R.id.rvMyPosts)
        rvMyPins = findViewById(R.id.rvMyPins)
        headerMyPostsTitle = findViewById(R.id.headerMyPostsTitle)
        headerMyPinsTitle = findViewById(R.id.headerMyPinsTitle)

        loadUserProfile()
        setupRecyclerViews()
        loadMyData(2)

        headerMyPostsTitle.setOnClickListener {
            val intent = Intent(this, UserPostListActivity::class.java)
            intent.putExtra(UserPostListActivity.EXTRA_USER_ID, currentUserId)
            startActivity(intent)
        }

        headerMyPinsTitle.setOnClickListener {
            val intent = Intent(this, UserPinListActivity::class.java)
            intent.putExtra(UserPinListActivity.EXTRA_USER_ID, currentUserId)
            startActivity(intent)
        }


        val navMap = findViewById<LinearLayout>(R.id.nav_map)
        val navBoard = findViewById<LinearLayout>(R.id.nav_board)
        val navProfile = findViewById<LinearLayout>(R.id.nav_profile)

        navProfile.setBackgroundResource(R.drawable.nav_active_background)
        navMap.setBackgroundResource(R.drawable.nav_inactive_background)
        navBoard.setBackgroundResource(R.drawable.nav_inactive_background)

        navMap.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
            finish()
        }

        navBoard.setOnClickListener {
            val intent = Intent(this, BoardActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
            finish()
        }

        navProfile.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@MypageActivity, HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(intent)
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent?.flags?.and(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT) != 0) {
        }
    }

    override fun onResume() {
        super.onResume()
        val navMap = findViewById<LinearLayout>(R.id.nav_map)
        val navBoard = findViewById<LinearLayout>(R.id.nav_board)
        val navProfile = findViewById<LinearLayout>(R.id.nav_profile)
        navProfile.setBackgroundResource(R.drawable.nav_active_background)
        navMap.setBackgroundResource(R.drawable.nav_inactive_background)
        navBoard.setBackgroundResource(R.drawable.nav_inactive_background)
        loadMyData(2)
    }

    private fun loadUserProfile() {
        mypageNickname.text = "나의 닉네임 (ID: ${currentUserId})"
        Glide.with(this)
            .load("https://picsum.photos/seed/${currentUserId}/200")
            .placeholder(R.drawable.ic_default_profile)
            .error(R.drawable.ic_default_profile)
            .circleCrop()
            .into(mypageProfileImage)
    }

    private fun setupRecyclerViews() {
        rvMyPosts.layoutManager = LinearLayoutManager(this)
        rvMyPosts.isNestedScrollingEnabled = false
        myPostsAdapter = PostAdapter(emptyList(), object : PostAdapter.OnPostItemClickListener {
            override fun onPostItemClicked(post: PostItem) {
                val intent = Intent(this@MypageActivity, PostViewActivity::class.java)
                intent.putExtra(PostViewActivity.EXTRA_POST_ID, post.id)
                startActivity(intent)
            }
        })
        rvMyPosts.adapter = myPostsAdapter

        rvMyPins.layoutManager = LinearLayoutManager(this)
        rvMyPins.isNestedScrollingEnabled = false
        myPinsAdapter = PinAdapter(emptyList(), object : PinAdapter.OnPinItemClickListener {
            override fun onPinItemClicked(pin: PinItem) {
                val intent = Intent(this@MypageActivity, PinViewActivity::class.java)
                intent.putExtra(PinViewActivity.EXTRA_PIN_ID, pin.id)
                startActivity(intent)
            }
        })
        rvMyPins.adapter = myPinsAdapter
    }

    private fun loadMyData(itemLimit: Int? = null) {
        val myAllPosts = allSampleData
            .filterIsInstance<PostItem>()
            .filter { it.userId == currentUserId }
            .sortedByDescending { it.id }

        myPostsAdapter.updateData(if(itemLimit != null) myAllPosts.take(itemLimit) else myAllPosts)

        val myAllPins = allSampleData
            .filterIsInstance<PinItem>()
            .filter { it.userId == currentUserId }
            .sortedByDescending { it.id }

        myPinsAdapter.updateData(if(itemLimit != null) myAllPins.take(itemLimit) else myAllPins)
    }
}