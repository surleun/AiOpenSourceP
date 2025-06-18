package com.sweetspot

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FreeBoardFragment : Fragment(), PostAdapter.OnPostItemClickListener {

    private lateinit var postsRecyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private var samplePosts = mutableListOf<PostItem>()
    private var postItemHost: PostItemHost? = null

    interface PostItemHost {
        fun openPostView(postId: Long)
        fun openPinView(pinId: Long)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is PostItemHost) {
            postItemHost = context
        } else {
            throw RuntimeException("$context must implement PostItemHost")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_free_board, container, false)
        postsRecyclerView = view.findViewById(R.id.postsRecyclerView)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadSampleData()

        setFragmentResultListener("newPostRequest") { _, bundle ->
            val title = bundle.getString("newPostTitle") ?: ""
            val content = bundle.getString("newPostContent") ?: ""
            val imageUri = bundle.getString("newPostImageUri")

            val newPost = PostItem(
                id = System.currentTimeMillis(),
                userId = 3,
                title = title,
                content = content,
                createdAt = "방금 전",
                views = 0,
                authorNickname = "나",
                authorProfileImageUrl = null,
                imageUrl = imageUri,
                commentCount = 0,
                likeCount = 0
            )
            addNewPostToList(newPost)
        }
    }

    private fun setupRecyclerView() {
        postAdapter = PostAdapter(samplePosts, this)
        postsRecyclerView.adapter = postAdapter
        postsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun loadSampleData() {
        if (samplePosts.isEmpty()) {
            val initialPosts = listOf(
                PostItem(1L, 101L, "자유게시판 첫 번째 글", "이것은 자유 게시판 첫 번째 글의 전체 내용입니다. DB에서 불러올 실제 내용이 여기에 표시됩니다.", "2024-06-05 10:00", 150, "자유로운영혼", "https://picsum.photos/seed/user101/50", "https://picsum.photos/seed/fb1/200/200", 15, 33),
                PostItem(2L, 102L, "자유게시판 두 번째 글", "이미지가 없는 자유 게시판 글의 본문입니다. 자유롭게 이야기를 나눌 수 있습니다.", "2024-06-04 15:30", 75, "익명글쓴이", null, null, 7, 12)
            )
            samplePosts.addAll(initialPosts)
        }
        postAdapter.updateData(samplePosts)
    }

    private fun addNewPostToList(newPost: PostItem) {
        samplePosts.add(0, newPost)
        postAdapter.updateData(samplePosts)
        postsRecyclerView.scrollToPosition(0)
    }

    override fun onPostItemClicked(post: PostItem) {
        postItemHost?.openPostView(post.id)
    }

    override fun onDetach() {
        super.onDetach()
        postItemHost = null
    }
}