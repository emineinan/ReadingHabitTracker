package com.hms.readinghabittracker.ui.goals

import android.annotation.SuppressLint
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hms.readinghabittracker.R
import com.hms.readinghabittracker.base.BaseFragment
import com.hms.readinghabittracker.data.model.GoalItem
import com.hms.readinghabittracker.databinding.FragmentGoalsBinding
import com.hms.readinghabittracker.utils.PermissionUtils
import com.hms.readinghabittracker.utils.TimeUtils
import com.huawei.hms.kit.awareness.Awareness
import com.huawei.hms.kit.awareness.capture.TimeCategoriesResponse
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class GoalsFragment :
    BaseFragment<FragmentGoalsBinding, GoalsViewModel>(FragmentGoalsBinding::inflate),
    EasyPermissions.PermissionCallbacks {

    override val viewModel: GoalsViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private val showDone: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }


    override fun setupUi() {
        requestPermissions()

        binding.floatingActionButtonGoal.setOnClickListener {
            findNavController().navigate(R.id.action_goalsFragment_to_addGoalFragment)
        }

        recyclerView = binding.recyclerViewGoals
        recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = GoalsAdapter()
        recyclerView.adapter = adapter

        var observableList = MutableLiveData<List<GoalItem>>()

        showDone.observe(this.viewLifecycleOwner) { newValue ->
            observableList.removeObservers(this.viewLifecycleOwner)

            if (newValue == true) {
                observableList = viewModel.allGoalItemsDone as MutableLiveData
                if (activity is AppCompatActivity) {
                    (activity as AppCompatActivity).supportActionBar?.title = "Goal done"
                }
            } else {
                observableList = viewModel.allGoalItems as MutableLiveData
                if (activity is AppCompatActivity) {
                    (activity as AppCompatActivity).supportActionBar?.title = "Goal to do"
                }
            }
            observableList.observe(this.viewLifecycleOwner) { items ->
                items.let {
                    adapter.submitList(it)
                }
            }
        }

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