package com.morkath.contacts.data.repository

import android.icu.util.Calendar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.morkath.contacts.data.mapper.toDomain
import com.morkath.contacts.data.mapper.toEntity
import com.morkath.contacts.data.local.database.dao.ContactDao
import com.morkath.contacts.domain.model.Contact
import com.morkath.contacts.domain.repository.ContactRepository
import com.morkath.contacts.domain.repository.DeviceContactDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ContactRepositoryImpl @Inject constructor(
    private val contactDao: ContactDao,
) : ContactRepository {

    override suspend fun getContacts(): Flow<List<Contact>> {
        return contactDao.getAllContacts()
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun getContactById(id: Long): Flow<Contact?> {
        return contactDao.getContactById(id)
            .map { it?.toDomain() }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun searchContacts(query: String): Flow<List<Contact>> {
        return contactDao.searchContacts("%$query%")
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun insertContact(contact: Contact): Contact? {
        return withContext(Dispatchers.IO) {
            val entity = contact.toEntity().copy(updatedAt = Calendar.getInstance().timeInMillis)
            val id = contactDao.insertContact(entity)
            if (id != -1L) {
                contactDao.getContactById(id).first()?.toDomain()
            } else {
                null
            }
        }
    }

    override suspend fun updateContact(contact: Contact): Boolean {
        return withContext(Dispatchers.IO) {
            val entity = contact.toEntity().copy(updatedAt = Calendar.getInstance().timeInMillis)
            contactDao.updateContact(entity) > 0
        }
    }

    override suspend fun deleteContact(id: Long): Boolean {
        return withContext(Dispatchers.IO) {
            contactDao.deleteContact(listOf(id)) > 0
        }
    }

    override suspend fun deleteContacts(ids: List<Long>): Boolean {
        return withContext(Dispatchers.IO) {
            contactDao.deleteContact(ids) > 0
        }
    }
}