package com.hms.readinghabittracker.ui.goals

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.util.SparseArray
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import com.hms.readinghabittracker.base.BaseFragment
import com.hms.readinghabittracker.databinding.FragmentGoalsBinding
import com.hms.readinghabittracker.utils.PermissionManager
import com.huawei.hms.kit.awareness.Awareness
import com.huawei.hms.kit.awareness.barrier.TimeBarrier
import com.huawei.hms.kit.awareness.capture.TimeCategoriesResponse

class GoalsFragment :
    BaseFragment<FragmentGoalsBinding, GoalsViewModel>(FragmentGoalsBinding::inflate) {

    override val viewModel: GoalsViewModel by viewModels()

    override fun setupUi() {
        val TIME_DESCRIPTION_MAP = SparseArray<String>()
        TIME_DESCRIPTION_MAP.put(TimeBarrier.TIME_CATEGORY_WEEKDAY, "Today is weekday.")
        TIME_DESCRIPTION_MAP.put(TimeBarrier.TIME_CATEGORY_WEEKEND, "Today is weekend.")
        TIME_DESCRIPTION_MAP.put(TimeBarrier.TIME_CATEGORY_HOLIDAY, "Today is holiday.")
        TIME_DESCRIPTION_MAP.put(TimeBarrier.TIME_CATEGORY_NOT_HOLIDAY, "Today is not holiday.")
        TIME_DESCRIPTION_MAP.put(TimeBarrier.TIME_CATEGORY_MORNING, "Good morning.")
        TIME_DESCRIPTION_MAP.put(TimeBarrier.TIME_CATEGORY_AFTERNOON, "Good afternoon.")
        TIME_DESCRIPTION_MAP.put(TimeBarrier.TIME_CATEGORY_EVENING, "Good evening.")
        TIME_DESCRIPTION_MAP.put(TimeBarrier.TIME_CATEGORY_NIGHT, "Good night.")

        val permissionManager = PermissionManager()
        permissionManager.hasLocationPermission(requireContext(), requireActivity())

        context?.let {
            if (ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.e("AWARENESS KIT", "Izin verilmedi")
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            Awareness.getCaptureClient(it).timeCategories
                // Callback listener for execution success.
                .addOnSuccessListener { timeCategoriesResponse: TimeCategoriesResponse ->
                    val categories = timeCategoriesResponse.timeCategories
                    val timeInfo = categories.timeCategories
                    for (timeCode in timeInfo) {
                        binding.textViewTime.text =
                            TIME_DESCRIPTION_MAP.get(timeCode)
                        Log.e("AWARENESS KIT", TIME_DESCRIPTION_MAP.get(timeCode))
                    }
                }
                .addOnFailureListener { e: Exception? ->
                    binding.textViewTime.text = e?.localizedMessage
                    Log.e("AWARENESS KIT", e?.localizedMessage.toString())
                }
        }

    }
}