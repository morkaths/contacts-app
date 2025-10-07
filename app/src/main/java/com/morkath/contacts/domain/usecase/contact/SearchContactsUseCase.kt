package com.morkath.contacts.domain.usecase.contact

import com.morkath.contacts.domain.repository.ContactRepository
import javax.inject.Inject

class SearchContactsUseCase @Inject constructor(private val repository: ContactRepository) {
    suspend operator fun invoke(query: String) = repository.searchContacts(query)
}