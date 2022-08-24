package com.hms.readinghabittracker.ui.goals

import android.annotation.SuppressLint
import androidx.fragment.app.viewModels
import com.hms.readinghabittracker.base.BaseFragment
import com.hms.readinghabittracker.databinding.FragmentGoalsBinding
import com.hms.readinghabittracker.utils.PermissionUtils
import com.hms.readinghabittracker.utils.TimeUtils
import com.huawei.hms.kit.awareness.Awareness
import com.huawei.hms.kit.awareness.capture.TimeCategoriesResponse

class GoalsFragment :
    BaseFragment<FragmentGoalsBinding, GoalsViewModel>(FragmentGoalsBinding::inflate) {

    override val viewModel: GoalsViewModel by viewModels()

    override fun setupUi() {
        PermissionUtils.hasLocationPermission(requireContext(), requireActivity())
        getTimes()
    }

    @SuppressLint("MissingPermission")  //Not missing we checked the permissions
    private fun getTimes() {
        val timeDescriptionMap = TimeUtils.initTimes()
        Awareness.getCaptureClient(requireContext()).timeCategories
            .addOnSuccessListener { timeCategoriesResponse: TimeCategoriesResponse ->
                val categories = timeCategoriesResponse.timeCategories
                val timeInfo = categories.timeCategories
                for (timeCode in timeInfo) {
                    binding.textViewTime.text =
                        timeDescriptionMap.get(timeCode)
                }
            }
            .addOnFailureListener { e: Exception? ->
                binding.textViewTime.text = e?.localizedMessage
            }
    }
}