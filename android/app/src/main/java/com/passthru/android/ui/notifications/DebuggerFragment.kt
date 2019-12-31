package com.passthru.android.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.passthru.android.R
import com.passthru.android.util.Globals
import com.passthru.android.util.InputReport
import kotlinx.android.synthetic.main.fragment_debugger.*

class DebuggerFragment : Fragment() {

    private lateinit var debuggerViewModel: DebuggerViewModel
    private lateinit var tvXAxis: TextView
    private lateinit var tvYAxis: TextView
    private lateinit var tvZAxis: TextView
    private lateinit var tvRxAxis: TextView
    private lateinit var tvRyAxis: TextView
    private lateinit var tvRzAxis: TextView
    private lateinit var tvThrottle: TextView
    private lateinit var tvBrake: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        debuggerViewModel =
            ViewModelProviders.of(this).get(DebuggerViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_debugger, container, false)
        val textView: TextView = root.findViewById(R.id.text_notifications)
        debuggerViewModel.text.observe(this, Observer {
            textView.text = it
        })

        tvXAxis = root.findViewById((R.id.lblAxisXVal))
        tvYAxis = root.findViewById((R.id.lblAxisYVal))
        tvZAxis = root.findViewById((R.id.lblAxisZVal))
        tvRxAxis = root.findViewById((R.id.lblAxisRxVal))
        tvRyAxis = root.findViewById((R.id.lblAxisRyVal))
        tvRzAxis = root.findViewById((R.id.lblAxisRzVal))
        tvThrottle = root.findViewById((R.id.lblThrottleVal))
        tvBrake = root.findViewById((R.id.lblBrakeVal))

        val cbDebugMode: CheckBox = root.findViewById((R.id.cbDebugMode))
        cbDebugMode.setOnCheckedChangeListener{ _, isChecked ->
            Globals.debugMode.set(isChecked)
            if(isChecked){
                Globals.debuggerFragment.set(this)
            }
            else{
                Globals.debuggerFragment.set(null)
            }
        }

        Globals.debuggerFragment.set(this)

        return root
    }

    fun populateDebug(report: InputReport) {
        tvXAxis.setText(report.axisReport.axis1.x.toString())
        tvYAxis.setText(report.axisReport.axis1.y.toString())
        tvZAxis.setText(report.axisReport.axis1.z.toString())
        tvRxAxis.setText(report.axisReport.axis2.x.toString())
        tvRyAxis.setText(report.axisReport.axis2.y.toString())
        tvRzAxis.setText(report.axisReport.axis2.z.toString())
        tvThrottle.setText(report.axisReport.throttle.toString())
        tvBrake.setText(report.axisReport.brake.toString())
    }
}