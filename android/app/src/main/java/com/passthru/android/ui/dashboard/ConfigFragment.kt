package com.passthru.android.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.passthru.android.R

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
        return root
    }
}