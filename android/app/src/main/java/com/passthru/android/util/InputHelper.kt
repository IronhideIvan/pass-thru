package com.passthru.android.util

import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import java.io.Serializable

class InputHelper {
    fun convert(e: KeyEvent, report: InputReport): InputReport{
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
                Log.i("UNHANDLED KEY EVENT", e.keyCode.toString())
                keyHandled = false
            }
        }

        if(keyHandled){
            if(e.action == KeyEvent.ACTION_UP && report.buttons.and(buttonId) == buttonId){
                report.buttons -= buttonId
            }
            else if(e.action == KeyEvent.ACTION_DOWN){
                report.buttons = report.buttons.or(buttonId)
            }
        }

        return report
    }

    fun convert(e: MotionEvent, report: InputReport): InputReport{

        var buttonId = 0

        // Check if d-pad is being pressed
        var axis = e.getAxisValue(MotionEvent.AXIS_HAT_X)
        buttonId += when {
            axis.compareTo(-1.0f) == 0 -> 4 // Left
            axis.compareTo(1.0f) == 0 -> 8 // Right
            else -> 0
        }
        if(buttonId == 0){
            handleAxisButton(4, report, false)
            handleAxisButton(8, report, false)
        }
        else{
            handleAxisButton(buttonId, report, true)
        }

        buttonId = 0
        axis = e.getAxisValue(MotionEvent.AXIS_HAT_Y)
        buttonId += when {
            axis.compareTo(-1.0f) == 0 -> 1 // Up
            axis.compareTo(1.0f) == 0 -> 2 // Down
            else -> 0
        }
        if(buttonId == 0){
            handleAxisButton(1, report, false)
            handleAxisButton(2, report, false)
        }
        else{
            handleAxisButton(buttonId, report, true)
        }

        axis = e.getAxisValue(MotionEvent.AXIS_THROTTLE)

        report.axis1.x = e.getAxisValue(MotionEvent.AXIS_X)
        report.axis1.y = e.getAxisValue(MotionEvent.AXIS_Y)
        report.axis1.z = e.getAxisValue(MotionEvent.AXIS_Z)

        report.axis2.x = e.getAxisValue(MotionEvent.AXIS_RX)
        report.axis2.y = e.getAxisValue(MotionEvent.AXIS_RY)
        report.axis2.z = e.getAxisValue(MotionEvent.AXIS_RZ)

        report.throttle = e.getAxisValue(MotionEvent.AXIS_THROTTLE)
        report.brake = e.getAxisValue(MotionEvent.AXIS_BRAKE)

        return report
    }

    private fun handleAxisButton(buttonId: Int, report: InputReport, add: Boolean) {
        if(!add && report.buttons.and(buttonId) == buttonId){
            report.buttons -= buttonId
        }
        else if(add){
            report.buttons = report.buttons.or(buttonId)
        }
    }
}

class InputReport : Serializable{
    var buttons: Int = 0
    var axis1: Axis = Axis()
    var axis2: Axis = Axis()
    var throttle: Float = 0.0f
    var brake: Float = 0.0f
}
class Axis: Serializable{
    var x: Float = 0.0f
    var y: Float = 0.0f
    var z: Float = 0.0f
}
