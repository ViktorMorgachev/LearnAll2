package com.sedi.learnall.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sedi.learnall.R
import kotlinx.android.synthetic.main.show_layout.*


class ShowFragment : BaseFragment(R.layout.show_layout) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        text_edit?.setOnClickListener {
            findNavController().navigate(R.id.edit_fragment)
        }
        text_learn?.setOnClickListener {
            findNavController().navigate(R.id.learn_fragment)
        }
    }
}