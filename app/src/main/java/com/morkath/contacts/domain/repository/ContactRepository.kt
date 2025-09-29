package com.morkath.contacts.domain.repository

import com.morkath.contacts.data.local.database.entity.ContactEntity
import kotlinx.coroutines.flow.Flow

interface ContactRepository {
    /**
     * Get all contacts from the database.
     * @return A Flow emitting a list of ContactEntity objects.
     */
    suspend fun getContact(): Flow<List<ContactEntity>>
    /**
     * Get a contact by its ID.
     * @param id The ID of the contact to retrieve.
     * @return A Flow emitting the ContactEntity object, or null if not found.
     */
    suspend fun getContactById(id: Long): Flow<ContactEntity?>
    /**
     * Search contacts by a query string.
     * @param query The search query.
     * @return A Flow emitting a list of ContactEntity objects that match the query.
     */
    suspend fun searchContacts(query: String): Flow<List<ContactEntity>>
    /**
     * Insert a new contact into the database.
     * @param contact The ContactEntity object to insert.
     * @return The inserted ContactEntity object with its new ID, or null if insertion failed.
     */
    suspend fun insertContact(contact: ContactEntity): ContactEntity?
    /**
     * Update an existing contact in the database.
     * @param contact The ContactEntity object to update.
     * @return True if the update was successful, false otherwise.
     */
    suspend fun updateContact(contact: ContactEntity): Boolean
    /**
     * Delete contacts by their IDs.
     * @param ids A list of contact IDs to delete.
     * @return True if the deletion was successful, false otherwise.
     */
    suspend fun deleteContacts(ids: List<Long>): Boolean

}