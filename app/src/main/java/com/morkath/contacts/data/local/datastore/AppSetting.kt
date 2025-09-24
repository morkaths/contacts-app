package com.morkath.contacts.data.local.datastore

import kotlinx.coroutines.flow.Flow

interface AppSetting {
    val appSettingFlow: Flow<AppSettingData>

    suspend fun setNotification(isNotification: Boolean)
    suspend fun getIsNotification(): Boolean
//    suspend fun setMode(isDarkMode: Boolean)
//    suspend fun updateSortBy(sortBy: SortBy)
}