package com.hms.readinghabittracker.ui.addingbook

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.hms.readinghabittracker.base.BaseFragment
import com.hms.readinghabittracker.databinding.FragmentAddBookBinding

class AddBookFragment :
    BaseFragment<FragmentAddBookBinding, AddBookViewModel>(FragmentAddBookBinding::inflate) {

    override val viewModel: AddBookViewModel by viewModels()
    private val args: AddBookFragmentArgs by navArgs()

    override fun setupUi() {
        binding.toolbarAddBook.title = args.collectionName
    }
}