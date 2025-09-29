package com.morkath.contacts.data.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import com.morkath.contacts.data.local.database.entity.ContactEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    /**
     * Get all contacts from the database.
     */
    @Query("""SELECT * FROM contact ORDER BY name ASC""")
    fun getAllContacts(): Flow<List<ContactEntity>>

    /**
     * Get a contact by its ID.
     */
    @Query("""SELECT * FROM contact WHERE id = :id""")
    fun getContactById(id: Long): Flow<ContactEntity?>

    /**
     * Search contacts by a query string.
     */
    @Query("""
        SELECT * FROM contact
        WHERE name LIKE '%' || :query || '%'
            OR phone_number LIKE '%' || :query || '%'
            OR email LIKE '%' || :query || '%'
            OR address LIKE '%' || :query || '%'
    """)
    fun searchContacts(query: String): Flow<List<ContactEntity>>

    /**
     * Insert a new contact into the database.
     * If a contact with the same ID already exists, it will be replaced.
     * @return The ID of the inserted contact.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: ContactEntity): Long

    /**
     * Update an existing contact in the database.
     */
    @Update
    suspend fun updateContact(contact: ContactEntity): Int

    /**
     * Delete contacts by their IDs.
     */
    @Query("""DELETE FROM contact WHERE id IN (:ids)""")
    fun deleteContact(ids: List<Long>): Int

}