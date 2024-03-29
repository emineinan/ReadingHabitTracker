package com.hms.readinghabittracker.ui.bookdetail

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.hms.readinghabittracker.base.BaseFragment
import com.hms.readinghabittracker.databinding.FragmentBookDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookDetailFragment :
    BaseFragment<FragmentBookDetailBinding, BookDetailViewModel>(FragmentBookDetailBinding::inflate) {
    override val viewModel: BookDetailViewModel by viewModels()

    private val args: BookDetailFragmentArgs by navArgs()

    override fun setupUi() {
        super.setupUi()
        binding.textViewBookTitle.text = args.bookTitle
        binding.textViewBookAuthor.text = args.bookAuthor
        binding.textViewBookPages.text = args.bookNoOfPages.toString()
        binding.imageViewBookDetailPhoto.setImageBitmap(args.bookImage)
    }
}