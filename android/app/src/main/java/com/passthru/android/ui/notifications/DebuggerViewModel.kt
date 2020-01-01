package com.passthru.android.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DebuggerViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Test the app"
    }

    private val _xAxis = MutableLiveData<String>().apply {
        value = "N/A"
    }

    private val _yAxis = MutableLiveData<String>().apply {
        value = "N/A"
    }

    private val _zAxis = MutableLiveData<String>().apply {
        value = "N/A"
    }

    private val _rXAxis = MutableLiveData<String>().apply {
        value = "N/A"
    }

    private val _rYAxis = MutableLiveData<String>().apply {
        value = "N/A"
    }

    private val _rZAxis = MutableLiveData<String>().apply {
        value = "N/A"
    }

    private val _throttle = MutableLiveData<String>().apply {
        value = "N/A"
    }

    private val _brake = MutableLiveData<String>().apply {
        value = "N/A"
    }

    private val _buttons = MutableLiveData<String>().apply {
        value = "N/A"
    }


    val headerText: MutableLiveData<String> = _text
    val xAxis: MutableLiveData<String> = _xAxis
    val yAxis: MutableLiveData<String> = _yAxis
    val zAxis: MutableLiveData<String> = _zAxis
    val rXAxis: MutableLiveData<String> = _rXAxis
    val rYAxis: MutableLiveData<String> = _rYAxis
    val rZAxis: MutableLiveData<String> = _rZAxis
    val throttle: MutableLiveData<String> = _throttle
    val brake: MutableLiveData<String> = _brake
    val buttons: MutableLiveData<String> = _buttons


}