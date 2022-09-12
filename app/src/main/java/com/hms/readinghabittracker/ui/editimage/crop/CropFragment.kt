package com.hms.readinghabittracker.ui.editimage.crop

import android.graphics.Bitmap
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.hms.readinghabittracker.base.BaseFragment
import com.hms.readinghabittracker.databinding.FragmentCropBinding
import com.hms.readinghabittracker.utils.Constant.KEY_AVAILABILITY_CALENDAR_SHOULD_REFRESH
import com.huawei.hms.image.vision.crop.CropLayoutView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CropFragment :
    BaseFragment<FragmentCropBinding, CropViewModel>(FragmentCropBinding::inflate) {
    override val viewModel: CropViewModel by viewModels()

    private var inputBitMap: Bitmap? = null
    private val args: CropFragmentArgs by navArgs()

    override fun setupUi() {
        val cropLayoutView: CropLayoutView = binding.cropImageView

        //Input image coming from camera or gallery
        binding.imageViewBookCrop.visibility = View.INVISIBLE
        inputBitMap = args.image
        cropLayoutView.setImageBitmap(inputBitMap)
        cropLayoutView.visibility = View.VISIBLE

        binding.buttonFlipHorizontally.setOnClickListener {
            binding.imageViewBookCrop.visibility = View.INVISIBLE
            cropLayoutView.setImageBitmap(inputBitMap)
            cropLayoutView.visibility = View.VISIBLE
            cropLayoutView.flipImageHorizontally()
        }

        binding.buttonFlipVertically.setOnClickListener {
            binding.imageViewBookCrop.visibility = View.INVISIBLE
            cropLayoutView.setImageBitmap(inputBitMap)
            cropLayoutView.visibility = View.VISIBLE
            cropLayoutView.flipImageVertically()
        }

        binding.buttonRotate.setOnClickListener {
            binding.imageViewBookCrop.visibility = View.INVISIBLE
            cropLayoutView.setImageBitmap(inputBitMap)
            cropLayoutView.visibility = View.VISIBLE
            cropLayoutView.rotateClockwise()
        }

        binding.imageViewDone.setOnClickListener {
            val croppedImage = cropLayoutView.croppedImage
            binding.imageViewBookCrop.setImageBitmap(croppedImage)
            cropLayoutView.visibility = View.INVISIBLE
            binding.imageViewBookCrop.visibility = View.VISIBLE
            goPreviousFragmentWithBackStack(croppedImage)
        }

        binding.radioButtonGroup.setOnCheckedChangeListener { radioGroup, _ ->
            val radioButton = radioGroup.checkedRadioButtonId
            if (radioButton == binding.radioButtonCircular.id) {
                cropLayoutView.cropShape = CropLayoutView.CropShape.OVAL
            } else {
                cropLayoutView.cropShape = CropLayoutView.CropShape.RECTANGLE
            }
        }
    }

    private fun goPreviousFragmentWithBackStack(resultImage: Bitmap) {
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            KEY_AVAILABILITY_CALENDAR_SHOULD_REFRESH.toString(),
            resultImage
        )
        findNavController().popBackStack()
    }
}