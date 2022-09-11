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
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.hms.readinghabittracker.R
import com.hms.readinghabittracker.base.BaseFragment
import com.hms.readinghabittracker.databinding.AddImageDialogBinding
import com.hms.readinghabittracker.databinding.EditImageDialogBinding
import com.hms.readinghabittracker.databinding.FragmentAddBookBinding
import com.hms.readinghabittracker.utils.Constant
import com.hms.readinghabittracker.utils.ImageUtils.convertBitmapToByteArray
import com.hms.readinghabittracker.utils.PermissionUtils.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE
import com.hms.readinghabittracker.utils.PermissionUtils.PERMISSION_REQUEST_CODE_CAMERA
import com.hms.readinghabittracker.utils.PermissionUtils.REQUEST_PICK_IMAGE
import com.hms.readinghabittracker.utils.extensions.loadImage
import com.huawei.agconnect.auth.AGConnectAuth
import dagger.hilt.android.AndroidEntryPoint
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

        startListenBackStackEntry()

        binding.imageViewAddIcon.setOnClickListener {
            showAddBookImageDialog()
        }

        binding.buttonSaveBook.setOnClickListener {
            saveBook()
        }

        binding.imageViewEditImage.setOnClickListener {
            showEditImageDialog()
        }
    }

    private fun showEditImageDialog() {
        val dialogBinding: EditImageDialogBinding =
            EditImageDialogBinding.inflate(LayoutInflater.from(context))
        val builder = AlertDialog.Builder(context).setView(dialogBinding.root).show()
        val cropSelected = dialogBinding.textViewCrop
        val filterSelected = dialogBinding.textViewFilter

        cropSelected.setOnClickListener {
            if (viewModel.selectedBitmap.value != null) {
                val action = AddBookFragmentDirections.actionAddBookFragmentToCropFragment(
                    viewModel.selectedBitmap.value!!
                )
                Navigation.findNavController(binding.root).navigate(action)
            } else {
                Toast.makeText(activity, "Upload an Image to continue", Toast.LENGTH_SHORT).show()
            }
            builder.dismiss()
        }

        filterSelected.setOnClickListener {
            // todo navigate Filter Fragment
            builder.dismiss()
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

    private fun startListenBackStackEntry() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Bitmap>(
            Constant.KEY_AVAILABILITY_CALENDAR_SHOULD_REFRESH.toString()
        )
            ?.observe(
                viewLifecycleOwner
            ) { resultImage ->
                val image: Bitmap = resultImage
                binding.imageViewBookPhoto.loadImage(image)
                viewModel.setSelectedBitmap(image)
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
                PERMISSION_REQUEST_CODE_CAMERA
            )
        } else {
            takePhoto()
        }
    }

    private fun takePhoto() {
        val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (activity?.packageManager?.let { it1 -> callCameraIntent.resolveActivity(it1) } != null) {
            startActivityForResult(callCameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
            binding.imageViewAddIcon.visibility = View.INVISIBLE
            binding.imageViewEditImage.visibility = View.VISIBLE
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
                        binding.imageViewEditImage.visibility = View.VISIBLE

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
}