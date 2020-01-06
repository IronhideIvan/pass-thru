package com.passthru.android.util

import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import com.passthru.android.util.models.MouseReport

class MouseHelper {
    fun convert(e: KeyEvent, immutableReport: MouseReport): MouseReport {

        val report = immutableReport.clone()
        val buttonReport = report.buttons

        var buttonId = 0
        var keyHandled = true

        when(e.keyCode){
            KeyEvent.KEYCODE_BUTTON_B -> buttonId = 256
            KeyEvent.KEYCODE_BUTTON_C -> buttonId = 131072
            else -> {
                keyHandled = false
            }
        }

        report.click = keyHandled
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

    fun convert(e: MotionEvent, immutableReport: MouseReport, historyPos: Int): MouseReport{

        val fullReport= immutableReport.clone()
        fullReport.click = false
        val axis = fullReport.velocity

        axis.x = getAxisVal(e, MotionEvent.AXIS_X, historyPos)
        axis.y = getAxisVal(e, MotionEvent.AXIS_Y, historyPos)

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
}