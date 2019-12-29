package com.passthru.android.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DebuggerViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Test the app"
    }
    val text: LiveData<String> = _text
}