package com.hms.readinghabittracker.data.repository

import com.hms.readinghabittracker.data.model.GoalItem
import kotlinx.coroutines.flow.Flow

interface GoalsRepository {
    suspend fun insertGoal(goalItem: GoalItem): Long

    suspend fun updateGoal(goalItem: GoalItem)

    suspend fun deleteGoal(goalItem: GoalItem)

    fun observeGoal(id: Int): Flow<GoalItem>

    fun observeAllGoals(): Flow<List<GoalItem>>

    fun observeGoalsDone(): Flow<List<GoalItem>>
}