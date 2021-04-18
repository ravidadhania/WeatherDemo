package com.weatherdemo.ui.fragment

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.weatherdemo.R

import com.weatherdemo.base.BaseFragment
import com.weatherdemo.callbacks.AdapterViewClickListener
import com.weatherdemo.entity.CityMaster
import com.weatherdemo.room.AppDatabase
import com.weatherdemo.ui.activity.MainActivity
import com.weatherdemo.ui.adapter.CityAdapter
import kotlinx.android.synthetic.main.fragment_city.*
import kotlinx.android.synthetic.main.toolbar_center.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class CityFragment : BaseFragment() {

    private var cityMasterList = ArrayList<CityMaster>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_city
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setTitle()
        getCityMasterList()

        ivMenu.setOnClickListener {
            (activity as MainActivity).openCloseDrawer()
        }
    }

    //Set Screen title
    private fun setTitle() {
        tvTitle.text = getString(R.string.home)
    }

    //Get City List From Local DB and Set Adapter for it
    private fun getCityMasterList() {
        cityMasterList = AppDatabase.get(activity!!.application).getCityMasterDao()
            .getCityList() as java.util.ArrayList<CityMaster>

        if (cityMasterList.size > 0) {
            setAdapterForCity(cityMasterList)
        } else {
            (activity as MainActivity).openMapFragment()
        }
    }

    //Adapter for City master list
    private fun setAdapterForCity(list: List<CityMaster?>?) {
        rvCity.visibility = View.VISIBLE

        rvCity.layoutManager = LinearLayoutManager(
            activity, RecyclerView.VERTICAL, false
        )
        val cityAdapter =
            CityAdapter(object : AdapterViewClickListener<CityMaster> {

                override fun onClickAdapterView(
                    objectAtPosition: CityMaster?,
                    viewType: Int,
                    position: Int
                ) {
                    if (viewType == 0) {
                        (activity!! as MainActivity).openCityDetailFragment(
                            objectAtPosition!!.latitude,
                            objectAtPosition.longitude, objectAtPosition.City
                        )
                    }
                    if (viewType == 1) {
                        showAlertDialogForDelete(
                            activity!!,
                            getString(R.string.delete_alert),
                            objectAtPosition!!.IDNumber
                        )
                    }
                }
            })
        rvCity.adapter = cityAdapter
        cityAdapter.submitList(list)
    }

    //Alert dialog for delete city
    fun showAlertDialogForDelete(context: Context?, message: String?, position: Int?) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(
            ContextThemeWrapper(context, R.style.DialogTheme)
        )

        builder.setMessage(message).setCancelable(false)
            .setPositiveButton(getString(R.string.ok)) { dialog, which ->

                CoroutineScope(IO).launch {
                    deleteCityFromLocalDB(position.toString(), activity!!.application)
                }
                Toast.makeText(activity!!, R.string.city_deleted_successfully, Toast.LENGTH_SHORT)
                    .show()

            }.setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                dialog.cancel()
            }.show()
    }

    //Delete City From Local DB
    private fun deleteCityFromLocalDB(idNumber: String, application: Application) {
        AppDatabase.get(application).getCityMasterDao().deleteCity(idNumber)
        getCityMasterList()
    }
}