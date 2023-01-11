package com.syntxr.digitalattendance

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.syntxr.digitalattendance.databinding.FragmentLeavesBinding

class HistoryLeavesFragment : Fragment(R.layout.fragment_history_leaves) {

    private val binding : FragmentLeavesBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}