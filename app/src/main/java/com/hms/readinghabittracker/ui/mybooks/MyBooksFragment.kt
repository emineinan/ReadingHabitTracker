package com.hms.readinghabittracker.ui.mybooks

import androidx.fragment.app.viewModels
import com.hms.readinghabittracker.base.BaseFragment
import com.hms.readinghabittracker.databinding.FragmentMyBooksBinding

class MyBooksFragment :
    BaseFragment<FragmentMyBooksBinding, MyBooksViewModel>(FragmentMyBooksBinding::inflate) {

    override val viewModel: MyBooksViewModel by viewModels()
}