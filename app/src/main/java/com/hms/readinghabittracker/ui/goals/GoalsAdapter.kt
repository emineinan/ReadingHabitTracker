package com.hms.readinghabittracker.ui.goals

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hms.readinghabittracker.R
import com.hms.readinghabittracker.data.model.GoalItem
import com.hms.readinghabittracker.databinding.GoalsItemBinding

class GoalsAdapter : ListAdapter<GoalItem, GoalsAdapter.GoalsViewHolder>(diffCallBack) {

    class GoalsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = GoalsItemBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalsViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.goals_item, parent, false)
        return GoalsViewHolder(view)
    }

    override fun onBindViewHolder(holder: GoalsViewHolder, postion: Int) {
        holder.binding.apply {
            val goalsItem = getItem(postion)
            textViewGoalName.text = goalsItem.name
            textViewGoalsDate.text = goalsItem.timeStamp.toString()
            checkboxGoalsDone.isChecked = goalsItem.done
        }
    }

    companion object {
        private val diffCallBack = object : DiffUtil.ItemCallback<GoalItem>() {
            override fun areItemsTheSame(oldItem: GoalItem, newItem: GoalItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: GoalItem, newItem: GoalItem): Boolean {
                return (oldItem.name == newItem.name && oldItem.description == newItem.description && oldItem.timeStamp == newItem.timeStamp)
            }
        }
    }
}