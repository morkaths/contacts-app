package com.morkath.contacts.data.local.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey


object AppSettingDataStoreKeys {
    val IS_NOTIFICATION = booleanPreferencesKey("is_notification")
    val THEME_MODE = stringPreferencesKey("theme_mode")
    val SORT_BY = stringPreferencesKey("sort_by")
}