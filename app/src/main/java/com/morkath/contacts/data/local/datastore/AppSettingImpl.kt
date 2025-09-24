package com.morkath.contacts.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AppSettingImpl(private val context: Context) : AppSetting {

    // Create DataStore instance (Key-value storage)
    private val Context.dataStore: DataStore<Preferences>
        by preferencesDataStore(name = "app-setting-pref")

    // Read data from DataStore
    override val appSettingFlow: Flow<AppSettingData>
        get() = context.dataStore.data
            .map { pref ->
                AppSettingData(
                    isNotification = pref[AppSettingDataStoreKeys.IS_NOTIFICATION] ?: true,
                    themeMode = pref[AppSettingDataStoreKeys.THEME_MODE]?.let {
                        when (it) {
                            "LIGHT" -> ThemeMode.LIGHT
                            "DARK" -> ThemeMode.DARK
                            else -> ThemeMode.SYSTEM_DEFAULT
                        }
                    } ?: ThemeMode.SYSTEM_DEFAULT,
                    sortBy = pref[AppSettingDataStoreKeys.SORT_BY]?.let {
                        when (it) {
                            "PHONE_NUMBER" -> SortBy.PHONE_NUMBER
                            "EMAIL" -> SortBy.EMAIL
                            else -> SortBy.NAME
                        }
                    } ?: SortBy.NAME
                )
            }

    override suspend fun setNotification(isNotification: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[AppSettingDataStoreKeys.IS_NOTIFICATION] = isNotification
        }
    }

    override suspend fun getIsNotification(): Boolean {
        // appSettingFlow.first().isNotification
        return withContext(Dispatchers.IO) {
            context.dataStore.data
                .map { it[AppSettingDataStoreKeys.IS_NOTIFICATION] ?: true }
                .first()
        }
    }
}