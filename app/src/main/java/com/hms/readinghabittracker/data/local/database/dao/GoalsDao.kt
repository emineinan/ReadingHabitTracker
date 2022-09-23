package com.hms.readinghabittracker.data.local.database.dao

import androidx.room.*
import com.hms.readinghabittracker.data.model.GoalItem
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGoal(goalItem: GoalItem): Long

    @Update
    suspend fun updateGoal(goalItem: GoalItem)

    @Delete
    suspend fun deleteGoal(goalItem: GoalItem)

    @Query("SELECT * FROM goals WHERE id = :id")
    fun observeGoal(id: Int): Flow<GoalItem>

    @Query("SELECT * FROM goals WHERE is_done = 0 ORDER BY time_stamp ASC")
    fun observeAllGoals(): Flow<List<GoalItem>>

    @Query("SELECT * FROM goals WHERE is_done = 1 ORDER BY time_stamp ASC")
    fun observeGoalDone(): Flow<List<GoalItem>>
}