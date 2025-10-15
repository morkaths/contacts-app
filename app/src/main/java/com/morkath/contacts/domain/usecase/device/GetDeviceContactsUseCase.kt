package com.morkath.contacts.domain.usecase.device

import com.morkath.contacts.domain.model.Contact
import com.morkath.contacts.domain.repository.DeviceContactDataSource
import javax.inject.Inject

class GetDeviceContactsUseCase @Inject constructor(
    private val repository: DeviceContactDataSource
) {
    suspend operator fun invoke(): List<Contact> {
        return repository.getDeviceContacts()
    }
}