package com.passthru.android.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.passthru.android.R
import com.passthru.android.util.InputDispatcher
import com.passthru.android.util.PrefsHelper
import com.passthru.android.util.UdpHelper

class ControllerFragment : Fragment() {

    private lateinit var controllerViewModel: ControllerViewModel

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

        val statusLabel: TextView = root.findViewById(R.id.tvStatusVal)
        if(UdpHelper.isConnected()){
            statusLabel.setText("Connected")
        }
        else{
            statusLabel.setText("Not Connected")
        }

        // Setup the buttons
        val connectButton: Button = root.findViewById(R.id.btnControllerConnect)
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

        val disconnectButton: Button = root.findViewById(R.id.btnControllerRemove)
        disconnectButton.setOnClickListener{
            if(!UdpHelper.isConnected()){
                Toast.makeText(context, "Already disconnected", Toast.LENGTH_SHORT).show()
            }
            else{
                UdpHelper.disconnect()
                statusLabel.setText("Disconnected")
                Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT).show()
            }
        }

        val clearButton: Button = root.findViewById(R.id.btnControllerClear)
        clearButton.setOnClickListener{
            if(!UdpHelper.isConnected()){
                Toast.makeText(context, "Not connected", Toast.LENGTH_SHORT).show()
            }
            else{
                InputDispatcher.clearInputs()
                Toast.makeText(context, "Cleared", Toast.LENGTH_SHORT).show()
            }

        }

        return root
    }
}