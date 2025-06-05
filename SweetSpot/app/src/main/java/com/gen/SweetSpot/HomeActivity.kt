package com.gen.SweetSpot

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeActivity : AppCompatActivity(), PostAdapter.OnPostItemClickListener, PinAdapter.OnPinItemClickListener {

    private var isBackPressedOnce = false
    private val backPressedHandler = Handler(Looper.getMainLooper())
    private val backPressedToast: Toast by lazy {
        Toast.makeText(this, "한 번 더 뒤로가기 버튼을 누르면 종료됩니다.", Toast.LENGTH_SHORT)
    }

    private lateinit var rvPopularPosts: RecyclerView
    private lateinit var rvPopularPins: RecyclerView
    private lateinit var popularPostsAdapter: PostAdapter
    private lateinit var popularPinsAdapter: PinAdapter

    private val popularPostsData = mutableListOf<PostItem>()
    private val popularPinsData = mutableListOf<PinItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        rvPopularPosts = findViewById(R.id.rvPopularPosts)
        rvPopularPins = findViewById(R.id.rvPopularPins)

        setupRecyclerViews()
        loadPopularData()

        val navMap = findViewById<LinearLayout>(R.id.nav_map)
        val navBoard = findViewById<LinearLayout>(R.id.nav_board)
        val navProfile = findViewById<LinearLayout>(R.id.nav_profile)

        navMap.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }

        navBoard.setOnClickListener {
            val intent = Intent(this, BoardActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }

        navProfile.setOnClickListener {
            val intent = Intent(this, MypageActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isBackPressedOnce) {
                    backPressedToast.cancel()
                    finishAffinity()
                    return
                }

                isBackPressedOnce = true
                backPressedToast.show()

                backPressedHandler.postDelayed({
                    isBackPressedOnce = false
                }, 2000)
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    private fun setupRecyclerViews() {
        popularPostsAdapter = PostAdapter(popularPostsData, this)
        rvPopularPosts.layoutManager = LinearLayoutManager(this)
        rvPopularPosts.adapter = popularPostsAdapter
        rvPopularPosts.isNestedScrollingEnabled = false

        popularPinsAdapter = PinAdapter(popularPinsData, this)
        rvPopularPins.layoutManager = LinearLayoutManager(this)
        rvPopularPins.adapter = popularPinsAdapter
        rvPopularPins.isNestedScrollingEnabled = false
    }

    private fun loadPopularData() {
        popularPostsData.clear()
        popularPostsData.addAll(listOf(
            PostItem(301L, 501L, "인기 게시물 1", "내용 요약: Alpha 인기글입니다. 많은 조회수를 기록했습니다.", "2024-06-05", 1500, "인기글A", null, "https://picsum.photos/seed/pop_post_alpha/200", 120, 350),
            PostItem(302L, 502L, "인기 게시물 2", "내용 요약: Beta 인기글, 다양한 반응을 얻고 있습니다.", "2024-06-04", 1250, "인기글B", "https://picsum.photos/seed/user502/50", "https://picsum.photos/seed/pop_post_beta/200", 90, 280)
        ))
        popularPostsAdapter.updateData(popularPostsData)

        popularPinsData.clear()
        popularPinsData.addAll(listOf(
            PinItem(401L, 601L, "핫플레이스 추천 핀 #1", "설명: 꼭 방문해야 할 핫플레이스 핀입니다.", 37.5600, 126.9700, "2024-06-02", "여행전문가", "https://picsum.photos/seed/user601/50", "https://picsum.photos/seed/pop_pin_one/400", 95, 450),
            PinItem(402L, 602L, "핫플레이스 추천 핀 #2", "설명: 미식가들이 추천하는 인기 맛집 핀.", 35.1700, 129.0700, "2024-06-01", "맛집헌터", null, "https://picsum.photos/seed/pop_pin_two/400", 70, 380)
        ))
        popularPinsAdapter.updateData(popularPinsData)
    }

    override fun onPostItemClicked(post: PostItem) {
        val intent = Intent(this, PostViewActivity::class.java)
        intent.putExtra(PostViewActivity.EXTRA_POST_ID, post.id)
        startActivity(intent)
    }

    override fun onPinItemClicked(pin: PinItem) {
        val intent = Intent(this, PinViewActivity::class.java)
        intent.putExtra(PinViewActivity.EXTRA_PIN_ID, pin.id)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        backPressedHandler.removeCallbacksAndMessages(null)
    }
}