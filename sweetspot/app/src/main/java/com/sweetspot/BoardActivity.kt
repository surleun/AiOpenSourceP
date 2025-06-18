package com.sweetspot

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton

class BoardActivity : AppCompatActivity(), FreeBoardFragment.PostItemHost {

    private lateinit var navFree: TextView
    private lateinit var navPin: TextView
    private lateinit var navPopular: TextView
    private lateinit var navPopularPin: TextView
    private lateinit var allTopNavTabs: List<TextView>

    private lateinit var topNavContainer: LinearLayout
    private lateinit var bottomNavContainer: LinearLayout
    private lateinit var fabWritePost: FloatingActionButton
    private lateinit var boardViewPager: ViewPager2
    private lateinit var overlayFragmentContainer: FrameLayout

    private lateinit var boardPagerAdapter: BoardPagerAdapter

    private val backStackListener = FragmentManager.OnBackStackChangedListener {
        val overlayFragment = supportFragmentManager.findFragmentById(R.id.create_post_host_container)
        if (overlayFragment != null && overlayFragment.isVisible) {
            setMainUIVisibility(false)
            if (overlayFragment is CreatePostFragment || overlayFragment is PostViewFragment || overlayFragment is PinViewFragment) {
                updateTopNavSelectionByViewPagerItem(-1)
            }
        } else {
            setMainUIVisibility(true)
            updateTopNavSelectionByViewPagerItem(boardViewPager.currentItem)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        navFree = findViewById(R.id.nav_free)
        navPin = findViewById(R.id.nav_pin)
        navPopular = findViewById(R.id.nav_popular)
        navPopularPin = findViewById(R.id.nav_popular_pin)
        allTopNavTabs = listOf(navFree, navPin, navPopular, navPopularPin)

        topNavContainer = findViewById(R.id.top_nav_container)
        bottomNavContainer = findViewById(R.id.bottom_nav_container)
        fabWritePost = findViewById(R.id.fab_write_post)
        boardViewPager = findViewById(R.id.boardViewPager)
        overlayFragmentContainer = findViewById(R.id.create_post_host_container)

        boardPagerAdapter = BoardPagerAdapter(this)
        boardViewPager.adapter = boardPagerAdapter

        boardViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateTopNavSelectionByViewPagerItem(position)
            }
        })

        navFree.setOnClickListener { boardViewPager.currentItem = 0 }
        navPin.setOnClickListener { boardViewPager.currentItem = 1 }
        navPopular.setOnClickListener { boardViewPager.currentItem = 2 }
        navPopularPin.setOnClickListener { boardViewPager.currentItem = 3 }

        if (savedInstanceState == null) {
            updateTopNavSelectionByViewPagerItem(0)
            setMainUIVisibility(true)
        }

        fabWritePost.setOnClickListener {
            setMainUIVisibility(false)
            updateTopNavSelectionByViewPagerItem(-1)
            replaceFragmentInOverlay(CreatePostFragment(), true)
        }

        supportFragmentManager.addOnBackStackChangedListener(backStackListener)

        val navMap = findViewById<LinearLayout>(R.id.nav_map)
        val navBoard = findViewById<LinearLayout>(R.id.nav_board)
        val navProfile = findViewById<LinearLayout>(R.id.nav_profile)

        navMap.setBackgroundResource(R.drawable.nav_inactive_background)
        navProfile.setBackgroundResource(R.drawable.nav_inactive_background)

        navMap.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }

        navBoard.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
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
                val overlayFragment = supportFragmentManager.findFragmentById(R.id.create_post_host_container)
                if (overlayFragment != null && overlayFragment.isVisible && supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                } else {
                    val intent = Intent(this@BoardActivity, HomeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(intent)
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.flags.and(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT) != 0) {
        }
    }

    override fun onResume() {
        super.onResume()
        val navMap = findViewById<LinearLayout>(R.id.nav_map)
        val navBoard = findViewById<LinearLayout>(R.id.nav_board)
        val navProfile = findViewById<LinearLayout>(R.id.nav_profile)
        navMap.setBackgroundResource(R.drawable.nav_inactive_background)
        navProfile.setBackgroundResource(R.drawable.nav_inactive_background)
    }

    private fun replaceFragmentInOverlay(fragment: Fragment, addToBackStack: Boolean) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.create_post_host_container, fragment)
        if (addToBackStack) {
            transaction.addToBackStack(fragment::class.java.simpleName)
        }
        transaction.commit()
    }

    private fun updateTopNavSelectionByViewPagerItem(position: Int) {
        allTopNavTabs.forEachIndexed { index, textView ->
            if (index == position) {
                textView.setTextColor(ContextCompat.getColor(this, R.color.purple_500))
                textView.setTypeface(null, Typeface.BOLD)
            } else {
                textView.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray))
                textView.setTypeface(null, Typeface.NORMAL)
            }
        }
    }

    private fun setMainUIVisibility(isVisible: Boolean) {
        val visibility = if (isVisible) View.VISIBLE else View.GONE
        topNavContainer.visibility = visibility
        bottomNavContainer.visibility = visibility
        boardViewPager.visibility = visibility
        fabWritePost.visibility = visibility
        overlayFragmentContainer.visibility = if (isVisible) View.GONE else View.VISIBLE
    }

    override fun openPostView(postId: Long) {
        setMainUIVisibility(false)
        updateTopNavSelectionByViewPagerItem(-1)
        val postViewFragment = PostViewFragment.newInstance(postId)
        replaceFragmentInOverlay(postViewFragment, true)
    }

    override fun openPinView(pinId: Long) {
        setMainUIVisibility(false)
        updateTopNavSelectionByViewPagerItem(-1)
        val pinViewFragment = PinViewFragment.newInstance(pinId)
        replaceFragmentInOverlay(pinViewFragment, true)
    }

    override fun onDestroy() {
        super.onDestroy()
        supportFragmentManager.removeOnBackStackChangedListener(backStackListener)
    }
}