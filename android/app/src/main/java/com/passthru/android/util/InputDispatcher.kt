package com.passthru.android.util

import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import com.google.gson.Gson
import com.passthru.android.util.models.AxisReport
import com.passthru.android.util.models.ButtonReport
import com.passthru.android.util.models.InputReport
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

object InputDispatcher {
    private val inputHelper: InputHelper = InputHelper()
    private var inputReport: InputReport = InputReport()
    private var clearInputs: AtomicBoolean = AtomicBoolean(false)
    private var inputAxis = AxisReport()
    private val buttonQueue = LinkedList<ButtonReport>()

    fun dispatchMotionEvent(ev: MotionEvent): InputReport? {
        var btnReport = inputHelper.convertButtonsFromMotionEvent(ev, inputReport, -1)
        var axisReport = inputHelper.convert(ev, inputReport.axisReport, -1)

        val inputReportLast = InputReport()
        inputReportLast.axisReport.copy(axisReport)
        inputReportLast.buttonReport.copy(btnReport)
        debugInputReport(inputReportLast)

        if(!UdpHelper.isConnected()){
            return null
        }

        var inputReportLoop = inputReport.clone()
        var loopHit = false
        (0 until ev.historySize).forEach {i ->
            loopHit = true
            inputReportLoop.axisReport.copy(inputHelper.convert(ev, inputReportLoop.axisReport, i))

            val previousButtonReport = inputReportLoop.buttonReport.clone()
            inputReportLoop.buttonReport.copy(inputHelper.convertButtonsFromMotionEvent(ev, inputReportLoop, i))

            inputAxis = inputReportLoop.axisReport.clone()
            if(!inputReportLoop.buttonReport.areEqual(previousButtonReport)){
                buttonQueue.addLast(inputReportLoop.buttonReport.clone())
            }
        }

        inputAxis = inputReportLast.axisReport.clone()

        if((loopHit && !inputReportLast.buttonReport.areEqual(inputReportLoop.buttonReport))
            || (!loopHit && !inputReport.buttonReport.areEqual(inputReportLast.buttonReport))){
            buttonQueue.addLast(inputReportLast.buttonReport.clone())
        }

        inputReport = inputReportLast

        return inputReportLast
    }

    fun dispatchKeyEvent(event: KeyEvent): InputReport? {
        if(event.repeatCount > 0){
            return null
        }

        val tempInputReport = inputReport.clone()
        val btnReport = inputHelper.convert(event, tempInputReport.buttonReport)
        tempInputReport.buttonReport.copy(btnReport)

        debugInputReport(tempInputReport)

        if(!UdpHelper.isConnected()){
            return null
        }

        if(!tempInputReport.buttonReport.areEqual(inputReport.buttonReport)){
            buttonQueue.addLast(btnReport.clone())
        }

        inputReport = tempInputReport.clone()
        return tempInputReport
    }

    fun clearInputs(){
        clearInputs.set(true)
    }

    private var lastSentReport = InputReport()
    @Synchronized fun checkAndSendInputMessage() {
        val debugMode = Globals.debugMode.get()

        val currentAxis = inputAxis.clone()
        if ((clearInputs.get() || buttonQueue.size > 0 || !currentAxis.areEqual(lastSentReport.axisReport)) && UdpHelper.isConnected()) {
            // Events might be appending to the queue while we are reading it, so we want to
            // only work with what we have at this very moment
            val qteToSend = InputReport()

            if(debugMode){
                Log.d("Queue", "Size: ${buttonQueue.size}")
            }

            if(clearInputs.get()){
                buttonQueue.clear()
                clearInputs.set(false)
            }
            else{
                if(buttonQueue.size > 0){
                    qteToSend.buttonReport.copy(buttonQueue.removeFirst())
                }
                else{
                    qteToSend.buttonReport.copy(lastSentReport.buttonReport)
                }

                qteToSend.axisReport.copy(currentAxis)
            }

            // Avoid sending the same thing multiple times to reduce bandwidth
            qteToSend.messageTimestamp = System.currentTimeMillis()
            lastSentReport.copy(qteToSend)
            val json = toJson(qteToSend)

            if(debugMode){
                Log.d("Queue", "Packet: " + json)
            }

            UdpHelper.sendUdp("C$json")
        }
    }

    private fun debugInputReport(report: InputReport){
        val frag = Globals.debuggerFragment.get()
        if(Globals.debugMode.get() && frag != null){
            frag.populateDebug(report)
        }
    }

    private fun toJson(pte: InputReport): String{
        val gson = Gson()
        return gson.toJson(pte)
    }
}