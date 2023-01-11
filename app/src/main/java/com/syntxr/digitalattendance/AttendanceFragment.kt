package com.syntxr.digitalattendance

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.syntxr.digitalattendance.databinding.FragmentAttendanceBinding

class AttendanceFragment : Fragment(R.layout.fragment_attendance) {

    private val binding : FragmentAttendanceBinding by  viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}