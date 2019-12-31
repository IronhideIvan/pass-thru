package com.passthru.android.util

import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.Button
import java.io.Serializable

class InputHelper {
    fun convert(e: KeyEvent, immutableReport: InputReport): InputReport{

        val report = InputReport().copy(immutableReport)
        val buttonReport = report.buttonReport

        var buttonId = 0
        var keyHandled = true

        when(e.keyCode){
            KeyEvent.KEYCODE_DPAD_UP -> buttonId = 1
            KeyEvent.KEYCODE_DPAD_DOWN -> buttonId = 2
            KeyEvent.KEYCODE_DPAD_LEFT -> buttonId = 4
            KeyEvent.KEYCODE_DPAD_RIGHT -> buttonId = 8
            KeyEvent.KEYCODE_BUTTON_THUMBL -> buttonId = 16
            KeyEvent.KEYCODE_BUTTON_THUMBR -> buttonId = 32
            KeyEvent.KEYCODE_BUTTON_X -> buttonId = 64
            KeyEvent.KEYCODE_BUTTON_Y -> buttonId = 128
            KeyEvent.KEYCODE_BUTTON_B -> buttonId = 256
            KeyEvent.KEYCODE_BUTTON_A -> buttonId = 512
            KeyEvent.KEYCODE_BUTTON_R1 -> buttonId = 1024
            KeyEvent.KEYCODE_BUTTON_L1 -> buttonId = 2048
            KeyEvent.KEYCODE_BUTTON_R2 -> buttonId = 4096
            KeyEvent.KEYCODE_BUTTON_L2 -> buttonId = 8192
            KeyEvent.KEYCODE_BUTTON_START -> buttonId = 16384
            KeyEvent.KEYCODE_BUTTON_SELECT, KeyEvent.KEYCODE_MENU -> buttonId = 32768
            KeyEvent.KEYCODE_BACK -> buttonId = 65536
            KeyEvent.KEYCODE_BUTTON_C -> buttonId = 131072
            KeyEvent.KEYCODE_BUTTON_Z -> buttonId = 262144
            KeyEvent.KEYCODE_BUTTON_MODE -> buttonId = 524288
            else -> {
                Log.d("UNHANDLED KEY EVENT", e.keyCode.toString())
                keyHandled = false
            }
        }

        if(keyHandled){
            if(e.action == KeyEvent.ACTION_UP && buttonReport.buttons.and(buttonId) == buttonId){
                buttonReport.buttons -= buttonId
            }
            else if(e.action == KeyEvent.ACTION_DOWN){
                buttonReport.buttons = buttonReport.buttons.or(buttonId)
            }
        }

        return report
    }

    fun convert(e: MotionEvent, immutableReport: InputReport, historyPos: Int): InputReport{

        val fullReport= InputReport().copy(immutableReport)
        val axisReport = fullReport.axisReport
        val buttonReport = fullReport.buttonReport
        var buttonId = 0

        // Check if d-pad is being pressed
        var axisX = getAxisVal(e, MotionEvent.AXIS_HAT_X, historyPos)
        buttonId += when {
            axisX.compareTo(-1.0f) == 0 -> 4 // Left
            axisX.compareTo(1.0f) == 0 -> 8 // Right
            else -> 0
        }
        // Always remove both since a rapid transition between these
        // means we may have missed deactivating one of them
        handleAxisButton(4, buttonReport, false)
        handleAxisButton(8, buttonReport, false)
        if(buttonId > 0){
            handleAxisButton(buttonId, buttonReport, true)
        }

        buttonId = 0
        var axisY = getAxisVal(e, MotionEvent.AXIS_HAT_Y, historyPos)
        buttonId += when {
            axisY.compareTo(-1.0f) == 0 -> 1 // Up
            axisY.compareTo(1.0f) == 0 -> 2 // Down
            else -> 0
        }
        // Always remove both since a rapid transition between these
        // means we may have missed deactivating one of them
            handleAxisButton(1, buttonReport, false)
            handleAxisButton(2, buttonReport, false)
        if(buttonId > 0){
            handleAxisButton(buttonId, buttonReport, true)
        }

        axisReport.axis1.x = getAxisVal(e, MotionEvent.AXIS_X, historyPos)
        axisReport.axis1.y = getAxisVal(e, MotionEvent.AXIS_Y, historyPos)
        axisReport.axis1.z = getAxisVal(e, MotionEvent.AXIS_Z, historyPos)

        axisReport.axis2.x = getAxisVal(e, MotionEvent.AXIS_RX, historyPos)
        axisReport.axis2.y = getAxisVal(e, MotionEvent.AXIS_RY, historyPos)
        axisReport.axis2.z = getAxisVal(e, MotionEvent.AXIS_RZ, historyPos)

        axisReport.throttle = getAxisVal(e, MotionEvent.AXIS_THROTTLE, historyPos)
        axisReport.brake = getAxisVal(e, MotionEvent.AXIS_BRAKE, historyPos)

        return fullReport
    }

    private fun getAxisVal(e: MotionEvent, eventKey: Int, historyPos: Int): Float {
        if (historyPos >= 0){
            return e.getHistoricalAxisValue(eventKey, historyPos)
        }
        else{
            return e.getAxisValue(eventKey)
        }
    }

    private fun handleAxisButton(buttonId: Int, report: ButtonReport, add: Boolean) {
        if(!add && report.buttons.and(buttonId) == buttonId){
            report.buttons -= buttonId
        }
        else if(add){
            report.buttons = report.buttons.or(buttonId)
        }
    }
}

class InputReport : Serializable{
    var messageTimestamp: Long = 0
    val buttonReport: ButtonReport = ButtonReport()
    val axisReport: AxisReport = AxisReport()

    fun copy(other: InputReport): InputReport{
        buttonReport.copy(other.buttonReport)
        axisReport.copy(other.axisReport)
        return this
    }
}

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

class AxisReport : Serializable {
    val axis1: Axis = Axis()
    val axis2: Axis = Axis()
    var throttle: Float = 0.0f
    var brake: Float = 0.0f

    fun copy(other: AxisReport): AxisReport{
        axis1.copy(other.axis1)
        axis2.copy(other.axis2)
        this.throttle = other.throttle
        this.brake = other.brake
        return this
    }
}

class Axis: Serializable{
    var x: Float = 0.0f
    var y: Float = 0.0f
    var z: Float = 0.0f

    fun copy(other: Axis){
        this.x = other.x
        this.y = other.y
        this.z = other.z
    }
}
