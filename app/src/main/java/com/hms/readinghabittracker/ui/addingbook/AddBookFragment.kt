package com.hms.readinghabittracker.ui.addingbook

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.hms.readinghabittracker.R
import com.hms.readinghabittracker.base.BaseFragment
import com.hms.readinghabittracker.databinding.AddImageDialogBinding
import com.hms.readinghabittracker.databinding.FragmentAddBookBinding
import com.hms.readinghabittracker.utils.Constants
import com.hms.readinghabittracker.utils.Constants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE
import com.hms.readinghabittracker.utils.Constants.REQUEST_PICK_IMAGE
import com.hms.readinghabittracker.utils.extensions.loadImage
import com.huawei.agconnect.auth.AGConnectAuth
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@AndroidEntryPoint
class AddBookFragment :
    BaseFragment<FragmentAddBookBinding, AddBookViewModel>(FragmentAddBookBinding::inflate) {

    override val viewModel: AddBookViewModel by viewModels()
    private val args: AddBookFragmentArgs by navArgs()

    @Inject
    lateinit var agConnect: AGConnectAuth


    override fun setupUi() {
        binding.toolbarAddBook.title = args.collectionName

        binding.imageViewAddIcon.setOnClickListener {
            showAddBookImageDialog()
        }

        binding.buttonSaveBook.setOnClickListener {
            saveBook()
        }
    }

    private fun saveBook() {
        val bookTitle = binding.editTextBookTitle.text.toString()
        val bookAuthor = binding.editTextBookAuthor.text.toString()
        val noOfPages = binding.editTextNoOfPages.text.toString().toInt()

        if (bookTitle.isEmpty() || bookAuthor.isEmpty() || noOfPages < 0) {
            Toast.makeText(context, R.string.add_book_warning, Toast.LENGTH_SHORT).show()
        } else if (viewModel.selectedBitmap.value != null) {
            viewModel.saveBookToCloudDb(
                bookTitle,
                bookAuthor,
                noOfPages,
                convertBitmapToByteArray(viewModel.selectedBitmap.value!!),
                agConnect.currentUser.uid.toLong(),
                args.collectionId
            )
            Toast.makeText(activity, "Book successfully saved!", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_addBookFragment_to_myBooksFragment)
        }
    }

    override fun setupObserver() {
        viewModel.selectedBitmap.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.imageViewBookPhoto.setImageBitmap(it)
                binding.imageViewAddIcon.visibility = View.INVISIBLE
            }
        }
    }

    private fun showAddBookImageDialog() {
        val dialogBinding: AddImageDialogBinding =
            AddImageDialogBinding.inflate(LayoutInflater.from(context))
        val builder = AlertDialog.Builder(context).setView(dialogBinding.root).show()
        val cameraSelected = dialogBinding.textViewCamera
        val gallerySelected = dialogBinding.textViewGallery

        cameraSelected.setOnClickListener {
            checkCameraPermission()
            builder.dismiss()
        }

        gallerySelected.setOnClickListener {
            pickGallery()
            builder.dismiss()
        }
    }

    private fun pickGallery() {
        val getPhotoIntent = Intent(Intent.ACTION_GET_CONTENT)
        val mimeTypes = arrayOf("image/jpg", "image/png", "image/jpeg")
        getPhotoIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        getPhotoIntent.type = "image/*"
        getPhotoIntent.addCategory(Intent.CATEGORY_OPENABLE)
        this.startActivityForResult(getPhotoIntent, REQUEST_PICK_IMAGE)
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.CAMERA
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                Constants.PERMISSION_REQUEST_CODE_CAMERA
            )
        } else {
            takePhoto()
        }
    }

    private fun takePhoto() {
        val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (activity?.packageManager?.let { it1 -> callCameraIntent.resolveActivity(it1) } != null) {
            startActivityForResult(callCameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (intent != null) {
            if (resultCode == Activity.RESULT_OK) {
                when (requestCode) {
                    REQUEST_PICK_IMAGE -> try {
                        val uri: Uri? = intent.data

                        if (uri != null) {
                            binding.imageViewBookPhoto.loadImage(uri)
                        }

                        binding.imageViewAddIcon.visibility = View.INVISIBLE

                        if (uri != null) {
                            viewModel.setSelectedBitmap(
                                MediaStore.Images.Media.getBitmap(
                                    requireActivity().contentResolver,
                                    uri
                                )
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            if (resultCode == Activity.RESULT_OK && requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                val bitmap = intent.extras?.get("data") as Bitmap
                binding.imageViewBookPhoto.setImageBitmap(bitmap)
                viewModel.setSelectedBitmap(bitmap)
            }
        }
    }

    private fun convertBitmapToByteArray(bitmap: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(
            Bitmap.CompressFormat.JPEG,
            100,
            byteArrayOutputStream
        ) //Quality compression method, here 100 means no compression, store the compressed data in the BIOS
        var options = 100
        while (getImageSize(byteArrayOutputStream) > 500) {  //Cycle to determine if the compressed image is greater than 2Mb, greater than continue compression
            byteArrayOutputStream.reset() //Reset the BIOS to clear it
            //First parameter: picture format, second parameter: picture quality, 100 is the highest, 0 is the worst, third parameter: save the compressed data stream
            bitmap.compress(
                Bitmap.CompressFormat.JPEG,
                options,
                byteArrayOutputStream
            ) //Here, the compression options are used to store the compressed data in the BIOS
            options -= 10 //10 less each time
        }
        return byteArrayOutputStream.toByteArray()
    }


    private fun getImageSize(byteArrayOutputStream: ByteArrayOutputStream) =
        byteArrayOutputStream.toByteArray().size / 1024
}