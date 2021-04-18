package com.weatherdemo.ui.fragment

import android.app.Application
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.weatherdemo.R

import com.weatherdemo.base.BaseFragment
import com.weatherdemo.constants.MyConfig
import com.weatherdemo.custompreference.CustomPreference
import com.weatherdemo.room.AppDatabase
import com.weatherdemo.ui.activity.MainActivity
import kotlinx.android.synthetic.main.fragment_setting.*
import kotlinx.android.synthetic.main.toolbar_center.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingFragment : BaseFragment() {

    override fun getLayoutId(): Int {
        return R.layout.fragment_setting
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setTitle()
        setDefaultData()

        ivMenu.setOnClickListener {
            (activity as MainActivity).openCloseDrawer()
        }

        tvFahrenheit.setOnClickListener {
            onClickFahrenheit()
        }

        tvCelsius.setOnClickListener {
            onClickCelsius()
        }

        tvResetBookmarkedCities.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                clearCityMaster(activity!!.application)
            }
            Toast.makeText(
                activity!!,
                R.string.reset_bookmarked_city_successfully,
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    private fun setTitle() {
        tvTitle.text = getString(R.string.setting)
    }

    private fun setDefaultData() {
        if (CustomPreference.getPreference(activity, MyConfig.PreferenceKeys.unit)
                .equals(getString(R.string.metric))
        ) {
            onClickCelsius()
        } else {
            onClickFahrenheit()
        }
    }

    private fun onClickFahrenheit() {
        tvFahrenheit.background = ContextCompat.getDrawable(activity!!, R.drawable.cd_white_box)
        tvCelsius.background = null
        CustomPreference.setPreference(
            context,
            MyConfig.PreferenceKeys.unit,
            getString(R.string.imperial)
        )
    }

    private fun onClickCelsius() {
        tvCelsius.background = ContextCompat.getDrawable(activity!!, R.drawable.cd_white_box)
        tvFahrenheit.background = null
        CustomPreference.setPreference(
            context,
            MyConfig.PreferenceKeys.unit,
            getString(R.string.metric)
        )
    }

    private fun clearCityMaster(application: Application) {
        AppDatabase.get(application).getCityMasterDao().clearCityMaster()
    }
}