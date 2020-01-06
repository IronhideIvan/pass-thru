package com.passthru.android.util.models

import java.io.Serializable

class ButtonReport : Serializable {
    var buttons: Int = 0

    fun areEqual(other: ButtonReport): Boolean{
        return this.buttons == other.buttons
    }

    fun clone(): ButtonReport {
        val temp = ButtonReport()
        temp.copy(this)
        return temp
    }

    fun copy(other: ButtonReport) {
        this.buttons = other.buttons
    }
}