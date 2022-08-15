package com.hms.readinghabittracker.ui.mybooks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hms.readinghabittracker.databinding.FragmentMyBooksBinding

class MyBooksFragment : Fragment() {
    private lateinit var binding: FragmentMyBooksBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyBooksBinding.inflate(inflater, container, false)
        return binding.root
    }
}