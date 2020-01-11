package com.passthru.android.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.passthru.android.R
import com.passthru.android.util.ActionDispatcher
import com.passthru.android.util.PrefsHelper
import com.passthru.android.util.UdpHelper

class ControllerFragment : Fragment() {

    private lateinit var controllerViewModel: ControllerViewModel
    private lateinit var statusLabel: TextView
    private lateinit var connectButton: Button
    private lateinit var clearButton: Button
    private lateinit var disconnectButton: Button
    private lateinit var controlModeGroup: RadioGroup
    private lateinit var rbJoypad: RadioButton
    private lateinit var rbMouse: RadioButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        controllerViewModel =
            ViewModelProviders.of(this).get(ControllerViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_controller, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        controllerViewModel.text.observe(this, Observer {
            textView.text = it
        })

        statusLabel = root.findViewById(R.id.tvStatusVal)
        if(UdpHelper.isConnected()){
            statusLabel.setText("Connected")
        }
        else{
            statusLabel.setText("Not Connected")
        }

        // Setup the buttons
        connectButton = root.findViewById(R.id.btnControllerConnect)
        buildConnectButton()

        disconnectButton = root.findViewById(R.id.btnControllerRemove)
        buildDisconnectButton()

        clearButton = root.findViewById(R.id.btnControllerClear)
        buildClearButton()

        controlModeGroup = root.findViewById(R.id.rgControlMode)
        rbMouse = root.findViewById(R.id.rbMouse)
        rbJoypad = root.findViewById(R.id.rbJoystick)
        buildControlModeRadioGroup(root)

        return root
    }

    private fun buildConnectButton() {
        connectButton.setOnClickListener{
            if(UdpHelper.isConnected()){
                Toast.makeText(context, "Already connected", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val prefs = PrefsHelper.prefs
            if(prefs == null){
                Toast.makeText(context, "Server not configured", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val ipAddress = prefs.getString(PrefsHelper.KEY_IP_ADDRESS, "")
            val port = prefs.getInt(PrefsHelper.KEY_PORT, 0)

            if(port == 0 || ipAddress == ""){
                Toast.makeText(context, "Server not configured", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            UdpHelper.connect(ipAddress, port)
            Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show()
            statusLabel.setText("Connected")
        }
    }

    private fun buildClearButton() {
        clearButton.setOnClickListener{
            if(!UdpHelper.isConnected()){
                Toast.makeText(context, "Not connected", Toast.LENGTH_SHORT).show()
            }
            else{
                ActionDispatcher.clearInputs()
                Toast.makeText(context, "Cleared", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun buildDisconnectButton() {
        disconnectButton.setOnClickListener{
            if(!UdpHelper.isConnected()) {
                Toast.makeText(context, "Already disconnected", Toast.LENGTH_SHORT).show()
            }
            else {
                UdpHelper.disconnect()
                statusLabel.setText("Not connected")
                Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun buildControlModeRadioGroup(root: View) {
        if(ActionDispatcher.getMode() == "Mouse"){
            rbMouse.isChecked = true
        }
        else{
            rbJoypad.isChecked = true
        }

        controlModeGroup.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener{_, checkedId ->
                if(rbMouse.id == checkedId) {
                    ActionDispatcher.setMode("Mouse")
                }
                else{
                    ActionDispatcher.setMode("Input")
                }
            }
        )
    }
}