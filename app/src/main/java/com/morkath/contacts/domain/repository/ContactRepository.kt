package com.morkath.contacts.domain.repository

import com.morkath.contacts.data.local.database.entity.ContactEntity
import kotlinx.coroutines.flow.Flow

interface ContactRepository {
    suspend fun getContact(): Flow<List<ContactEntity>>
    suspend fun getContactById(id: Long): Flow<ContactEntity?>
    suspend fun searchContacts(query: String): Flow<List<ContactEntity>>
    suspend fun insertContact(contact: ContactEntity): ContactEntity?
    suspend fun updateContact(contact: ContactEntity): Boolean
    suspend fun deleteContacts(ids: List<Long>): Boolean

}