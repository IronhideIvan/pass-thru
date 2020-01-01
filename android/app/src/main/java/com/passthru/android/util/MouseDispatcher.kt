package com.passthru.android.util

import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import com.google.gson.Gson
import com.passthru.android.util.models.MouseReport
import java.util.*

object MouseDispatcher {
    private val mouseHelper: MouseHelper = MouseHelper()
    private var mouseReport: MouseReport = MouseReport()
    private val queue = LinkedList<MouseReport>()
    private var clearInputs: Boolean = false

    fun dispatchMotionEvent(ev: MotionEvent): MouseReport? {
        var mrOrig: MouseReport = MouseReport().copy(mouseReport)
        var mr: MouseReport = MouseReport().copy(mouseReport)
        var mrLLast = mouseHelper.convert(ev, mouseReport, -1)
        mouseReport = mouseReport.copy(mrLLast)

        if(!UdpHelper.isConnected()){
            return null
        }

        (0 until ev.historySize).forEach {i ->
            mr = mouseHelper.convert(ev, mr, i)
            if(!mr.velocity.areEqual(mrOrig.velocity)){
                queue.addLast(mr)
            }
        }

        if(!mrOrig.velocity.areEqual(mrLLast.velocity)){
            queue.addLast(mrLLast)
        }

        return mrLLast
    }

    fun dispatchKeyEvent(event: KeyEvent): MouseReport? {
        if(event.repeatCount > 0){
            return null
        }

        val mr = mouseHelper.convert(event, mouseReport)
        mouseReport = mouseReport.copy(mr)

        if(!UdpHelper.isConnected()){
            return null
        }

        if(mr.click){
            queue.addLast(mr)
        }

        return mr
    }

    fun clearInputs(){
        clearInputs = true
    }

    @Synchronized fun checkAndSendInputMessage() {
        val queueSize = queue.size
        val debugMode = Globals.debugMode.get()
        if ((clearInputs || queueSize > 0) && UdpHelper.isConnected()) {
            // Events might be appending to the queue while we are reading it, so we want to
            // only work with what we have at this very moment
            val mrToSend = MouseReport()

            if(debugMode){
                Log.d("Queue", "Size: " + queueSize.toString())
            }

            if(clearInputs){
                queue.clear()
                clearInputs = false
            }
            else if (queueSize == 1) {
                mrToSend.copy(queue.removeFirst())
            } else {
                val lastReport = queue[queueSize - 1]
                var firstReport = queue.removeFirst()

                // We always send the first button and last axis reports
                mrToSend.velocity.copy((lastReport.velocity))
                mrToSend.buttons.copy(firstReport.buttons)
                mrToSend.click = firstReport.click

                // We only care about the last motion inputs since a half millimeter of motion
                // is negligible and would only serve to overload the server with too many events
                // to process
                mrToSend.velocity.copy(lastReport.velocity)

                var index = 1
                while (index < queueSize) {
                    var currentReport = queue[0]
                    if(!currentReport.click){
                        queue.removeFirst()
                    }

                    ++index
                }
            }

            // Avoid sending the same thing multiple times to reduce bandwidth
            mrToSend.messageTimestamp = System.currentTimeMillis()
            val json = toJson(mrToSend)

            if(debugMode){
                Log.d("Queue", "Packet: " + json)
            }

            UdpHelper.sendUdp("M" + json)
        }
    }

    private fun toJson(pte: MouseReport): String{
        val gson = Gson()
        return gson.toJson(pte)
    }
}