package com.hms.readinghabittracker.ui.goals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hms.readinghabittracker.R
import com.hms.readinghabittracker.databinding.FragmentGoalsBinding

class GoalsFragment : Fragment() {
    private lateinit var binding: FragmentGoalsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGoalsBinding.inflate(inflater, container, false)
        return binding.root
    }
}