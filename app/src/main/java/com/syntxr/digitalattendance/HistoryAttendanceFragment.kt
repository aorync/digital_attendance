package com.syntxr.digitalattendance

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.faltenreich.skeletonlayout.applySkeleton
import com.syntxr.digitalattendance.`object`.Objects
import com.syntxr.digitalattendance.databinding.FragmentAttendanceHistoryBinding
import com.syntxr.digitalattendance.ui.HistoryAttendanceAdapter
import kotlinx.coroutines.launch

class HistoryAttendanceFragment : Fragment(R.layout.fragment_attendance_history) {

    private val binding: FragmentAttendanceHistoryBinding by viewBinding()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchData()

        binding.swipeAttendanceHistory.setOnRefreshListener {
            fetchData()
        }


    }

    private fun fetchData(){
        val api = Objects.create()
        lifecycleScope.launch {
            try {

                val skeleton = binding.rvListHistory.applySkeleton(R.layout.item_history)
                skeleton.showSkeleton()
                val attendanceList = api.history("*")
                binding.swipeAttendanceHistory.isRefreshing = false
                binding.historyProgress.visibility = View.GONE

                skeleton.showOriginal()

                val adapter = HistoryAttendanceAdapter(attendanceList)
                val layoutManager = LinearLayoutManager(requireContext())
                binding.rvListHistory.adapter = adapter
                binding.rvListHistory.layoutManager = layoutManager

                adapter.itemClick = {
                    val bundle = bundleOf("userId" to it.usersId, "id" to it.attendanceId)
                    findNavController().navigate(R.id.action_history_to_mapsFragment,bundle)
                }
            }catch (e : Exception){
                Toast.makeText(requireContext(), "error ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


}