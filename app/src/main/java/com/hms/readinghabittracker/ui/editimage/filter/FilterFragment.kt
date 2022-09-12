package com.hms.readinghabittracker.ui.editimage.filter

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.hms.readinghabittracker.base.BaseFragment
import com.hms.readinghabittracker.databinding.FragmentFilterBinding
import com.hms.readinghabittracker.utils.Constant
import com.hms.readinghabittracker.utils.OnItemClickListener
import com.huawei.hms.image.vision.ImageVision
import com.huawei.hms.image.vision.ImageVisionImpl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject

@AndroidEntryPoint
class FilterFragment :
    BaseFragment<FragmentFilterBinding, FilterViewModel>(FragmentFilterBinding::inflate),
    OnItemClickListener {
    override val viewModel: FilterViewModel by viewModels()

    private lateinit var adapter: FilterAdapter
    private lateinit var imageVisionAPI: ImageVisionImpl
    var authJson: JSONObject? = null
    private var filteredImageList: ArrayList<Bitmap> = ArrayList()
    var initCode = -1
    private var inputImage: Bitmap? = null
    private val args: FilterFragmentArgs by navArgs()
    var imageWithFilter: Bitmap? = null


    override fun setupUi() {
        inputImage = args.image
        binding.imageViewFilteredImageBook.setImageBitmap(inputImage)
        binding.imageViewFilteredImageBook.visibility = View.VISIBLE

        initAuthJson()
        initImageVisionAPI(requireContext())

        setAdapter()

        binding.progressBar.visibility = View.VISIBLE
        startFilterRecyclerView(1.0)

        binding.imageViewDone.setOnClickListener {
            goPreviousFragmentWithBackStack(imageWithFilter)
        }
    }

    private fun setAdapter() {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        adapter = FilterAdapter(filteredImageList, this)
        binding.recyclerViewFilteredImages.adapter = adapter
        binding.recyclerViewFilteredImages.setHasFixedSize(true)
        binding.recyclerViewFilteredImages.layoutManager = layoutManager
    }

    private fun goPreviousFragmentWithBackStack(resultImage: Bitmap?) {
        resultImage?.let {
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                Constant.KEY_AVAILABILITY_CALENDAR_SHOULD_REFRESH.toString(),
                resultImage
            )
        }
        findNavController().popBackStack()
    }

    private fun startFilterRecyclerView(intensity: Double) {
        lifecycleScope.launch {
            val jsonObject = JSONObject()
            val taskJson = JSONObject()
            for (filterNumber in 0..24) {
                try {
                    taskJson.put(Constant.intensity, intensity)
                    taskJson.put(Constant.filterType, filterNumber)
                    taskJson.put(Constant.compressRate, 1)
                    jsonObject.put(Constant.requestId, 1)
                    jsonObject.put(Constant.taskJson, taskJson)
                    jsonObject.put(Constant.authJson, authJson)
                } catch (e: JSONException) {
                    Log.e(TAG, "JSONException: ${e.message}")
                }

                val visionResult = withContext(Dispatchers.IO) {
                    imageVisionAPI.getColorFilter(
                        jsonObject,
                        inputImage
                    )
                }
                val image = visionResult.image
                filteredImageList.add(image)
            }
            imageVisionAPI.stop()
            adapter.updateList(filteredImageList)
            binding.progressBar.visibility = View.INVISIBLE
        }
    }

    private fun initImageVisionAPI(context: Context?) {
        imageVisionAPI = ImageVision.getInstance(context)
        imageVisionAPI.setVisionCallBack(object : ImageVision.VisionCallBack {
            override fun onSuccess(successCode: Int) {
                initCode = imageVisionAPI.init(context, authJson)
                Log.d(TAG, "onSuccess: init ImageVisionAPI :$initCode")
            }

            override fun onFailure(errorCode: Int) {
                Log.d(TAG, "onFailure: $errorCode")
            }
        })
    }

    private fun initAuthJson() {
        try {
            authJson = JSONObject().apply {
                put("projectId", Constant.PROJECT_ID)
                put("appId", Constant.APP_ID)
                put("authApiKey", Constant.API_KEY)
                put("clientSecret", Constant.CLIENT_SECRET)
                put("clientId", Constant.CLIENT_ID)
            }
        } catch (e: JSONException) {
            Log.i("Error with authJson", e.toString())
        }
    }

    override fun onItemClicked(position: Int) {
        binding.textViewFilterName.text = Constant.filters[position]
        binding.imageViewFilteredImageBook.setImageBitmap(filteredImageList[position])
        imageWithFilter = filteredImageList[position]
    }

    companion object {
        private const val TAG = "FilterFragment"
    }
}