package com.sweetspot

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class BoardPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    private val fragmentList: List<Fragment> = listOf(
        FreeBoardFragment(),
        PinBoardFragment(),
        PopularBoardFragment(),
        PopularPinFragment()
    )

    override fun getItemCount(): Int = fragmentList.size

    override fun createFragment(position: Int): Fragment = fragmentList[position]

    fun getFragmentClass(position: Int): Class<out Fragment> {
        return fragmentList[position].javaClass
    }
}