package com.morkath.contacts.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contact")
data class ContactEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    @ColumnInfo(name = "phone_number")
    val phoneNumber: String,
    val email: String?,
    val address: String?,
    val birthday: String?,
    val notes: String?,
    @ColumnInfo(name = "photo_uri")
    val photoUri: String?,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)