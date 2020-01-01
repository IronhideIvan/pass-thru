package com.passthru.android.util

import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import com.google.gson.Gson
import com.passthru.android.ui.notifications.DebuggerFragment
import com.passthru.android.util.models.InputReport
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

object ActionDispatcher {

    private var inputMode: String = "Input" // Input || Mouse

    fun dispatchMotionEvent(ev: MotionEvent): Boolean {
        var handled: Boolean

        if(inputMode == "Mouse"){
            handled = MouseDispatcher.dispatchMotionEvent(ev) != null
        }
        else{
            handled = InputDispatcher.dispatchMotionEvent(ev) != null
        }

        return handled
    }

    fun dispatchKeyEvent(event: KeyEvent): Boolean {
        var handled: Boolean

        if(inputMode == "Mouse"){
            handled = MouseDispatcher.dispatchKeyEvent(event) != null
        }
        else{
            handled = InputDispatcher.dispatchKeyEvent(event) != null
        }

        return handled
    }

    fun clearInputs(){
        MouseDispatcher.clearInputs()
        InputDispatcher.clearInputs()
    }

    fun setMode(mode: String){
        inputMode = mode
    }

    fun getMode(): String {
        return inputMode
    }

    @Synchronized fun checkAndSendInputMessage() {
        if(inputMode == "Mouse"){
            MouseDispatcher.checkAndSendInputMessage()
        }
        else{
            InputDispatcher.checkAndSendInputMessage()
        }
    }
}