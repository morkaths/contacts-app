package com.morkath.contacts.domain.usecase.device

import com.morkath.contacts.domain.model.Contact
import com.morkath.contacts.domain.repository.DeviceContactDataSource
import javax.inject.Inject

class AddContactToDeviceUseCase @Inject constructor(
    private val repository: DeviceContactDataSource
) {
    suspend operator fun invoke(contact: Contact): Boolean {
        return repository.addContactToDevice(contact)
    }
}