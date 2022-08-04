package com.hms.readinghabittracker.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("settings")

@Singleton
class DataStoreManager @Inject constructor(val context: Context) {

    private val settingsDataStore = context.dataStore

    val isOnBoardingShowed: Flow<Boolean>
        get() = settingsDataStore.data.map { preferences ->
            preferences[KEY_ONBOARD] ?: false
        }

    suspend fun saveOnBoardingState(state: Boolean) {
        settingsDataStore.edit { preferences ->
            preferences[KEY_ONBOARD] = state
        }
    }

    companion object {
        val KEY_ONBOARD = booleanPreferencesKey("onboard")
    }
}