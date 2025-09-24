package com.morkath.contacts

import androidx.lifecycle.ViewModel
import android.util.Log
import com.morkath.contacts.domain.repository.ContactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val contactRepository: ContactRepository
) : ViewModel() {
    init {
        Log.d("MainViewModel", "init $contactRepository")
    }
}