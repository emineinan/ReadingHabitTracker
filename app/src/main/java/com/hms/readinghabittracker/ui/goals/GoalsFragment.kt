package com.hms.readinghabittracker.ui.goals

import android.annotation.SuppressLint
import android.util.Log
import androidx.fragment.app.viewModels
import com.hms.readinghabittracker.R
import com.hms.readinghabittracker.base.BaseFragment
import com.hms.readinghabittracker.databinding.FragmentGoalsBinding
import com.hms.readinghabittracker.utils.PermissionUtils
import com.hms.readinghabittracker.utils.TimeUtils
import com.hms.readinghabittracker.utils.dialog.DaysDialog
import com.huawei.hms.kit.awareness.Awareness
import com.huawei.hms.kit.awareness.capture.TimeCategoriesResponse
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class GoalsFragment :
    BaseFragment<FragmentGoalsBinding, GoalsViewModel>(FragmentGoalsBinding::inflate),
    EasyPermissions.PermissionCallbacks {

    override val viewModel: GoalsViewModel by viewModels()
    
    val selectedDays: MutableList<Int> = ArrayList()

    override fun setupUi() {
        requestPermissions()

        binding.buttonDays.setOnClickListener {
            daysDialogCreate()
        }
        binding.buttonTime.setOnClickListener {

        }
        binding.buttonSaveGoals.setOnClickListener {

        }
    }

    private fun daysDialogCreate() {
        val customDialog = DaysDialog.getInstance(requireContext())
            .setPositiveButton(
                "Ok",
                object : DaysDialog.ICustomDialogClickListener {
                    override fun onClick() {
                    }
                }) {
                binding.buttonDays.text = it.toString()
                selectedDays.clear()
                for(i in it){
                    when (i) {
                        "Sunday" -> {
                            selectedDays.add(1)
                        }
                        "Monday" -> {
                            selectedDays.add(2)
                        }
                        "Tuesday" -> {
                            selectedDays.add(3)
                        }
                        "Wednesday" -> {
                            selectedDays.add(4)
                        }
                        "Thursday" -> {
                            selectedDays.add(5)
                        }
                        "Friday" -> {
                            selectedDays.add(6)
                        }
                        "Saturday" -> {
                            selectedDays.add(7)
                        }
                    }
                }
            }
            .setCancelButton("Cancel")
            .createDialog()
        customDialog.showDialog()
    }

    private fun requestPermissions() {
        if (PermissionUtils.hasLocationPermissions(requireContext())) {
            getTimes()
            return
        }
        EasyPermissions.requestPermissions(
            this,
            getString(R.string.location_permission),
            PermissionUtils.LOCATION_REQUEST_CODE,
            PermissionUtils.ACCESS_COARSE_LOCATION,
            PermissionUtils.ACCESS_FINE_LOCATION
        )
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @SuppressLint("MissingPermission")  //Not missing we checked the permissions
    private fun getTimes() {
        val timeDescriptionMap = TimeUtils.initTimes()
        Awareness.getCaptureClient(requireContext()).timeCategories
            .addOnSuccessListener { timeCategoriesResponse: TimeCategoriesResponse ->
                val categories = timeCategoriesResponse.timeCategories
                val timeInfo = categories.timeCategories.last()
                binding.textViewTime.text = timeDescriptionMap.get(timeInfo)
            }
            .addOnFailureListener { e: Exception? ->
                Log.e("Awareness Kit", e?.localizedMessage.toString())
            }
    }

    override fun onResume() {
        super.onResume()
        if (PermissionUtils.hasLocationPermissions(requireContext())) {
            getTimes()
        }
    }
}