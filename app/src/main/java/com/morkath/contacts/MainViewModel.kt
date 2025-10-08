package com.morkath.contacts

import androidx.lifecycle.ViewModel
import android.util.Log
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    // Dùng quản lý theme, user session, v.v.
    init {
        Log.d("MainViewModel", "init")
    }
}