package com.passthru.android.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.passthru.android.R

class DebuggerFragment : Fragment() {

    private lateinit var debuggerViewModel: DebuggerViewModel

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
        return root
    }
}