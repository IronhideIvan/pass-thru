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

    fun copy(other: MouseReport): MouseReport{
        buttons.copy(other.buttons)
        velocity.copy(other.velocity)
        click = other.click
        return this
    }
}