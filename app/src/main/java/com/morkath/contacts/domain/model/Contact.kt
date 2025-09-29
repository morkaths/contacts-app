package com.morkath.contacts.domain.model

data class Contact(
    val id: Long,
    val name: String,
    val phoneNumber: String,
    val email: String? = null,
    val address: String? = null,
    val pictureUri: String? = null,
    val isFavorite: Boolean? = false
)