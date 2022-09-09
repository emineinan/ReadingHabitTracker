package com.hms.readinghabittracker.ui.addingbook

import android.app.AlertDialog
import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.hms.readinghabittracker.base.BaseFragment
import com.hms.readinghabittracker.databinding.AddImageDialogBinding
import com.hms.readinghabittracker.databinding.FragmentAddBookBinding

class AddBookFragment :
    BaseFragment<FragmentAddBookBinding, AddBookViewModel>(FragmentAddBookBinding::inflate) {

    override val viewModel: AddBookViewModel by viewModels()
    private val args: AddBookFragmentArgs by navArgs()

    override fun setupUi() {
        binding.toolbarAddBook.title = args.collectionName

        binding.imageViewAddIcon.setOnClickListener {
            showAddBookImageDialog()
        }
    }

    private fun showAddBookImageDialog() {
        val dialogBinding: AddImageDialogBinding =
            AddImageDialogBinding.inflate(LayoutInflater.from(context))
        val builder = AlertDialog.Builder(context).setView(dialogBinding.root).show()
        val cameraSelected = dialogBinding.textViewCamera
        val gallerySelected = dialogBinding.textViewGallery

        cameraSelected.setOnClickListener {
            Toast.makeText(requireContext(), "Camera selected.", Toast.LENGTH_SHORT).show()
            builder.dismiss()
        }

        gallerySelected.setOnClickListener {
            Toast.makeText(requireContext(), "Gallery selected.", Toast.LENGTH_SHORT).show()
            builder.dismiss()
        }
    }
}