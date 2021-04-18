package com.weatherdemo.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.view.GravityCompat
import com.weatherdemo.R
import com.weatherdemo.base.BaseActivity
import com.weatherdemo.constants.MyConfig
import com.weatherdemo.custompreference.CustomPreference
import com.weatherdemo.entity.CityMaster
import com.weatherdemo.room.AppDatabase
import com.weatherdemo.ui.fragment.*
import com.weatherdemo.utils.AndroidUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar_center.*

class MainActivity : BaseActivity() {

    companion object {
        fun getIntent(ctx: Context): Intent {
            return Intent(ctx, MainActivity::class.java)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        setDefaultData()
        checkWhichFragmentShouldBeOpen()

        tvHome.setOnClickListener {
            openCityFragment()
            openCloseDrawer()
        }

        tvMap.setOnClickListener {
            openMapFragment()
            openCloseDrawer()
        }

        tvSettings.setOnClickListener {
            openSettingFragment()
            openCloseDrawer()
        }

        tvHelp.setOnClickListener {
            openHelpFragment()
            openCloseDrawer()
        }
    }

    fun clearBackStack() {
        val fm = supportFragmentManager
        for (i in 0 until fm.backStackEntryCount) {
            fm.popBackStack()
        }
    }

    private fun setDefaultData() {
        if (CustomPreference.getPreference(this@MainActivity, MyConfig.PreferenceKeys.unit)
                .equals("")
        ) {
            CustomPreference.setPreference(
                this@MainActivity,
                MyConfig.PreferenceKeys.unit,
                getString(R.string.metric)
            )
        }
    }

    fun openCloseDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawers()
        else
            drawerLayout.openDrawer(GravityCompat.START)
    }

    private fun checkWhichFragmentShouldBeOpen() {
        var cityMasterList = ArrayList<CityMaster>()

        cityMasterList = AppDatabase.get(application).getCityMasterDao()
            .getCityList() as ArrayList<CityMaster>

        if (cityMasterList.size > 0) {
            openCityFragment()
        } else {
            openMapFragment()
        }
    }

    fun openMapFragment() {
        clearBackStack()
        AndroidUtils.replaceFragment(supportFragmentManager, R.id.frameContainer, MapFragment())
    }

    private fun openCityFragment() {
        clearBackStack()
        AndroidUtils.replaceFragment(supportFragmentManager, R.id.frameContainer, CityFragment())
    }

    private fun openSettingFragment() {
        clearBackStack()
        AndroidUtils.replaceFragment(supportFragmentManager, R.id.frameContainer, SettingFragment())
    }

    private fun openHelpFragment() {
        clearBackStack()
        AndroidUtils.replaceFragment(supportFragmentManager, R.id.frameContainer, HelpFragment())
    }

    fun openCityDetailFragment(latitude: Double, longitude: Double, city: String) {

        val fragment = CityDetailFragment()
        val bundle = Bundle()
        bundle.putDouble(MyConfig.Keys.latitude, latitude)
        bundle.putDouble(MyConfig.Keys.longitude, longitude)
        bundle.putString(MyConfig.Keys.city, city)
        fragment.arguments = bundle
        fragment.retainInstance = true

        supportFragmentManager.beginTransaction()
            .add(R.id.frameContainer, fragment)
            .addToBackStack(MyConfig.Tag.CityDetail)
            .commit()
    }
}
