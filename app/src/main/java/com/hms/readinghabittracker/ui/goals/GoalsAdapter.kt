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
import java.util.*
import kotlin.concurrent.schedule

class GoalsAdapter(
    private val deleteCallBack: (goalItem: GoalItem) -> Unit,
    private val navCallBack: (id: Int) -> Unit
) :
    ListAdapter<GoalItem, GoalsAdapter.GoalsViewHolder>(diffCallBack) {

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

            goalItemCardView.setOnClickListener {
                navCallBack(goalsItem.id)
            }

            var timer = Timer("check_task", false)
            checkboxGoalsDone.setOnCheckedChangeListener { _, isChecked ->
                timer.cancel()
                timer.purge()
                timer = Timer("check_task:" + goalsItem.id, false)

                if (isChecked != goalsItem.done) {
                    timer.schedule(400) {
                        deleteCallBack(goalsItem)
                    }
                }
            }

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