package com.passthru.android

import android.content.Context
import android.drm.DrmStore
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.WindowManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.passthru.android.util.*
import kotlinx.coroutines.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private val PREFS_FILENAME = "com.passthru.android.prefs"
    private val threadJob: Job = Job()
    private val threadScope = CoroutineScope(Dispatchers.Default + threadJob)
    private var threadLaunched: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        // Keep the screen on
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

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

        if(!threadLaunched){
            threadLaunched = true
            threadScope.launch {
                var errCount: Int = 0
                lateinit var exception: Exception;
                while(true){
                    try{
                        ActionDispatcher.checkAndSendInputMessage()
                        errCount = 0
                    }
                    catch (e: Exception){
                        ++errCount
                        exception = e;
                    }

                    if(errCount > 5){
                        throw Exception("Coroutine failed too many times consecutively, aborting program.", exception)
                    }

                    delay(10)
                }
            }
        }
    }

    override fun dispatchGenericMotionEvent(ev: MotionEvent?): Boolean {
        if(ev == null){
            return true
        }

        val handled = ActionDispatcher.dispatchMotionEvent(ev)
        if(!handled){
            return super.dispatchGenericMotionEvent(ev)
        }

        return true
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if(event == null){
            return true
        }

        val handled = ActionDispatcher.dispatchKeyEvent(event)
        if(!handled){
            return super.dispatchKeyEvent(event)
        }

        return true
    }
}
