package com.passthru.android.ui.dashboard

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
import com.passthru.android.util.PrefsHelper
import com.passthru.android.util.UdpHelper

class ConfigFragment : Fragment() {

    private lateinit var configViewModel: ConfigViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        configViewModel =
            ViewModelProviders.of(this).get(ConfigViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_config, container, false)
        val textView: TextView = root.findViewById(R.id.text_dashboard)
        configViewModel.text.observe(this, Observer {
            textView.text = it
        })

        // Grab the preferences
        val prefs = PrefsHelper.prefs
        if(prefs != null){
            val ipAddressView: EditText = root.findViewById(R.id.txtIpAddress)
            val portView: EditText = root.findViewById(R.id.txtPort)

            val ipAddress = prefs.getString(PrefsHelper.KEY_IP_ADDRESS, "")
            val port = prefs.getInt(PrefsHelper.KEY_PORT, 0)

            if(ipAddress !== ""){
                ipAddressView.setText(ipAddress)
            }

            if(port > 0){
                portView.setText(Integer.toString(port))
            }
        }

        // Setup the button
        val connectButton: Button = root.findViewById(R.id.btnConnectConfig)

        connectButton.setOnClickListener{
            val ipAddressView: EditText = root.findViewById(R.id.txtIpAddress)
            val portView: EditText = root.findViewById(R.id.txtPort)
            val port = portView.text.toString();
            val ipAddress = ipAddressView.text.toString()

            if(port === "" || ipAddress === ""){
                return@setOnClickListener
            }

            val portNumber = Integer.parseInt(port)
            val prefs = PrefsHelper.prefs
            val editor = prefs!!.edit()
            if(editor != null){
                editor.putString(PrefsHelper.KEY_IP_ADDRESS, ipAddress)
                editor.putInt(PrefsHelper.KEY_PORT, portNumber)
                editor.apply()
            }

            UdpHelper.connect(ipAddress, portNumber)
            Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show()
        }

        return root
    }
}