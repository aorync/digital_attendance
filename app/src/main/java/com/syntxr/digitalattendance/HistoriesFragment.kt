package com.syntxr.digitalattendance


import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.syntxr.digitalattendance.databinding.FragmentHistoryBinding

class HistoriesFragment : Fragment(R.layout.fragment_history) {

    private val binding : FragmentHistoryBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentList = listOf(HistoryAttendanceFragment(),HistoryLeavesFragment())
        val adapter =ViewPagerAdapter(requireParentFragment(),fragmentList)

        val title = listOf("Kehadiran","Izin")
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout,binding.viewPager){tab,pos ->
            tab.text = title[pos]
        }.attach()

    }

}