package com.passthru.android.ui.notifications

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.passthru.android.R
import com.passthru.android.util.ActionDispatcher
import com.passthru.android.util.Globals
import com.passthru.android.util.InputDispatcher
import com.passthru.android.util.UdpHelper
import com.passthru.android.util.models.InputReport
import java.security.Key
import kotlin.random.Random

class DebuggerFragment : Fragment() {

    private lateinit var vm: DebuggerViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vm =
            ViewModelProviders.of(this).get(DebuggerViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_debugger, container, false)
        val textView: TextView = root.findViewById(R.id.text_notifications)
        val tvXAxis: TextView = root.findViewById((R.id.lblAxisXVal))
        val tvYAxis: TextView = root.findViewById((R.id.lblAxisYVal))
        val tvZAxis: TextView = root.findViewById((R.id.lblAxisZVal))
        val tvRxAxis: TextView = root.findViewById((R.id.lblAxisRxVal))
        val tvRyAxis: TextView = root.findViewById((R.id.lblAxisRyVal))
        val tvRzAxis: TextView = root.findViewById((R.id.lblAxisRzVal))
        val tvThrottle: TextView = root.findViewById((R.id.lblThrottleVal))
        val tvBrake: TextView = root.findViewById((R.id.lblBrakeVal))
        val tvButtons: TextView = root.findViewById((R.id.lblButtonsVal))

        vm.headerText.observe(this, Observer {
            textView.text = it
        })

        vm.xAxis.observe(this, Observer {
            tvXAxis.text = it
        })

        vm.yAxis.observe(this, Observer {
            tvYAxis.text = it
        })

        vm.zAxis.observe(this, Observer {
            tvZAxis.text = it
        })

        vm.rXAxis.observe(this, Observer {
            tvRxAxis.text = it
        })

        vm.rYAxis.observe(this, Observer {
            tvRyAxis.text = it
        })

        vm.rZAxis.observe(this, Observer {
            tvRzAxis.text = it
        })

        vm.throttle.observe(this, Observer {
            tvThrottle.text = it
        })

        vm.brake.observe(this, Observer {
            tvBrake.text = it
        })

        vm.buttons.observe(this, Observer {
            tvButtons.text = it
        })

        val cbDebugMode: CheckBox = root.findViewById((R.id.cbDebugMode))
        cbDebugMode.isChecked = Globals.debugMode.get()
        Globals.debuggerFragment.set(this)

        cbDebugMode.setOnCheckedChangeListener{ _, isChecked ->
            Globals.debugMode.set(isChecked)
            if(isChecked){
                Globals.debuggerFragment.set(this)
            }
            else{
                Globals.debuggerFragment.set(null)
            }
        }

        val btnSendSignal: Button = root.findViewById((R.id.btnSendMotionSignal))
        btnSendSignal.setOnClickListener{
            if(!UdpHelper.isConnected()){
                Toast.makeText(context, "Not connected", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val motionEventCodes = intArrayOf(MotionEvent.AXIS_X, MotionEvent.AXIS_Y, MotionEvent.AXIS_Z, MotionEvent.AXIS_RX, MotionEvent.AXIS_RY, MotionEvent.AXIS_RZ, MotionEvent.AXIS_THROTTLE, MotionEvent.AXIS_BRAKE)
            val motionEvent = MotionEvent.obtain(1, 1, motionEventCodes[Random.nextInt(0, motionEventCodes.size)], Random.nextFloat(), Random.nextFloat(), 0)
            ActionDispatcher.dispatchMotionEvent(motionEvent)
        }

        val btnSendButton: Button = root.findViewById((R.id.btnSendButtonSignal))
        btnSendButton.setOnClickListener{
            if(!UdpHelper.isConnected()){
                Toast.makeText(context, "Not connected", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val actionCodes = intArrayOf(KeyEvent.ACTION_UP, KeyEvent.ACTION_DOWN)
            val btnEventCodes = intArrayOf(KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.KEYCODE_BUTTON_X,
                KeyEvent.KEYCODE_BUTTON_Y, KeyEvent.KEYCODE_BUTTON_A, KeyEvent.KEYCODE_BUTTON_B)

            val keyEvent: KeyEvent = KeyEvent(1, 1,
                actionCodes[Random.nextInt(0, actionCodes.size)],
                btnEventCodes[Random.nextInt(0, btnEventCodes.size)],
            0, 0, 0, 0, 0, 0)

            ActionDispatcher.dispatchKeyEvent(keyEvent)
        }

        return root
    }

    fun populateDebug(report: InputReport) {
        vm.xAxis.value = report.axisReport.axis1.x.toString()
        vm.yAxis.value = report.axisReport.axis1.y.toString()
        vm.zAxis.value = report.axisReport.axis1.z.toString()
        vm.rXAxis.value = report.axisReport.axis2.x.toString()
        vm.rYAxis.value = report.axisReport.axis2.y.toString()
        vm.rZAxis.value = report.axisReport.axis2.z.toString()
        vm.throttle.value = report.axisReport.throttle.toString()
        vm.brake.value = report.axisReport.brake.toString()
        vm.buttons.value = report.buttonReport.buttons.toString()
    }
}