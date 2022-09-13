package com.hms.readinghabittracker.ui.goals

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.hms.readinghabittracker.data.model.GoalItem
import com.hms.readinghabittracker.data.repository.GoalsRepository
import com.hms.readinghabittracker.utils.Constant.TAG_GOAL_ID
import com.hms.readinghabittracker.utils.Constant.TAG_GOAL_NAME
import com.hms.readinghabittracker.utils.ContextProvider
import com.hms.readinghabittracker.utils.receiver.GoalsReceiver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val goalsRepository: GoalsRepository,
    private val contextProvider: ContextProvider
) : ViewModel() {

    val allGoalItems: LiveData<List<GoalItem>> =
        goalsRepository.observeAllGoals().asLiveData()
    val allGoalItemsDone: LiveData<List<GoalItem>> =
        goalsRepository.observeGoalsDone().asLiveData()
    private val alarmManager: AlarmManager =
        contextProvider.getContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private fun insertGoalItem(goalItem: GoalItem) {
        viewModelScope.launch {
            val goalItemId = goalsRepository.insertGoal(goalItem)
            scheduleReminder(goalItemId.toInt(), goalItem.name, goalItem.timeStamp.time)
        }
    }

    private fun updateGoalItem(goalItem: GoalItem) {
        viewModelScope.launch {
            goalsRepository.updateGoal(goalItem)
        }
    }

    private fun deleteGoalItem(goalItem: GoalItem) {
        viewModelScope.launch {
            goalsRepository.deleteGoal(goalItem)
        }
    }

    private fun scheduleReminder(goalItemId: Int, goalItemName: String, time: Long) {
        val intent = Intent(contextProvider.getContext(), GoalsReceiver::class.java)
        intent.putExtra(TAG_GOAL_NAME, goalItemName)
        intent.putExtra(TAG_GOAL_ID, goalItemId)
        val pendingIntent = PendingIntent.getBroadcast(
            contextProvider.getContext(),
            goalItemId,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
    }

    private fun cancelReminder(goalItemId: Int) {
        val intent = Intent(contextProvider.getContext(), GoalsReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            contextProvider.getContext(),
            goalItemId,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        alarmManager.cancel(
            pendingIntent
        )
    }

    private fun getNewGoalEntry(
        goalItemName: String,
        goalItemDescription: String,
        goalItemDate: Date,
        goalItemId: Int = 0,
        isDone: Boolean = false
    ): GoalItem {
        return GoalItem(
            id = goalItemId,
            name = goalItemName,
            description = goalItemDescription,
            timeStamp = goalItemDate,
            done = isDone
        )
    }

    fun addNewGoalItem(goalItemName: String, goalItemDescription: String, goalItemDate: Date) {
        val newGoalItem = getNewGoalEntry(goalItemName, goalItemDescription, goalItemDate)
        insertGoalItem(newGoalItem)
    }

    fun getGoalItem(id: Int): LiveData<GoalItem> {
        return goalsRepository.observeGoal(id).asLiveData()
    }
}