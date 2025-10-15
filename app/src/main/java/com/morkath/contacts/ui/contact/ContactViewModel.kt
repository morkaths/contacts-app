package com.morkath.contacts.ui.contact

import android.app.Application
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.morkath.contacts.domain.model.Contact
import com.morkath.contacts.domain.usecase.contact.*
import com.morkath.contacts.domain.usecase.device.*
import com.morkath.contacts.util.ImageUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ContactUiState {
    object Loading : ContactUiState()
    data class Success(val contacts: List<Contact>) : ContactUiState()
    data class Error(val message: String) : ContactUiState()
}

data class ContactFormUiState(
    val nameError: String? = null,
    val phoneError: String? = null,
    val emailError: String? = null,
    val isValid: Boolean = true
)

@HiltViewModel
class ContactViewModel @Inject constructor(
    private val app: Application,
    private val getContactsUseCase: GetContactsUseCase,
    private val getContactByIdUseCase: GetContactByIdUseCase,
    private val searchContactsUseCase: SearchContactsUseCase,
    private val insertContactUseCase: InsertContactUseCase,
    private val updateContactUseCase: UpdateContactUseCase,
    private val deleteContactUseCase: DeleteContactUseCase,
    private val validatorNameUseCase: ValidatorNameUseCase,
    private val validatorPhoneUseCase: ValidatorPhoneUseCase,
    private val validatorEmailUseCase: ValidatorEmailUseCase,
    private val getDeviceContactsUseCase: GetDeviceContactsUseCase,
    private val addContactToDeviceUseCase: AddContactToDeviceUseCase
) : ViewModel() {
    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    private val _contact = MutableStateFlow<Contact?>(null)
    private val _uiState = MutableStateFlow<ContactUiState>(ContactUiState.Loading)
    private val _formUiState = MutableStateFlow(ContactFormUiState())
    private val _uiEvent = Channel<String>()

    val contacts: StateFlow<List<Contact>> = _contacts.asStateFlow()
    val contact: StateFlow<Contact?> = _contact.asStateFlow()
    val uiState: StateFlow<ContactUiState> = _uiState.asStateFlow()
    val formUiState: StateFlow<ContactFormUiState> = _formUiState.asStateFlow()
    val uiEvent: Flow<String> = _uiEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            loadContacts()
        }
    }

    private fun loadContacts() {
        viewModelScope.launch {
            _uiState.value = ContactUiState.Loading
            try {
                getContactsUseCase().collect { contactList ->
                    _contacts.value = contactList
                    _uiState.value = ContactUiState.Success(contactList)
                }
            } catch (e: Exception) {
                _uiState.value = ContactUiState.Error("Error loading contacts: ${e.message}")
                _uiEvent.send("Error loading contacts: ${e.message}")
            }
        }
    }

    fun importDeviceContacts() {
        viewModelScope.launch {
            try {
                val deviceContacts = getDeviceContactsUseCase()
                deviceContacts.forEach { contact ->
                    insertContactUseCase(contact)
                }
                loadContacts()
                _uiEvent.send("Imported ${deviceContacts.size} contacts from device.")
            } catch (e: Exception) {
                _uiEvent.send("Error importing device contacts: ${e.message}")
            }
        }
    }

