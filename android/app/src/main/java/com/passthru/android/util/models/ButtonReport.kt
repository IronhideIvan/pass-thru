package com.passthru.android.util.models

import java.io.Serializable

class ButtonReport : Serializable {
    var buttons: Int = 0

    fun areEqual(other: ButtonReport): Boolean{
        return this.buttons == other.buttons
    }

    fun copy(other: ButtonReport): ButtonReport{
        this.buttons = other.buttons
        return this
    }
}