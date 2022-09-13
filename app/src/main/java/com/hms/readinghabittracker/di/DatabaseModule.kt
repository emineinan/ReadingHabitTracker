package com.hms.readinghabittracker.di

import android.content.Context
import androidx.room.Room
import com.hms.readinghabittracker.data.local.database.dao.GoalsDao
import com.hms.readinghabittracker.data.local.database.database.GoalsDatabase
import com.hms.readinghabittracker.data.repository.GoalsRepositoryImpl
import com.hms.readinghabittracker.data.repository.GoalsRepository
import com.hms.readinghabittracker.utils.Constant.DATABASE_NAME
import com.hms.readinghabittracker.utils.ContextProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideGoalsDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context, GoalsDatabase::class.java, DATABASE_NAME).build()

    @Provides
    @Singleton
    fun provideGoalsDao(
        database: GoalsDatabase
    ) = database.goalsDao()


    @Provides
    @Singleton
    fun provideDefaultGoalsRepository(
        goalsDao: GoalsDao
    ) = GoalsRepositoryImpl(goalsDao) as GoalsRepository

    @Provides
    @Singleton
    fun provideContextProvider(@ApplicationContext context: Context): ContextProvider {
        return object : ContextProvider {
            override fun getContext(): Context {
                return context
            }
        }
    }
}