package com.morkath.contacts.data.repository

import android.content.Context
import android.provider.ContactsContract
import android.util.Log
import com.morkath.contacts.data.local.database.dao.ContactDao
import com.morkath.contacts.data.local.database.entity.ContactEntity
import com.morkath.contacts.data.mapper.toEntity
import com.morkath.contacts.domain.model.Contact
import com.morkath.contacts.domain.repository.DeviceContactDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import android.content.ContentProviderOperation
import kotlin.compareTo
import kotlin.toString

@Singleton
class DeviceContactDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val contactDao: ContactDao
) : DeviceContactDataSource {
    override suspend fun getDeviceContacts(): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val contentResolver = context.contentResolver
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null, null, null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            // Lấy các cột khác nếu cần (ID, Photo URI, ...)

            val deviceContactsMap = mutableMapOf<String, Contact>()
            while (it.moveToNext()) {
                val name = if (nameIndex != -1) it.getString(nameIndex) else "N/A"
                val phoneNumber = if (numberIndex != -1) it.getString(numberIndex) else "N/A"

                // Chuẩn hóa số điện thoại để tránh trùng lặp do định dạng khác nhau
                val normalizedPhoneNumber = phoneNumber.replace(Regex("[\\s-]"), "")
                if (!deviceContactsMap.containsKey(normalizedPhoneNumber)) {
                    deviceContactsMap[normalizedPhoneNumber] = Contact(
                        id = 0,
                        name = name,
                        phoneNumber = normalizedPhoneNumber,
                        email = null,
                        isFavorite = false,
                        photoUri = null
                    )
                }
            }
            contacts.addAll(deviceContactsMap.values)
        }
        return contacts
    }

    suspend fun syncDeviceContactsToLocalDb(): Pair<Int, Int> = withContext(Dispatchers.IO) {
        val deviceContacts = getDeviceContacts()
        val localContactEntities = contactDao.getAllContactsAsList()
        val localContactsMap = localContactEntities.associateBy { it.phoneNumber?.replace(Regex("[\\s-]"), "") }

        val contactsToAdd = mutableListOf<Contact>()
        val contactsToUpdate = mutableListOf<ContactEntity>()

        for (deviceContact in deviceContacts) {
            val normalizedPhoneNumber = deviceContact.phoneNumber.replace(Regex("[\\s-]"), "")
            val localEntity = localContactsMap[normalizedPhoneNumber]
            if (localEntity == null) {
                contactsToAdd.add(deviceContact)
            } else {
                if (localEntity.name != deviceContact.name) {
                    val updatedEntity = localEntity.copy(name = deviceContact.name)
                    contactsToUpdate.add(updatedEntity)
                }
            }
        }

        if (contactsToAdd.isNotEmpty()) {
            val entitiesToAdd = contactsToAdd.map { it.toEntity() }
            contactDao.insertAll(entitiesToAdd)
            Log.d("ContactSync", "Added ${entitiesToAdd.size} new contacts.")
        }

        if (contactsToUpdate.isNotEmpty()) {
            contactDao.updateAll(contactsToUpdate)
        }

        if (contactsToAdd.isEmpty() && contactsToUpdate.isEmpty()) {
            Log.d("ContactSync", "Contacts are already up to date.")
        }

        return@withContext Pair(contactsToAdd.size, contactsToUpdate.size)
    }

    override suspend fun addContactToDevice(contact: Contact): Long? = withContext(Dispatchers.IO) {
        val ops = ArrayList<ContentProviderOperation>()

        // Bước 1: Tạo một raw contact mới và yêu cầu ContentResolver trả về URI của nó
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
            .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
            .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
            .build())

        // Bước 2: Thêm tên cho raw contact
        // withValueBackReference(..., 0) tham chiếu đến kết quả của thao tác đầu tiên (vị trí 0 trong list ops)
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
            .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contact.name)
            .build())

        // Bước 3: Thêm số điện thoại cho raw contact
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contact.phoneNumber)
            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
            .build())

        // Thêm email nếu có
        if (!contact.email.isNullOrBlank()) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Email.DATA, contact.email)
                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                .build())
        }

        try {
            // Bước 4: Thực thi batch operations
            val results = context.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
            Log.d("DeviceContact", "Added contact '${contact.name}' to device.")

            // Bước 5: Lấy RawContactId từ kết quả trả về và truy vấn ContactId
            val rawContactUri = results[0].uri
            if (rawContactUri != null) {
                // Truy vấn bảng RawContacts để lấy CONTACT_ID dựa trên _ID (chính là rawContactId)
                val cursor = context.contentResolver.query(
                    rawContactUri,
                    arrayOf(ContactsContract.RawContacts.CONTACT_ID),
                    null,
                    null,
                    null
                )
                cursor?.use {
                    if (it.moveToFirst()) {
                        val contactIdIndex = it.getColumnIndex(ContactsContract.RawContacts.CONTACT_ID)
                        if (contactIdIndex != -1) {
                            val deviceContactId = it.getLong(contactIdIndex)
                            Log.d("DeviceContact", "Retrieved new device contact ID: $deviceContactId")
                            return@withContext deviceContactId
                        }
                    }
                }
            }
            // Nếu không lấy được ID, trả về null
            return@withContext null
        } catch (e: Exception) {
            Log.e("DeviceContact", "Error adding contact to device: ${e.message}", e)
            return@withContext null
        }
    }


    override suspend fun updateContactOnDevice(contact: Contact): Boolean = withContext(Dispatchers.IO) {
        try {
            val ops = ArrayList<ContentProviderOperation>()
            // Update name
            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(
                    "${ContactsContract.Data.CONTACT_ID}=? AND ${ContactsContract.Data.MIMETYPE}=?",
                    arrayOf(contact.id.toString(), ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                )
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contact.name)
                .build())
            // Update phone
            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(
                    "${ContactsContract.Data.CONTACT_ID}=? AND ${ContactsContract.Data.MIMETYPE}=?",
                    arrayOf(contact.id.toString(), ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                )
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contact.phoneNumber)
                .build())
            context.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
            true
        } catch (e: Exception) {
            Log.e("DeviceContact", "Error updating contact: ${e.message}", e)
            false
        }
    }

    override suspend fun deleteContactFromDevice(contactId: Long): Boolean = withContext(Dispatchers.IO) {
        try {
            val uri = ContactsContract.RawContacts.CONTENT_URI
            val selection = "${ContactsContract.RawContacts.CONTACT_ID}=?"
            val args = arrayOf(contactId.toString())
            val rows = context.contentResolver.delete(uri, selection, args)
            rows > 0
        } catch (e: Exception) {
            Log.e("DeviceContact", "Error deleting contact: ${e.message}", e)
            false
        }
    }

}