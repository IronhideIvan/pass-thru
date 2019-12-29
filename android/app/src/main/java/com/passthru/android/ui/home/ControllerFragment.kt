package com.passthru.android.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.passthru.android.R

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
        return root
    }
}