package com.morkath.contacts.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.morkath.contacts.data.local.database.dao.ContactDao
import com.morkath.contacts.data.local.database.entity.ContactEntity

@Database(
    entities = [
        ContactEntity::class
    ],
    version = 1,
    exportSchema = false
)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
}