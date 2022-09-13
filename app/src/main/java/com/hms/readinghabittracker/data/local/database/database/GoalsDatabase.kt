package com.hms.readinghabittracker.data.local.database.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hms.readinghabittracker.data.local.database.converters.Converters
import com.hms.readinghabittracker.data.local.database.dao.GoalsDao
import com.hms.readinghabittracker.data.model.GoalItem
import com.hms.readinghabittracker.utils.Constant

@Database(entities = [GoalItem::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class GoalsDatabase() : RoomDatabase() {

    abstract fun goalsDao(): GoalsDao

    companion object {
        @Volatile
        private var INSTANCE: GoalsDatabase? = null

        fun getDatabase(context: Context): GoalsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GoalsDatabase::class.java,
                    Constant.DATABASE_NAME
                )
                    .fallbackToDestructiveMigration() //Data will be lost of there is a schema change
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}