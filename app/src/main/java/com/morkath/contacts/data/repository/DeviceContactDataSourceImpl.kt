package com.morkath.contacts.data.repository

import android.content.Context
import com.morkath.contacts.domain.model.Contact
import com.morkath.contacts.domain.repository.DeviceContactDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import android.provider.ContactsContract

@Singleton
class DeviceContactDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : DeviceContactDataSource {
    override suspend fun getDeviceContacts(): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val contentResolver = context.contentResolver
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null, null, null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        cursor?.use { // 'use' sẽ tự động đóng cursor sau khi thực hiện xong
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            // Lấy các cột khác nếu cần (ID, Photo URI, ...)

            while (it.moveToNext()) {
                val name = if (nameIndex != -1) it.getString(nameIndex) else "N/A"
                val phoneNumber = if (numberIndex != -1) it.getString(numberIndex) else "N/A"
                contacts.add(
                    Contact(
                        id = 0,
                        name = name,
                        phoneNumber = phoneNumber,
                        email = null,
                        isFavorite = false,
                        photoUri = null
                    )
                )
            }
        }
        return contacts
    }
}