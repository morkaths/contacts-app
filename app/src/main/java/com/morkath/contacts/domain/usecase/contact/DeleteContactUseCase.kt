package com.morkath.contacts.domain.usecase.contact

import com.morkath.contacts.domain.repository.ContactRepository
import javax.inject.Inject

class DeleteContactUseCase @Inject constructor(private val repository: ContactRepository) {
    suspend operator fun invoke(id: Long) = repository.deleteContact(id)
}