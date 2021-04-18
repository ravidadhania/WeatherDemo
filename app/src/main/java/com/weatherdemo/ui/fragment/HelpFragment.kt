package com.weatherdemo.ui.fragment

import android.os.Bundle
import com.weatherdemo.R

import com.weatherdemo.base.BaseFragment
import com.weatherdemo.ui.activity.MainActivity
import kotlinx.android.synthetic.main.toolbar_center.*

class HelpFragment : BaseFragment() {


    override fun getLayoutId(): Int {
        return R.layout.fragment_help
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setTitle()
        ivMenu.setOnClickListener {
            (activity as MainActivity).openCloseDrawer()
        }
    }

    private fun setTitle() {
        tvTitle.text = getString(R.string.help)
    }
}