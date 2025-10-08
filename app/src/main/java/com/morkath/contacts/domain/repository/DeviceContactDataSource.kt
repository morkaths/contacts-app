package com.morkath.contacts.domain.repository

import com.morkath.contacts.domain.model.Contact

interface DeviceContactDataSource {
    suspend fun getDeviceContacts(): List<Contact>
}