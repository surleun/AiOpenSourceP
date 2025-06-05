package com.gen.SweetSpot

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PopularPinFragment : Fragment(), PinAdapter.OnPinItemClickListener {

    private lateinit var popularPinRecyclerView: RecyclerView
    private lateinit var pinAdapter: PinAdapter
    private val popularPins = mutableListOf<PinItem>()
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
        val view = inflater.inflate(R.layout.fragment_popular_pin, container, false)
        popularPinRecyclerView = view.findViewById(R.id.popularPinRecyclerView)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadPopularPinData()
    }

    private fun setupRecyclerView() {
        pinAdapter = PinAdapter(popularPins, this)
        popularPinRecyclerView.adapter = pinAdapter
        popularPinRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun loadPopularPinData() {
        val newPopularPins = listOf(
            PinItem(
                id = 401L,
                userId = 601L,
                title = "인기 핀 1",
                content = "모두가 저장하는 바로 그 핀! 이곳은 꼭 가봐야 합니다.",
                latitude = 35.1796,
                longitude = 129.0756,
                createdAt = "2024-06-01 09:00",
                authorNickname = "여행의달인",
                authorProfileImageUrl = "https://picsum.photos/seed/user601/50",
                imageUrl = "https://picsum.photos/seed/popular_pin1/400",
                commentCount = 120,
                likeCount = 550
            ),
            PinItem(
                id = 402L,
                userId = 602L,
                title = "인기 핀 2",
                content = "SNS에서 난리난 바로 그곳! 상세 정보와 위치를 확인하세요.",
                latitude = 37.5519,
                longitude = 126.9918,
                createdAt = "2024-05-28 17:45",
                authorNickname = "핫플헌터",
                authorProfileImageUrl = null,
                imageUrl = "https://picsum.photos/seed/popular_pin2/400",
                commentCount = 95,
                likeCount = 480
            )
        )
        popularPins.clear()
        popularPins.addAll(newPopularPins)
        if (::pinAdapter.isInitialized) {
            pinAdapter.updateData(popularPins)
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