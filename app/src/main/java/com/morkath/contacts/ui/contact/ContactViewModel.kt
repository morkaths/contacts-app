package com.morkath.contacts.ui.contact

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.morkath.contacts.domain.model.Contact
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

val sampleContacts = mutableStateListOf(
    Contact(
        id = 1,
        name = "Nguyen Van A",
        phoneNumber = "0768457358",
        email = "a@example.com",
        address = "123 Main St",
        photoUri = null,
        isFavorite = false
    ),
    Contact(
        id = 2,
        name = "Le Thi B",
        phoneNumber = "0987654321",
        email = "b@example.com",
        address = "456 Second St",
        photoUri = null,
        isFavorite = true
    ),
    Contact(
        id = 3,
        name = "Tran Van C",
        phoneNumber = "0121987654",
        email = null,
        address = null,
        photoUri = null,
        isFavorite = false
    )
)

data class ContactUiState(
    val nameError: String? = null,
    val phoneError: String? = null,
    val emailError: String? = null,
    val isValid: Boolean = true
)

class ContactViewModel : ViewModel() {
    private val _contacts = MutableStateFlow<List<Contact>>(sampleContacts)
    val contacts: StateFlow<List<Contact>> = _contacts.asStateFlow()
    private val _searchResults = MutableStateFlow<List<Contact>>(emptyList())
    val searchResults: StateFlow<List<Contact>> = _searchResults.asStateFlow()

    fun getById(id: Long): Flow<Contact?> {
        return contacts.map { list -> list.find { it.id == id } }
    }

    fun create(contact: Contact) {
        val newId = (_contacts.value.maxOfOrNull { it.id } ?: 0) + 1
        _contacts.value = _contacts.value + contact.copy(id = newId)
    }

    fun update(contact: Contact) {
        _contacts.value = _contacts.value.map { if (it.id == contact.id) contact else it }
    }

    fun delete(contactId: Long) {
        _contacts.value = _contacts.value.filter { it.id != contactId }
    }

    fun search(query: String) {
        _searchResults.value = _contacts.value.filter {
            it.name.contains(query, ignoreCase = true) ||
            it.phoneNumber.contains(query) ||
            it.email?.contains(query, ignoreCase = true) == true
        }
    }

    fun validate(contact: Contact): ContactUiState {
        val nameRegex = Regex("^[a-zA-Z\\s]+$")
        val phoneRegex = Regex("^(09|03|07|08|05)\\d{8}$")
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")

        val nameError = when {
            contact.name.isBlank() -> "Tên không được để trống"
            !nameRegex.matches(contact.name) -> "Tên chỉ được chứa chữ cái và khoảng trắng"
            else -> null
        }

        val phoneError = when {
            contact.phoneNumber.isBlank() -> "Số điện thoại không được để trống"
            !phoneRegex.matches(contact.phoneNumber) -> "Số điện thoại chỉ được chứa chữ số"
            else -> null
        }

        val emailError = if (!contact.email.isNullOrBlank() && !emailRegex.matches(contact.email)) {
            "Email không hợp lệ"
        } else null

        val isValid = nameError == null && phoneError == null && emailError == null

        return ContactUiState(
            nameError = nameError,
            phoneError = phoneError,
            emailError = emailError,
            isValid = isValid
        )
    }

}