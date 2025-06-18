package com.sweetspot

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class CreatePostFragment : Fragment() {

    private lateinit var btnTagSelect: MaterialButton
    private lateinit var btnPinSelect: MaterialButton
    private lateinit var boardTagsContainer: LinearLayout
    private lateinit var pinTagsContainer: LinearLayout
    private lateinit var btnSubmit: MaterialButton
    private lateinit var btnBack: MaterialButton
    private lateinit var btnAttachPhotoInBoard: MaterialButton
    private lateinit var btnConfigurePinShare: MaterialButton
    private lateinit var rvSelectedImageThumbnails: RecyclerView
    private lateinit var etTitle: TextInputEditText
    private lateinit var etContent: TextInputEditText


    private val selectedImageUris = mutableListOf<Uri>()
    private lateinit var thumbnailAdapter: ThumbnailAdapter
    private val maxImageSelectionCount = 10

    private lateinit var multipleImagePickerLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        multipleImagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let { data ->
                    if (data.clipData != null) {
                        val count = data.clipData!!.itemCount
                        for (i in 0 until count) {
                            if (selectedImageUris.size >= maxImageSelectionCount) {
                                Toast.makeText(requireContext(), "최대 ${maxImageSelectionCount}개까지 선택 가능합니다.", Toast.LENGTH_SHORT).show()
                                break
                            }
                            val imageUri: Uri = data.clipData!!.getItemAt(i).uri
                            selectedImageUris.add(imageUri)
                        }
                    } else if (data.data != null) {
                        if (selectedImageUris.size < maxImageSelectionCount) {
                            val imageUri: Uri = data.data!!
                            selectedImageUris.add(imageUri)
                        } else {
                            Toast.makeText(requireContext(), "최대 ${maxImageSelectionCount}개까지 선택 가능합니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    if (::thumbnailAdapter.isInitialized) {
                        thumbnailAdapter.notifyDataSetChanged()
                    }
                    updateThumbnailRecyclerViewVisibility()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnTagSelect = view.findViewById(R.id.btnTagSelect)
        btnPinSelect = view.findViewById(R.id.btnPinSelect)
        boardTagsContainer = view.findViewById(R.id.boardTagsContainer)
        pinTagsContainer = view.findViewById(R.id.pinTagsContainer)
        btnSubmit = view.findViewById(R.id.btnSubmit)
        btnBack = view.findViewById(R.id.btnBack)
        btnAttachPhotoInBoard = view.findViewById(R.id.btnAttachPhotoInBoardContainer)
        btnConfigurePinShare = view.findViewById(R.id.btnConfigurePinShare)
        rvSelectedImageThumbnails = view.findViewById(R.id.rvSelectedImageThumbnails)
        etTitle = view.findViewById(R.id.etTitle)
        etContent = view.findViewById(R.id.etContent)

        setupThumbnailRecyclerView()
        selectBoardType(isBoardSelected = true)

        btnTagSelect.setOnClickListener {
            selectBoardType(isBoardSelected = true)
        }

        btnPinSelect.setOnClickListener {
            selectBoardType(isBoardSelected = false)
        }

        btnAttachPhotoInBoard.setOnClickListener {
            openGalleryForMultipleImages()
        }

        btnConfigurePinShare.setOnClickListener {
            Toast.makeText(requireContext(), "핀 공유 설정 기능 실행", Toast.LENGTH_SHORT).show()
        }

        btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        btnSubmit.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val content = etContent.text.toString().trim()
            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(requireContext(), "제목과 내용을 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val resultBundle = bundleOf(
                "newPostTitle" to title,
                "newPostContent" to content,
                "newPostImageUri" to selectedImageUris.firstOrNull()?.toString()
            )
            setFragmentResult("newPostRequest", resultBundle)
            requireActivity().supportFragmentManager.popBackStack()
        }
        updateThumbnailRecyclerViewVisibility()
    }

    private fun setupThumbnailRecyclerView() {
        thumbnailAdapter = ThumbnailAdapter(selectedImageUris) { uriToRemove ->
            selectedImageUris.remove(uriToRemove)
            thumbnailAdapter.notifyDataSetChanged()
            updateThumbnailRecyclerViewVisibility()
        }
        rvSelectedImageThumbnails.adapter = thumbnailAdapter
        rvSelectedImageThumbnails.layoutManager = GridLayoutManager(requireContext(), 5)
    }

    private fun updateThumbnailRecyclerViewVisibility() {
        if (selectedImageUris.isEmpty()) {
            rvSelectedImageThumbnails.visibility = View.GONE
        } else {
            rvSelectedImageThumbnails.visibility = View.VISIBLE
        }
    }

    private fun openGalleryForMultipleImages() {
        if (selectedImageUris.size >= maxImageSelectionCount) {
            Toast.makeText(requireContext(), "최대 ${maxImageSelectionCount}장까지 첨부할 수 있습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        multipleImagePickerLauncher.launch(intent)
    }

    private fun selectBoardType(isBoardSelected: Boolean) {
        val selectedButtonBackgroundColor = ContextCompat.getColor(requireContext(), R.color.button_selected_background)
        val selectedButtonTextColor = ContextCompat.getColor(requireContext(), R.color.button_selected_text)

        val unselectedButtonBackgroundColor = ContextCompat.getColor(requireContext(), R.color.transparent)
        val unselectedButtonStrokeColor = ContextCompat.getColor(requireContext(), R.color.button_unselected_stroke)
        val unselectedButtonTextColor = ContextCompat.getColor(requireContext(), R.color.button_unselected_text)

        val defaultStrokeWidth = resources.getDimensionPixelSize(R.dimen.button_default_stroke_width)


        if (isBoardSelected) {
            btnTagSelect.backgroundTintList = ColorStateList.valueOf(selectedButtonBackgroundColor)
            btnTagSelect.setTextColor(selectedButtonTextColor)
            btnTagSelect.strokeWidth = 0
            btnTagSelect.iconTint = ColorStateList.valueOf(selectedButtonTextColor)


            btnPinSelect.backgroundTintList = ColorStateList.valueOf(unselectedButtonBackgroundColor)
            btnPinSelect.strokeColor = ColorStateList.valueOf(unselectedButtonStrokeColor)
            btnPinSelect.setTextColor(unselectedButtonTextColor)
            btnPinSelect.strokeWidth = defaultStrokeWidth
            btnPinSelect.iconTint = ColorStateList.valueOf(unselectedButtonTextColor)


            boardTagsContainer.visibility = View.VISIBLE
            pinTagsContainer.visibility = View.GONE
        } else {
            btnTagSelect.backgroundTintList = ColorStateList.valueOf(unselectedButtonBackgroundColor)
            btnTagSelect.strokeColor = ColorStateList.valueOf(unselectedButtonStrokeColor)
            btnTagSelect.setTextColor(unselectedButtonTextColor)
            btnTagSelect.strokeWidth = defaultStrokeWidth
            btnTagSelect.iconTint = ColorStateList.valueOf(unselectedButtonTextColor)


            btnPinSelect.backgroundTintList = ColorStateList.valueOf(selectedButtonBackgroundColor)
            btnPinSelect.setTextColor(selectedButtonTextColor)
            btnPinSelect.strokeWidth = 0
            btnPinSelect.iconTint = ColorStateList.valueOf(selectedButtonTextColor)


            boardTagsContainer.visibility = View.GONE
            pinTagsContainer.visibility = View.VISIBLE
        }
    }
}