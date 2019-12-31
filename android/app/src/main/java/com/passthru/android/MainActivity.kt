package com.passthru.android

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.gson.Gson
import com.passthru.android.ui.notifications.DebuggerFragment
import com.passthru.android.util.*
import java.util.*

class MainActivity : AppCompatActivity() {

    val PREFS_FILENAME = "com.passthru.android.prefs"
    val inputHelper: InputHelper = InputHelper()
    var inputReport: InputReport = InputReport()
    val localDebugMode = true
    val timer = Timer("inputSenderSchedule", true)
    val queue = LinkedList<InputReport>();
    var isScheduleRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_controller, R.id.navigation_config, R.id.navigation_debug
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Initialize the prefs
        val prefs = getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
        PrefsHelper.prefs = prefs

        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                checkAndSendInputMessage()
            }
        }, 100, 10)
    }

    var index: Int = 1
    override fun dispatchGenericMotionEvent(ev: MotionEvent?): Boolean {
        index++
        if(ev == null){
            return true
        }

        var pte: InputReport = inputReport;

        (0 until ev.historySize).forEach {i ->
            pte = inputHelper.convert(ev, pte, i)
            queue.addLast(pte)
        }

        pte = inputHelper.convert(ev, inputReport, -1)
        inputReport = pte
        debugInputReport()

        queue.addLast(pte)

        if(!UdpHelper.isConnected()){
            return super.dispatchGenericMotionEvent(ev)
        }

        return true
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if(event == null){
            return true
        }

        val pte = inputHelper.convert(event, inputReport)
        inputReport = pte
        debugInputReport()

        queue.addLast(pte)

        if(!UdpHelper.isConnected()){
            return super.dispatchKeyEvent(event)
        }

        return true
    }

    private fun debugInputReport(){
        val frag = Globals.debuggerFragment
        if(localDebugMode && frag != null && frag is DebuggerFragment){
            frag.populateDebug(inputReport)
        }
    }

    private fun toJson(pte: InputReport): String{
        val gson = Gson()
        return gson.toJson(pte)
    }

    private fun checkAndSendInputMessage() {
        if(isScheduleRunning){
            return
        }

        isScheduleRunning = true
        if(queue.size > 0 && UdpHelper.isConnected()) {
            // Events might be appending to the queue while we are reading it, so we want to
            // only work with what we have at this very moment
            val queueSize = queue.size
            val qteToSend = InputReport()

            if(queueSize == 1){
                qteToSend.copy(queue.removeFirst())
            }
            else{
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
                while (index < queueSize){
                    var currentReport = queue[0].buttonReport
                    if(firstReport.areEqual(currentReport)){
                        queue.removeFirst()
                    }

                    ++index
                }
            }

            qteToSend.messageTimestamp = System.currentTimeMillis()
            UdpHelper.sendUdp(toJson(qteToSend))
        }
        isScheduleRunning = false
    }
}
