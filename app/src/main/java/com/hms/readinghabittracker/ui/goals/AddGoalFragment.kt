package com.hms.readinghabittracker.ui.goals

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.hms.readinghabittracker.R
import com.hms.readinghabittracker.base.BaseFragment
import com.hms.readinghabittracker.databinding.FragmentAddGoalBinding
import com.hms.readinghabittracker.utils.TimeUtils
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class AddGoalFragment :
    BaseFragment<FragmentAddGoalBinding, GoalsViewModel>(FragmentAddGoalBinding::inflate) {

    override val viewModel: GoalsViewModel by viewModels()
    private val calendar: Calendar = Calendar.getInstance()

    override fun setupUi() {
        binding.editTextGoalDate.apply {
            val datePicker = TimeUtils.getDatePickerListener(this, calendar)
            isFocusable = false
            setOnClickListener {
                DatePickerDialog(
                    requireContext(),
                    R.style.CalendarDatePickerDialogStyle,
                    datePicker,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        }


        binding.editTextGoalTime.apply {
            val timePicker = TimeUtils.getTimePickerListener(this, calendar)
            isFocusable = false
            setOnClickListener {
                TimePickerDialog(
                    requireContext(),
                    R.style.CalendarTimePickerDialogStyle,
                    timePicker,
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false
                ).show()
            }
        }


        binding.buttonSaveGoal.setOnClickListener {
            //TODO Validate inputs
            addNewItem()
            findNavController().navigate(R.id.action_addGoalFragment_to_goalsFragment)
        }
    }

    private fun addNewItem() {
        viewModel.addNewGoalItem(
            binding.editTextGoalTitle.text.toString(),
            binding.editTextGoalDescription.text.toString(),
            calendar.time
        )
    }
}