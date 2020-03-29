package com.sedi.learnall.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sedi.learnall.R
import kotlinx.android.synthetic.main.learn_layout.*

class LearnFragment : BaseFragment(R.layout.learn_layout) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        text_back?.setOnClickListener {
            findNavController().navigate(R.id.show_fragment)
        }
    }
}