package com.morkath.contacts.ui.search

import androidx.lifecycle.ViewModel
import com.morkath.contacts.domain.model.Contact
import com.morkath.contacts.ui.contact.sampleContacts
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SearchViewModel : ViewModel() {
    private val _results = MutableStateFlow<List<Contact>>(sampleContacts)
    val results: StateFlow<List<Contact>> = _results.asStateFlow()

    fun search(query: String) {
        _results.value = if (query.isEmpty()) {
            sampleContacts
        } else {
            sampleContacts.filter {
                it.name.contains(query, ignoreCase = true)
                || it.phoneNumber.contains(query, ignoreCase = true)
            }
        }
    }
}