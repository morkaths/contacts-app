package com.morkath.contacts.domain.usecase.contact

import com.morkath.contacts.domain.repository.ContactRepository
import javax.inject.Inject

class DeleteContactsUseCase @Inject constructor(private val repository: ContactRepository) {
    suspend operator fun invoke(ids: List<Long>) = repository.deleteContacts(ids)
}