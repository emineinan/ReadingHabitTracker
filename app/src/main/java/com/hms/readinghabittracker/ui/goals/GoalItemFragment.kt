package com.hms.readinghabittracker.ui.goals

import androidx.fragment.app.viewModels
import com.hms.readinghabittracker.base.BaseFragment
import com.hms.readinghabittracker.databinding.FragmentGoalItemBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates

@AndroidEntryPoint
class GoalItemFragment :
    BaseFragment<FragmentGoalItemBinding, GoalsViewModel>(FragmentGoalItemBinding::inflate) {

    override val viewModel: GoalsViewModel by viewModels()

    private var goalItemId by Delegates.notNull<Int>()

    override fun setupUi() {
        arguments?.let {
            goalItemId = it.getInt(ID)
        }

        viewModel.getGoalItem(goalItemId).observe(this.viewLifecycleOwner) { goalItem ->
            goalItem.let {
                binding.textViewGoalItemTitle.text = goalItem.name
                binding.textViewGoalItemDescription.text = goalItem.description
                binding.textViewGoalItemDate.text = goalItem.timeStamp.toString()
            }
        }
    }

    companion object {
        var ID = "id"
        val TAG = "GoalItemFragment"
    }
}