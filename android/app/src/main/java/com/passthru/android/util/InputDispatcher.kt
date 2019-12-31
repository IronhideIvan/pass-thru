package com.passthru.android.util

import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import com.google.gson.Gson
import com.passthru.android.ui.notifications.DebuggerFragment
import java.util.*

object InputDispatcher {
    private val inputHelper: InputHelper = InputHelper()
    private var inputReport: InputReport = InputReport()
    private var sentInputReport: InputReport = InputReport()
    private val queue = LinkedList<InputReport>()
    private var clearInputs: Boolean = false

    fun dispatchMotionEvent(ev: MotionEvent): InputReport? {
        var pte: InputReport = InputReport().copy(inputReport)
        var pteLast = inputHelper.convert(ev, inputReport, -1)
        inputReport = pteLast
        debugInputReport(pte)

        if(!UdpHelper.isConnected()){
            return null
        }

        (0 until ev.historySize).forEach {i ->
            pte = inputHelper.convert(ev, pte, i)
            queue.addLast(pte)
        }

        queue.addLast(pteLast)
        return pteLast
    }

    fun dispatchKeyEvent(event: KeyEvent): InputReport? {
        val pte = inputHelper.convert(event, inputReport)
        inputReport = pte
        debugInputReport(pte)

        if(!UdpHelper.isConnected()){
            return null
        }

        queue.addLast(pte)
        return pte
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
            val qteToSend = InputReport()
            val forceSend = clearInputs

            if(debugMode){
                Log.d("Queue", "Size: " + queueSize.toString())
            }

            if(clearInputs){
                queue.clear()
                clearInputs = false
            }
            else if (queueSize == 1) {
                qteToSend.copy(queue.removeFirst())
            } else {
                val lastReport = queue[queueSize - 1]
                var firstReport = queue.removeFirst().buttonReport

                // We always send the first button and last axis reports
                qteToSend.axisReport.copy((lastReport.axisReport))
                qteToSend.buttonReport.copy(firstReport)

                // We only care about the last motion inputs since a half millimeter of motion
                // is negligible and would only serve to overload the server with too many events
                // to process
                qteToSend.axisReport.copy(lastReport.axisReport)

                // Remove duplicates from the button report so that we only
                // ever try to send information if something has changed between two events.
                // --
                // The index starts at 1 because we've already removed the first report from the
                // queue.
                var index = 1
                while (index < queueSize) {
                    var currentReport = queue[0].buttonReport
                    if (firstReport.areEqual(currentReport)) {
                        queue.removeFirst()
                    }

                    ++index
                }
            }

            // Avoid sending the same thing multiple times to reduce bandwidth
            if (forceSend || !qteToSend.areEqual(sentInputReport)) {
                qteToSend.messageTimestamp = System.currentTimeMillis()
                val json = toJson(qteToSend)

                if(debugMode){
                    Log.d("Queue", "Packet: " + json)
                }

                UdpHelper.sendUdp(json)
                sentInputReport = qteToSend
            }
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