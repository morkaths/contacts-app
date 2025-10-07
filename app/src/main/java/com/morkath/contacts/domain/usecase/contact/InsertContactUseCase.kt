package com.morkath.contacts.domain.usecase.contact

import com.morkath.contacts.domain.model.Contact
import com.morkath.contacts.domain.repository.ContactRepository
import javax.inject.Inject

class InsertContactUseCase @Inject constructor(private val repository: ContactRepository) {
    suspend operator fun invoke(contact: Contact) = repository.insertContact(contact)
}