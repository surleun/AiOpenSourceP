package com.gen.SweetSpot

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    }

    private fun setupRecyclerView() {
        postAdapter = PostAdapter(samplePosts, this)
        postsRecyclerView.adapter = postAdapter
        postsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun loadSampleData() {
        val newPosts = listOf(
            PostItem(
                id = 1L,
                userId = 101L,
                title = "자유게시판 첫 번째 글입니다!",
                content = "이것은 자유 게시판 첫 번째 글의 전체 내용입니다. DB에서 불러올 실제 내용이 여기에 표시됩니다.",
                createdAt = "2024-06-05 10:00",
                views = 150,
                authorNickname = "자유로운영혼",
                authorProfileImageUrl = "https://picsum.photos/seed/user101/50",
                imageUrl = "https://picsum.photos/seed/fb1/200/200",
                commentCount = 15,
                likeCount = 33
            ),
            PostItem(
                id = 2L,
                userId = 102L,
                title = "자유게시판 이미지 없는 글 테스트",
                content = "이미지가 없는 자유 게시판 글의 본문입니다. 자유롭게 이야기를 나눌 수 있습니다.",
                createdAt = "2024-06-04 15:30",
                views = 75,
                authorNickname = "익명글쓴이",
                authorProfileImageUrl = null,
                imageUrl = null,
                commentCount = 7,
                likeCount = 12
            )
        )
        samplePosts.clear()
        samplePosts.addAll(newPosts)
        if (::postAdapter.isInitialized) {
            postAdapter.updateData(samplePosts)
        }
    }

    override fun onPostItemClicked(post: PostItem) {
        postItemHost?.openPostView(post.id)
    }

    override fun onDetach() {
        super.onDetach()
        postItemHost = null
    }
}