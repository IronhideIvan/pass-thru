package com.passthru.android.util.models

class MouseReport {
    var messageTimestamp: Long = 0
    var buttons: ButtonReport = ButtonReport()
    var velocity: Axis = Axis()
    var click: Boolean = false

    fun areEqual(other: MouseReport): Boolean{
        return buttons.areEqual(other.buttons)
                && velocity.areEqual(other.velocity)
                && click == other.click
    }

    fun clone(): MouseReport {
        val temp = MouseReport()
        temp.copy(this)
        return temp
    }

    fun copy(other: MouseReport) {
        buttons.copy(other.buttons)
        velocity.copy(other.velocity)
        click = other.click
    }
}