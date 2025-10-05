package com.morkath.contacts.domain.mapper

import com.morkath.contacts.data.local.database.entity.ContactEntity
import com.morkath.contacts.domain.model.Contact
import javax.inject.Inject

class ContactMapper @Inject constructor() {
    fun Contact.toEntity(): ContactEntity = ContactEntity(
        id = id,
        name = name,
        phoneNumber = phoneNumber,
        email = email,
        address = address,
        birthday = birthday,
        photoUri = photoUri,
        isFavorite = isFavorite,
        updatedAt = System.currentTimeMillis()
    )

    fun ContactEntity.toDomain(): Contact = Contact(
        id = id,
        name = name,
        phoneNumber = phoneNumber.orEmpty(),
        email = email,
        address = address,
        birthday = birthday,
        photoUri = photoUri,
        isFavorite = isFavorite
    )
}