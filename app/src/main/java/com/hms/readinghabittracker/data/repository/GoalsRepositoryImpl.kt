package com.hms.readinghabittracker.data.repository

import com.hms.readinghabittracker.data.local.database.dao.GoalsDao
import com.hms.readinghabittracker.data.model.GoalItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GoalsRepositoryImpl @Inject constructor(
    private val goalsDao: GoalsDao
) : GoalsRepository {
    override suspend fun insertGoal(goalItem: GoalItem): Long = goalsDao.insertGoal(goalItem)

    override suspend fun updateGoal(goalItem: GoalItem) = goalsDao.updateGoal(goalItem)

    override suspend fun deleteGoal(goalItem: GoalItem) = goalsDao.deleteGoal(goalItem)

    override fun observeGoal(id: Int): Flow<GoalItem> = goalsDao.observeGoal(id)

    override fun observeAllGoals(): Flow<List<GoalItem>> = goalsDao.observeAllGoals()

    override fun observeGoalsDone(): Flow<List<GoalItem>> = goalsDao.observeGoalDone()
}
