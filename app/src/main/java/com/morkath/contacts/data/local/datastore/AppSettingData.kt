package com.morkath.contacts.data.local.datastore

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM_DEFAULT
}
enum class SortBy {
    NAME,
    PHONE_NUMBER,
    EMAIL
}

data class AppSettingData(
    val isNotification: Boolean = true,
    val themeMode: ThemeMode = ThemeMode.SYSTEM_DEFAULT,
    val sortBy: SortBy = SortBy.NAME
)
