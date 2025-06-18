package com.sweetspot

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PinBoardFragment : Fragment(), PinAdapter.OnPinItemClickListener {

    private lateinit var pinBoardRecyclerView: RecyclerView
    private lateinit var pinAdapter: PinAdapter
    private val pinItemsList = mutableListOf<PinItem>()
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
        val view = inflater.inflate(R.layout.fragment_pin_board, container, false)
        pinBoardRecyclerView = view.findViewById(R.id.pinBoardRecyclerView)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadPinData()
    }

    private fun setupRecyclerView() {
        pinAdapter = PinAdapter(pinItemsList, this)
        pinBoardRecyclerView.adapter = pinAdapter
        pinBoardRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun loadPinData() {
        val newPins = listOf(
            PinItem(
                id = 201L,
                userId = 301L,
                title = "정방폭포 (제주)",
                content = "바다로 직접 떨어지는 시원한 폭포! 꼭 가보세요. 근처에 주차장도 잘 되어 있습니다.",
                latitude = 33.2409,
                longitude = 126.5733,
                createdAt = "2024-05-10 14:30:00",
                authorNickname = "제주사랑",
                authorProfileImageUrl = "https://picsum.photos/seed/user301/50",
                imageUrl = "https://picsum.photos/seed/jeju_fall/400",
                commentCount = 18,
                likeCount = 77
            ),
            PinItem(
                id = 202L,
                userId = 302L,
                title = "수요미식회 나온 곰탕집 (서울)",
                content = "국물이 정말 진하고 맛있어요. 위치도 편리합니다. 점심시간에는 웨이팅이 있을 수 있습니다.",
                latitude = 37.5665,
                longitude = 126.9780,
                createdAt = "2024-04-22 19:00:00",
                authorNickname = "미식가K",
                authorProfileImageUrl = "https://picsum.photos/seed/user302/50",
                imageUrl = "https://picsum.photos/seed/gomtang_seoul/400",
                commentCount = 42,
                likeCount = 120
            )
        )
        pinItemsList.clear()
        pinItemsList.addAll(newPins)
        if (::pinAdapter.isInitialized) {
            pinAdapter.updateData(pinItemsList)
        }
    }

    override fun onPinItemClicked(pin: PinItem) {
        itemHost?.openPinView(pin.id)
    }

    override fun onDetach() {
        super.onDetach()
        itemHost = null
    }
}