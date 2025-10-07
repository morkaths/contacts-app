package com.morkath.contacts.domain.usecase.contact

import com.morkath.contacts.domain.model.Contact
import com.morkath.contacts.domain.repository.ContactRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetContactsUseCase @Inject constructor(private val repository: ContactRepository) {
    suspend operator fun invoke(): Flow<List<Contact>> = repository.getContacts()
}