package com.passthru.android.util

import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import com.google.gson.Gson
import java.io.Serializable
import java.security.Key
import kotlin.math.log

class InputHelper {
    fun convert(e: KeyEvent, report: InputReport): InputReport{
        val pte = InputReport()

        var buttonId: Int = 0
        var keyHandled: Boolean = true

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
            if(e.action === KeyEvent.ACTION_UP && report.buttons.or(buttonId) === buttonId){
                report.buttons -= buttonId;
            }
            else if(e.action === KeyEvent.ACTION_DOWN && report.buttons.and(buttonId) === 0){
                report.buttons += buttonId;
            }
        }

        return report
    }

    fun convert(e: MotionEvent, report: InputReport): InputReport{
        val pte = InputReport()
        return report
    }
}

class InputReport : Serializable{
    var buttons: Int = 0
    var axis1: Axis = Axis()
    var axis2: Axis = Axis()
    var throttle: Int = 0
    var brake: Int = 0
}
class Axis: Serializable{
    var x: Float = 0.0f
    var y: Float = 0.0f
    var z: Float = 0.0f
}