//    fun syncDeviceContacts(syncUseCase: suspend () -> Pair<Int, Int>) {
//        viewModelScope.launch {
//            try {
//                val (added, updated) = syncUseCase()
//                _uiEvent.send("Synced: $added added, $updated updated.")
//                loadContacts()
//            } catch (e: Exception) {
//                _uiEvent.send("Error syncing contacts: ${e.message}")
//            }
//        }
//    }

    fun loadContactById(contactId: Long) {
        viewModelScope.launch {
            getContactByIdUseCase(contactId).collect { contact ->
                _contact.value = contact
            }
        }
    }

    fun create(contact: Contact) {
        viewModelScope.launch {
            validate(contact)
            if (!_formUiState.value.isValid) {
                _uiEvent.send("Vui lòng sửa các lỗi trong form.")
                return@launch
            }

            try {
                var contactToSave = contact
                if (!contact.photoUri.isNullOrBlank() && contact.photoUri.startsWith("content://")) {
                    val copiedPath = ImageUtils.copyImageToInternalStorage(app, contact.photoUri.toUri())
                    if (copiedPath != null) {
                        contactToSave = contact.copy(photoUri = copiedPath)
                    } else {
                        _uiEvent.send("Failed to copy image to internal storage.")
                        contactToSave = contact.copy(photoUri = null)
                    }
                }
                insertContactUseCase(contactToSave)
                _uiEvent.send("Contact created successfully!")
            } catch (e: Exception) {
                _uiEvent.send("Error creating contact: ${e.message}")
            }
        }
    }

    fun addDevice(contact: Contact) {
        viewModelScope.launch {
            validate(contact)
            if (!_formUiState.value.isValid) {
                _uiEvent.send("Vui lòng sửa các lỗi trong form.")
                return@launch
            }

            try {
                var contactToSave = contact
                if (!contact.photoUri.isNullOrBlank() && contact.photoUri.startsWith("content://")) {
                    val copiedPath = ImageUtils.copyImageToInternalStorage(app, contact.photoUri.toUri())
                    if (copiedPath != null) {
                        contactToSave = contact.copy(photoUri = copiedPath)
                    } else {
                        _uiEvent.send("Lỗi khi sao chép ảnh.")
                        contactToSave = contact.copy(photoUri = null)
                    }
                }
                val appContact = insertContactUseCase(contactToSave)
                val deviceContactId = addContactToDeviceUseCase(contact)
                updateContactUseCase(appContact.copy(deviceId = deviceContactId))
                val success = addContactToDeviceUseCase(contact)

                if (success) {
                    _uiEvent.send("Contact added to device successfully!")
                } else {
                    _uiEvent.send("Failed to add contact to device.")
                }
            } catch (e: Exception) {
                _uiEvent.send("Error adding contact to device: ${e.message}")
            }
        }
    }

    fun update(contact: Contact) {
        viewModelScope.launch {
            validate(contact)
            if (!_formUiState.value.isValid) {
                _uiEvent.send("Vui lòng sửa các lỗi trong form.")
                return@launch
            }

            try {
                var contactToSave = contact
                val oldContact = getContactByIdUseCase(contact.id).first()
                val oldPhotoUri = oldContact?.photoUri

                if (!contact.photoUri.isNullOrBlank() && contact.photoUri.startsWith("content://")) {
                    val copiedPath = ImageUtils.copyImageToInternalStorage(app, contact.photoUri.toUri())
                    if (copiedPath != null) {
                        contactToSave = contact.copy(photoUri = copiedPath)
                        ImageUtils.deleteImageFromInternalStorage(oldPhotoUri)
                    } else {
                        _uiEvent.send("Failed to copy image to internal storage.")
                        contactToSave = contact.copy(photoUri = null)
                    }
                }
                updateContactUseCase(contactToSave)
                _uiEvent.send("Contact updated successfully!")
            } catch (e: Exception) {
                _uiEvent.send("Error updating contact: ${e.message}")
            }
        }
    }

    fun delete(contactId: Long) {
        viewModelScope.launch {
            try {
                val contactToDelete = getContactByIdUseCase(contactId).first()
                ImageUtils.deleteImageFromInternalStorage(contactToDelete?.photoUri)
                deleteContactUseCase(contactId)
                _uiEvent.send("Contact deleted successfully!")
            } catch (e: Exception) {
                _uiEvent.send("Error deleting contact: ${e.message}")
            }
        }
    }

    fun search(query: String) {
        viewModelScope.launch {
            _uiState.value = ContactUiState.Loading
            try {
                searchContactsUseCase(query).collect { result ->
                    _contacts.value = result
                    _uiState.value = ContactUiState.Success(result)
                }
            } catch (e: Exception) {
                _uiState.value = ContactUiState.Error("Error searching contacts: ${e.message}")
            }
        }
    }

    fun validate(contact: Contact) {
        val nameResult = validatorNameUseCase(contact.name)
        val phoneResult = validatorPhoneUseCase(contact.phoneNumber)
        val emailResult = if (!contact.email.isNullOrBlank()) {
            validatorEmailUseCase(contact.email)
        } else null

        val isValid = nameResult.isValid && phoneResult.isValid && (emailResult?.isValid ?: true)


        _formUiState.value = ContactFormUiState(
            nameError = nameResult.errorMessage,
            phoneError = phoneResult.errorMessage,
            emailError = emailResult?.errorMessage,
            isValid = isValid
        )
    }

}