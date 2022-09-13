package com.hms.readinghabittracker.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "goals")
data class GoalItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String,
    @ColumnInfo(name = "time_stamp")
    val timeStamp: Date,
    @ColumnInfo(name = "is_done")
    val done: Boolean = false
)