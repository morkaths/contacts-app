package com.morkath.contacts.data.repository

import android.icu.util.Calendar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.morkath.contacts.data.local.database.dao.ContactDao
import com.morkath.contacts.data.local.database.entity.ContactEntity
import com.morkath.contacts.domain.repository.ContactRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class ContactRepositoryImpl(
    private val contactDao: ContactDao
) : ContactRepository {
    /**
     * Get all contacts from the database.
     * @return A Flow emitting a list of ContactEntity objects.
     */
    override suspend fun getContact(): Flow<List<ContactEntity>> =
        contactDao.getAllContacts().flowOn(Dispatchers.IO)

    /**
     * Get a contact by its ID.
     * @param id The ID of the contact to retrieve.
     * @return A Flow emitting the ContactEntity if found, or null if not found.
     */
    override suspend fun getContactById(id: Long): Flow<ContactEntity?> =
        contactDao.getContactById(id).flowOn(Dispatchers.IO)

    override suspend fun searchContacts(query: String): Flow<List<ContactEntity>> =
        contactDao.searchContacts(query).flowOn(Dispatchers.IO)

    override suspend fun insertContact(contact: ContactEntity): ContactEntity? {
        val id = contactDao.insertContact(contact.copy(
            updatedAt = Calendar.getInstance().timeInMillis
        ))
        return if (id > 0) contact.copy(id = id) else null
    }

    override suspend fun updateContact(contact: ContactEntity): Boolean {
        return withContext(Dispatchers.IO) {
            contactDao.updateContact(contact.copy(
                updatedAt = Calendar.getInstance().timeInMillis
            )) > 0
        }
    }

    override suspend fun deleteContacts(ids: List<Long>): Boolean {
        return withContext(Dispatchers.IO) {
            contactDao.deleteContact(ids) > 0
        }
    }
}