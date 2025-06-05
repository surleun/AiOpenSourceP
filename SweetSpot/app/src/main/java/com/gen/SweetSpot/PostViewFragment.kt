package com.gen.SweetSpot

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PostViewFragment : Fragment() {

    private var postIdArg: Long = 0L

    private lateinit var postImage: ImageView
    private lateinit var postTitle: TextView
    private lateinit var postAuthor: TextView
    private lateinit var postTimestamp: TextView
    private lateinit var postViewCount: TextView
    private lateinit var postCommentCountText: TextView
    private lateinit var postRecommendationCountText: TextView
    private lateinit var postContent: TextView
    private lateinit var postLikeButton: MaterialButton
    private lateinit var postReportButton: MaterialButton

    private lateinit var commentsRecyclerView: RecyclerView
    private lateinit var etPostCommentInput: TextInputEditText
    private lateinit var btnSubmitPostComment: MaterialButton
    private lateinit var commentAdapter: CommentAdapter
    private val commentsList = mutableListOf<CommentItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            postIdArg = it.getLong(ARG_POST_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_post_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postImage = view.findViewById(R.id.postViewImage)
        postTitle = view.findViewById(R.id.postViewTitle)
        postAuthor = view.findViewById(R.id.postViewAuthor)
        postTimestamp = view.findViewById(R.id.postViewTimestamp)
        postViewCount = view.findViewById(R.id.postViewCount)
        postCommentCountText = view.findViewById(R.id.postViewCommentCountText)
        postRecommendationCountText = view.findViewById(R.id.postViewRecommendationCountText)
        postContent = view.findViewById(R.id.postViewContent)
        postLikeButton = view.findViewById(R.id.postLikeButton)
        postReportButton = view.findViewById(R.id.postReportButton)

        commentsRecyclerView = view.findViewById(R.id.postCommentsRecyclerView)
        etPostCommentInput = view.findViewById(R.id.etPostCommentInput)
        btnSubmitPostComment = view.findViewById(R.id.btnSubmitPostComment)

        loadPostDetails()
        setupCommentSection()
        loadDummyComments()

        postLikeButton.setOnClickListener {
            Toast.makeText(requireContext(), "게시물 좋아요 기능 (구현 예정)", Toast.LENGTH_SHORT).show()
        }
        postReportButton.setOnClickListener {
            Toast.makeText(requireContext(), "게시물 신고 기능 (구현 예정)", Toast.LENGTH_SHORT).show()
        }
        btnSubmitPostComment.setOnClickListener {
            submitNewComment()
        }
    }

    private fun loadPostDetails() {
        if (postIdArg == 0L) {
            postTitle.text = "오류: ID가 전달되지 않았습니다."
            return
        }

        val post = fetchDummyPostDataById(postIdArg)

        if (post != null) {
            postTitle.text = post.title
            postAuthor.text = "작성자: ${post.authorNickname}"
            postTimestamp.text = post.createdAt ?: "날짜 정보 없음"
            postViewCount.text = "조회 ${post.views}"
            postCommentCountText.text = "댓글 ${post.commentCount}"
            postRecommendationCountText.text = "추천 ${post.likeCount}"
            postContent.text = post.content ?: "내용 없음"

            if (!post.imageUrl.isNullOrEmpty()) {
                postImage.visibility = View.VISIBLE
                Glide.with(this)
                    .load(post.imageUrl)
                    .placeholder(R.drawable.ic_placeholder_image)
                    .error(R.drawable.ic_placeholder_image)
                    .into(postImage)
            } else {
                postImage.visibility = View.GONE
            }
        } else {
            postTitle.text = "게시물 ID (${postIdArg})을 찾을 수 없습니다."
            postContent.text = "해당 ID에 대한 게시물 정보가 없습니다."
            postImage.visibility = View.GONE
        }
    }

    private fun fetchDummyPostDataById(id: Long): PostItem? {
        val samplePosts = listOf(
            PostItem(1L, 101L, "첫 번째 일반 게시물", "첫 번째 글의 전체 내용입니다...", "2024-06-01 10:00", 150, "작가1", "https://picsum.photos/seed/user101/50", "https://picsum.photos/seed/pv1/600/300", 10, 5),
            PostItem(2L, 102L, "두 번째 일반 게시물의 내용", "두 번째 글의 전체 내용입니다...", "2024-06-02 11:00", 75, "테스터2", null, "https://picsum.photos/seed/pv2/600/300", 5, 22)
        )
        return samplePosts.find { it.id == id }
    }

    private fun setupCommentSection() {
        commentAdapter = CommentAdapter(commentsList)
        commentsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        commentsRecyclerView.adapter = commentAdapter
        commentsRecyclerView.isNestedScrollingEnabled = false
    }

    private fun loadDummyComments() {
        val currentId = postIdArg
        if (currentId == 0L) return

        val dummyComments = mutableListOf<CommentItem>()
        val sdf = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())

        when (currentId) {
            1L -> {
                dummyComments.add(CommentItem(1001L, currentId, "free", 201L, "첫 번째 댓글", sdf.format(Date()), "댓글러A", null, 3))
                dummyComments.add(CommentItem(1002L, currentId, "free", 202L, "두 번째 댓글", sdf.format(Date(System.currentTimeMillis() - 300000)), "방문객B", null, 1))
            }
            2L -> {
                dummyComments.add(CommentItem(1003L, currentId, "free", 203L, "두 번째 게시물에도 댓글 남겨요.", sdf.format(Date()), "사용자C", null, 0))
            }
            else -> {
                dummyComments.add(CommentItem(1004L, currentId, "free", 204L, "이 게시물에 대한 댓글입니다.", sdf.format(Date()), "익명", null, 0))
            }
        }
        commentAdapter.updateComments(dummyComments)
    }

    private fun submitNewComment() {
        val commentText = etPostCommentInput.text.toString().trim()
        if (commentText.isEmpty()) {
            Toast.makeText(requireContext(), "댓글 내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val sdf = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
        val newComment = CommentItem(
            id = System.currentTimeMillis(),
            postId = postIdArg,
            postType = "free",
            userId = 999L,
            content = commentText,
            createdAt = sdf.format(Date()),
            authorNickname = "현재사용자",
            authorProfileImageUrl = null,
            likeCount = 0
        )
        commentAdapter.addComment(newComment)
        etPostCommentInput.text?.clear()
        commentsRecyclerView.scrollToPosition(0)
        Toast.makeText(requireContext(), "댓글이 등록되었습니다.", Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val ARG_POST_ID = "post_id_long"

        @JvmStatic
        fun newInstance(postId: Long): PostViewFragment {
            return PostViewFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_POST_ID, postId)
                }
            }
        }
    }
}