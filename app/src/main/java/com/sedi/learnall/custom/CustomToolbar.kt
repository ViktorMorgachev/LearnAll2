package com.sedi.learnall.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.sedi.learnall.R
import com.sedi.learnall.extensions.gone
import com.sedi.learnall.extensions.invisible
import com.sedi.learnall.extensions.visible
import kotlinx.android.synthetic.main.custom_toolbar.view.*

class CustomToolbar(context: Context, attributeSet: AttributeSet) :
    ConstraintLayout(context, attributeSet) {
    init {
        LayoutInflater.from(context).inflate(R.layout.custom_toolbar, this, true)
    }

    fun setTitle(text: String) {
        tv_tittle.text = text
    }

    fun hideBackButton() = iv_back.gone()

    fun showBackButton() = iv_back.invisible()

    fun onBackClick(action: (() -> Unit) = {}) {
        iv_back.visible()
        iv_back.setOnClickListener { action() }
    }

    fun onActionClick(action: (() -> Unit) = {}) {
        iv_menu_options.visible()
        iv_menu_options.setOnClickListener { action() }
    }

    fun getMenuItem(): ImageView {
        return iv_menu_options
    }
}