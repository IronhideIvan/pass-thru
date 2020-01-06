package com.passthru.android.util

import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import com.google.gson.Gson
import com.passthru.android.util.models.Axis
import com.passthru.android.util.models.ButtonReport
import com.passthru.android.util.models.MouseReport
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

object MouseDispatcher {
    private val mouseHelper: MouseHelper = MouseHelper()
    private var mouseReport: MouseReport = MouseReport()
    private val buttonQueue = LinkedList<ButtonReport>()
    private var mouseVelocity = Axis()
    private var clearInputs: AtomicBoolean = AtomicBoolean(false)

    fun dispatchMotionEvent(ev: MotionEvent): MouseReport? {
        var mr: MouseReport = mouseHelper.convert(ev, mouseReport, -1)

        mouseReport.copy(mr)

        if(!UdpHelper.isConnected()){
            return null
        }

        var mrLoop = mr.clone()
        (0 until ev.historySize).forEach {i ->
            mrLoop = mouseHelper.convert(ev, mrLoop, i)
            mouseVelocity = mrLoop.velocity.clone()
        }

        mouseVelocity = mr.velocity.clone()

        return mr
    }

    fun dispatchKeyEvent(event: KeyEvent): MouseReport? {
        if(event.repeatCount > 0){
            return null
        }

        val mr = mouseHelper.convert(event, mouseReport)
        mouseReport.buttons.copy(mr.buttons)

        if(!UdpHelper.isConnected()){
            return null
        }

        if(mr.click){
            buttonQueue.addLast(mr.buttons)
        }

        return mr
    }

    fun clearInputs(){
        clearInputs.set(true)
    }

    private var lastSentReport = MouseReport()
    @Synchronized fun checkAndSendInputMessage() {
        val debugMode = Globals.debugMode.get()
        var currentVelocity = mouseVelocity.clone()
        if ((clearInputs.get() || buttonQueue.size > 0 || !currentVelocity.areEqual(lastSentReport.velocity)) && UdpHelper.isConnected()) {
            val mrToSend = MouseReport()

            if(debugMode){
                Log.d("Queue", "Size: ${buttonQueue.size}")
            }

            if (clearInputs.get()){
                buttonQueue.clear()
                clearInputs.set(false)
            }
            else {
                if(buttonQueue.size > 0){
                    mrToSend.buttons.copy(buttonQueue.removeFirst())
                    mrToSend.click = true
                }
                else{
                    mrToSend.buttons.copy(lastSentReport.buttons)
                }

                mrToSend.velocity.copy(mouseVelocity)
            }

            mrToSend.messageTimestamp = System.currentTimeMillis()
            lastSentReport.copy(mrToSend)
            val json = toJson(mrToSend)

            if(debugMode){
                Log.d("Queue", "Packet: $json")
            }

            UdpHelper.sendUdp("M$json")
        }
    }

    private fun toJson(pte: MouseReport): String{
        val gson = Gson()
        return gson.toJson(pte)
    }
}