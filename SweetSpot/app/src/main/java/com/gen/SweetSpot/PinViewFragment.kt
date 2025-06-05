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
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PinViewFragment : Fragment() {

    private var pinIdArg: Long = 0L

    private lateinit var pinImage: ImageView
    private lateinit var pinLabel: TextView
    private lateinit var pinDescription: TextView
    private lateinit var pinAuthorImage: ShapeableImageView
    private lateinit var pinAuthorNickname: TextView
    private lateinit var pinTimestamp: TextView
    private lateinit var pinSaveButton: MaterialButton
    private lateinit var pinShareButton: MaterialButton

    private lateinit var commentsRecyclerView: RecyclerView
    private lateinit var etPinCommentInput: TextInputEditText
    private lateinit var btnSubmitPinComment: MaterialButton
    private lateinit var commentAdapter: CommentAdapter
    private val commentsList = mutableListOf<CommentItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            pinIdArg = it.getLong(ARG_PIN_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pin_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pinImage = view.findViewById(R.id.pinViewMainImage)
        pinLabel = view.findViewById(R.id.pinViewLabel)
        pinDescription = view.findViewById(R.id.pinViewDescription)
        pinAuthorImage = view.findViewById(R.id.pinViewAuthorImage)
        pinAuthorNickname = view.findViewById(R.id.pinViewAuthorNickname)
        pinTimestamp = view.findViewById(R.id.pinViewTimestamp)
        pinSaveButton = view.findViewById(R.id.pinViewSaveButton)
        pinShareButton = view.findViewById(R.id.pinViewShareButton)

        commentsRecyclerView = view.findViewById(R.id.pinCommentsRecyclerView)
        etPinCommentInput = view.findViewById(R.id.etPinCommentInput)
        btnSubmitPinComment = view.findViewById(R.id.btnSubmitPinComment)

        loadPinDetails()
        setupCommentSection()
        loadDummyCommentsForPin()

        pinSaveButton.setOnClickListener {
            Toast.makeText(requireContext(), "핀 저장 기능 (구현 예정)", Toast.LENGTH_SHORT).show()
        }
        pinShareButton.setOnClickListener {
            Toast.makeText(requireContext(), "핀 공유 기능 (구현 예정)", Toast.LENGTH_SHORT).show()
        }
        btnSubmitPinComment.setOnClickListener {
            submitNewComment()
        }
    }

    private fun loadPinDetails() {
        if (pinIdArg == 0L) {
            pinLabel.text = "오류: 핀 ID가 없습니다."
            return
        }
        val pinData = fetchDummyPinDataById(pinIdArg)

        if (pinData != null) {
            pinLabel.text = pinData.title
            pinDescription.text = pinData.content ?: "내용 없음"
            pinAuthorNickname.text = pinData.authorNickname
            pinTimestamp.text = pinData.createdAt ?: "날짜 정보 없음"

            Glide.with(this)
                .load(pinData.authorProfileImageUrl ?: "https://picsum.photos/seed/defaultuser/40")
                .placeholder(R.drawable.ic_default_profile)
                .error(R.drawable.ic_default_profile)
                .circleCrop()
                .into(pinAuthorImage)

            if (!pinData.imageUrl.isNullOrEmpty()) {
                pinImage.visibility = View.VISIBLE
                Glide.with(this)
                    .load(pinData.imageUrl)
                    .placeholder(R.drawable.ic_placeholder_image)
                    .error(R.drawable.ic_placeholder_image)
                    .into(pinImage)
            } else {
                pinImage.visibility = View.GONE
            }
        } else {
            pinLabel.text = "핀 ID (${pinIdArg})을 찾을 수 없습니다."
        }
    }

    private fun fetchDummyPinDataById(id: Long): PinItem? {
        val samplePins = listOf(
            PinItem(201L, 301L, "정방폭포 (제주)", "바다로 직접 떨어지는 시원한 폭포! 꼭 가보세요.", 33.2409, 126.5733, "2024-05-10 14:30:00", "제주사랑", "https://picsum.photos/seed/user301/50", "https://picsum.photos/seed/jeju_fall/800/600", 18, 77),
            PinItem(202L, 302L, "수요미식회 나온 곰탕집 (서울)", "국물이 정말 진하고 맛있어요.", 37.5665, 126.9780, "2024-04-22 19:00:00", "미식가K", "https://picsum.photos/seed/user302/50", "https://picsum.photos/seed/gomtang_seoul/800/600", 42, 120)
        )
        return samplePins.find { it.id == id }
    }

    private fun setupCommentSection() {
        commentAdapter = CommentAdapter(commentsList)
        commentsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        commentsRecyclerView.adapter = commentAdapter
        commentsRecyclerView.isNestedScrollingEnabled = false
    }

    private fun loadDummyCommentsForPin() {
        val currentId = pinIdArg
        if (currentId == 0L) return

        val dummyComments = mutableListOf<CommentItem>()
        val sdf = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())

        when(currentId) {
            201L -> {
                dummyComments.add(CommentItem(2001L, currentId, "map", 401L, "여기 정말 멋져요! 다음에 꼭 가봐야겠어요.", sdf.format(Date()), "방문객1", null, 10))
                dummyComments.add(CommentItem(2002L, currentId, "map", 402L, "사진 구도가 좋네요.", sdf.format(Date(System.currentTimeMillis() - 100000)), "풍경사진가", null, 3))
            }
            202L -> {
                dummyComments.add(CommentItem(2003L, currentId, "map", 403L, "저도 이 집 가봤는데 인정입니다!", sdf.format(Date()), "푸드파이터", null, 25))
            }
            else -> {
                dummyComments.add(CommentItem(2004L, currentId, "map", 404L, "이 핀에 대한 댓글입니다.", sdf.format(Date()), "댓글러", null, 1))
            }
        }
        commentAdapter.updateComments(dummyComments)
    }

    private fun submitNewComment() {
        val commentText = etPinCommentInput.text.toString().trim()
        if (commentText.isEmpty()) {
            Toast.makeText(requireContext(), "댓글 내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val sdf = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
        val newComment = CommentItem(
            id = System.currentTimeMillis(),
            postId = pinIdArg,
            postType = "map",
            userId = 998L,
            content = commentText,
            createdAt = sdf.format(Date()),
            authorNickname = "현재사용자(핀)",
            authorProfileImageUrl = null,
            likeCount = 0
        )
        commentAdapter.addComment(newComment)
        etPinCommentInput.text?.clear()
        commentsRecyclerView.scrollToPosition(0)
        Toast.makeText(requireContext(), "댓글이 등록되었습니다.", Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val ARG_PIN_ID = "pin_id_long"

        @JvmStatic
        fun newInstance(pinId: Long): PinViewFragment {
            return PinViewFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_PIN_ID, pinId)
                }
            }
        }
    }
}