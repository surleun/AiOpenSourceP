package com.gen.SweetSpot

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PopularBoardFragment : Fragment(), PostAdapter.OnPostItemClickListener {

    private lateinit var popularBoardRecyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private val popularPosts = mutableListOf<PostItem>()
    private var itemHost: FreeBoardFragment.PostItemHost? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FreeBoardFragment.PostItemHost) {
            itemHost = context
        } else {
            throw RuntimeException("$context must implement FreeBoardFragment.PostItemHost")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_popular_board, container, false)
        popularBoardRecyclerView = view.findViewById(R.id.popularBoardRecyclerView)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadPopularBoardData()
    }

    private fun setupRecyclerView() {
        postAdapter = PostAdapter(popularPosts, this)
        popularBoardRecyclerView.adapter = postAdapter
        popularBoardRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun loadPopularBoardData() {
        val newPopularPosts = listOf(
            PostItem(
                id = 301L,
                userId = 501L,
                title = "인기 게시글 1",
                content = "이것은 매우 인기 있는 게시글의 내용입니다. 많은 사람들이 읽고 좋아합니다.",
                createdAt = "2024-06-04 11:00",
                views = 1205,
                authorNickname = "인기스타",
                authorProfileImageUrl = "https://picsum.photos/seed/user501/50",
                imageUrl = "https://picsum.photos/seed/popular_b1/200",
                commentCount = 88,
                likeCount = 250
            ),
            PostItem(
                id = 302L,
                userId = 502L,
                title = "인기 게시글 2",
                content = "두 번째 인기 게시글의 상세 내용입니다. 다양한 의견이 오고 가고 있습니다.",
                createdAt = "2024-06-03 18:30",
                views = 950,
                authorNickname = "트렌드세터",
                authorProfileImageUrl = null,
                imageUrl = "https://picsum.photos/seed/popular_b2/200",
                commentCount = 65,
                likeCount = 199
            )
        )
        popularPosts.clear()
        popularPosts.addAll(newPopularPosts)
        if (::postAdapter.isInitialized) {
            postAdapter.updateData(popularPosts)
        }
    }

    override fun onPostItemClicked(post: PostItem) {
        itemHost?.openPostView(post.id)
    }

    override fun onDetach() {
        super.onDetach()
        itemHost = null
    }
}