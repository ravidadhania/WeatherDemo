package com.weatherdemo.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.weatherdemo.R

import com.weatherdemo.base.BaseFragment
import com.weatherdemo.base.IResponseParser
import com.weatherdemo.callbacks.AdapterViewClickListener
import com.weatherdemo.constants.MyConfig
import com.weatherdemo.custompreference.CustomPreference
import com.weatherdemo.model.response.FilteredFiveDayResponseModel

import com.weatherdemo.model.response.FiveDayForeCastResponseModel
import com.weatherdemo.model.response.TodaysForecastResponseModel
import com.weatherdemo.network.RestResponse
import com.weatherdemo.ui.activity.MainActivity

import com.weatherdemo.ui.adapter.FiveDayForeCastAdapter
import com.weatherdemo.ui.adapter.TodayForeCastAdapter
import com.weatherdemo.utils.AndroidUtils
import kotlinx.android.synthetic.main.fragment_city_detail.*
import kotlinx.android.synthetic.main.toolbar_center.*

class CityDetailFragment : BaseFragment() {
    private var latitude = 0.0
    private var longitude = 0.0
    private var city = ""
    private var units: String = ""

    private var todayForecastResponseModel: TodaysForecastResponseModel? = null
    private var fiveDayForeCastResponseModel: FiveDayForeCastResponseModel? = null

    private var filterTodayForeCastList = ArrayList<FiveDayForeCastResponseModel.Slots>()
    private var filteredFiveDayForeCastList = ArrayList<FilteredFiveDayResponseModel>()

    private var filterFirstDayForeCastList = ArrayList<FiveDayForeCastResponseModel.Slots>()
    private var filterSecondDayForeCastList = ArrayList<FiveDayForeCastResponseModel.Slots>()
    private var filterThirdDayForeCastList = ArrayList<FiveDayForeCastResponseModel.Slots>()
    private var filterFourthDayForeCastList = ArrayList<FiveDayForeCastResponseModel.Slots>()
    private var filterFifthDayForeCastList = ArrayList<FiveDayForeCastResponseModel.Slots>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_city_detail
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getDataFromBundle()
        setTitle()
        subscribe()
        apiCallForGetTodayForeCast()

        ivMenu.setOnClickListener {
            (activity as MainActivity).openCloseDrawer()
        }
    }

    //Set Screen title
    private fun setTitle() {
        tvTitle.text = city
    }

    //Get Data from bundle
    private fun getDataFromBundle() {
        latitude = arguments?.getDouble(MyConfig.Keys.latitude)!!
        longitude = arguments?.getDouble(MyConfig.Keys.longitude)!!
        city = arguments?.getString(MyConfig.Keys.city)!!

        units = CustomPreference.getPreference(activity!!, MyConfig.PreferenceKeys.unit).toString()
    }

    //API Call For today forecast
    private fun apiCallForGetTodayForeCast() {
        vmCommon.apiGetTodayForeCast(
            latitude,
            longitude,
            getString(R.string.weather_api_key),
            units
        )
    }

    //API Call For Five Days forecast
    private fun apiCallForGetFiveDaysForeCast() {
        vmCommon.apiGetFiveDaysForeCast(
            latitude,
            longitude,
            getString(R.string.weather_api_key),
            units
        )
    }

    private fun subscribe() {

        //API response of today forecast
        vmCommon.eventGetTodayForeCast.observe(activity!!, {
            observationOfAPI(it, object : IResponseParser<TodaysForecastResponseModel>(this) {
                override fun onSuccess(it: RestResponse<TodaysForecastResponseModel>) {
                    super.onSuccess(it)

                    if (it.status != RestResponse.Status.ERROR) {
                        todayForecastResponseModel = it.data

                        if (todayForecastResponseModel == null)
                            showSnackBar("no data found", true)
                        else {
                            setData()
                            apiCallForGetFiveDaysForeCast()
                        }

                    } else {
                        showSnackBar("error", true)
                    }
                }

                override fun onError(it: RestResponse<TodaysForecastResponseModel>) {
                    super.onError(it)
                    showSnackBar(it.getErrorMessage(), false)
                }
            })
        })

        //API response of five days forecast
        vmCommon.eventGetFiveDaysForeCast.observe(activity!!, {
            observationOfAPI(it, object : IResponseParser<FiveDayForeCastResponseModel>(this) {
                override fun onSuccess(it: RestResponse<FiveDayForeCastResponseModel>) {
                    super.onSuccess(it)

                    if (it.status != RestResponse.Status.ERROR) {

                        fiveDayForeCastResponseModel = it.data

                        if (fiveDayForeCastResponseModel == null)
                            showSnackBar("no data found", true)
                        else {
                            filterTodayForeCast()
                            filterFiveDayForeCast(filterFirstDayForeCastList)
                            filterFiveDayForeCast(filterSecondDayForeCastList)
                            filterFiveDayForeCast(filterThirdDayForeCastList)
                            filterFiveDayForeCast(filterFourthDayForeCastList)
                            filterFiveDayForeCast(filterFifthDayForeCastList)
                            setAdapterForDayFiveForeCast(filteredFiveDayForeCastList)
                        }

                    } else {
                        showSnackBar("error", true)
                    }
                }

                override fun onError(it: RestResponse<FiveDayForeCastResponseModel>) {
                    super.onError(it)
                    showSnackBar(it.getErrorMessage(), false)
                }
            })
        })
    }

    // set initial data for today's forecast
    private fun setData() {
        llTodayForecast.visibility = View.VISIBLE
        tvCity.text = city
        tvTemp.text = todayForecastResponseModel!!.main.temp.toInt().toString()
        tvHumid.text = getString(R.string.humidity) + " : " + todayForecastResponseModel!!.main.humidity.toString()
        tvWind.text =
            getString(R.string.wind) + " : " + todayForecastResponseModel!!.wind.speed + " " + getString(
                R.string.km_h
            )
        tvMaxTemp.text = todayForecastResponseModel!!.main.temp_max.toInt().toString()
        tvMinTemp.text = todayForecastResponseModel!!.main.temp_min.toInt().toString()
        tvRain.text =
            getString(R.string.rain_probability) + " : " + todayForecastResponseModel!!.clouds.all + getString(
                R.string.percent
            )

        if (units == getString(R.string.metric)) {
            tvUnit.text = getString(R.string.celsius)
        } else {
            tvUnit.text = getString(R.string.fahrenheit)
        }
    }

    // Filter for today forecast
    private fun filterTodayForeCast() {
        for (i in fiveDayForeCastResponseModel!!.list.indices) {
            if (AndroidUtils.convertToTimeFormat(
                    MyConfig.DateFormat.yyyy_MM_dd_HH_mm_ss,
                    MyConfig.DateFormat.yyyy_MM_dd, fiveDayForeCastResponseModel!!.list[i].dt_txt
                ) ==
                AndroidUtils.getDateWithAdditionDay(MyConfig.DateFormat.yyyy_MM_dd)
            ) {
                filterTodayForeCastList.add(fiveDayForeCastResponseModel!!.list[i])
            }

            if (AndroidUtils.convertToTimeFormat(
                    MyConfig.DateFormat.yyyy_MM_dd_HH_mm_ss,
                    MyConfig.DateFormat.yyyy_MM_dd, fiveDayForeCastResponseModel!!.list[i].dt_txt
                ) ==
                AndroidUtils.getDateWithAdditionDay(MyConfig.DateFormat.yyyy_MM_dd, 1)
            ) {
                filterFirstDayForeCastList.add(fiveDayForeCastResponseModel!!.list[i])
            }

            if (AndroidUtils.convertToTimeFormat(
                    MyConfig.DateFormat.yyyy_MM_dd_HH_mm_ss,
                    MyConfig.DateFormat.yyyy_MM_dd, fiveDayForeCastResponseModel!!.list[i].dt_txt
                ) ==
                AndroidUtils.getDateWithAdditionDay(MyConfig.DateFormat.yyyy_MM_dd, 2)
            ) {
                filterSecondDayForeCastList.add(fiveDayForeCastResponseModel!!.list[i])
            }

            if (AndroidUtils.convertToTimeFormat(
                    MyConfig.DateFormat.yyyy_MM_dd_HH_mm_ss,
                    MyConfig.DateFormat.yyyy_MM_dd, fiveDayForeCastResponseModel!!.list[i].dt_txt
                ) ==
                AndroidUtils.getDateWithAdditionDay(MyConfig.DateFormat.yyyy_MM_dd, 3)
            ) {
                filterThirdDayForeCastList.add(fiveDayForeCastResponseModel!!.list[i])
            }

            if (AndroidUtils.convertToTimeFormat(
                    MyConfig.DateFormat.yyyy_MM_dd_HH_mm_ss,
                    MyConfig.DateFormat.yyyy_MM_dd, fiveDayForeCastResponseModel!!.list[i].dt_txt
                ) ==
                AndroidUtils.getDateWithAdditionDay(MyConfig.DateFormat.yyyy_MM_dd, 4)
            ) {
                filterFourthDayForeCastList.add(fiveDayForeCastResponseModel!!.list[i])
            }

            if (AndroidUtils.convertToTimeFormat(
                    MyConfig.DateFormat.yyyy_MM_dd_HH_mm_ss,
                    MyConfig.DateFormat.yyyy_MM_dd, fiveDayForeCastResponseModel!!.list[i].dt_txt
                ) ==
                AndroidUtils.getDateWithAdditionDay(MyConfig.DateFormat.yyyy_MM_dd, 5)
            ) {
                filterFifthDayForeCastList.add(fiveDayForeCastResponseModel!!.list[i])
            }
        }

        setAdapterForTodayForeCast(filterTodayForeCastList)

    }

    // Filter for five day forecast
    private fun filterFiveDayForeCast(list: ArrayList<FiveDayForeCastResponseModel.Slots>) {
        var date = ""
        var day = ""
        var humidity = 0
        var wind = 0.0
        var rain = 0
        var minTemp = 0.0
        var maxTemp = 0.0

        for (i in list.indices) {
            if (i == 0) {
                date = list[i].dt_txt
                day = AndroidUtils.getDayFromDate(
                    MyConfig.DateFormat.yyyy_MM_dd_HH_mm_ss,
                    MyConfig.DateFormat.EEEE, list[i].dt_txt
                )
                humidity = list[i].main.humidity
                wind = list[i].wind.speed
                rain = list[i].clouds.all
                minTemp = list[i].main.temp_min
                maxTemp = list[i].main.temp_max
            } else {
                if (minTemp > list[i].main.temp_min)
                    minTemp = list[i].main.temp_min
                if (maxTemp < list[i].main.temp_max)
                    maxTemp = list[i].main.temp_max

                humidity += list[i].main.humidity
                wind += list[i].wind.speed
                rain += list[i].clouds.all
            }
        }

        humidity /= list.size
        wind /= list.size
        rain /= list.size

        val filteredFiveDayResponseModel = FilteredFiveDayResponseModel(
            date = date,
            day = day,
            humidity = humidity,
            wind = wind,
            rain = rain,
            minTemp = minTemp,
            maxTemp = maxTemp
        )

        filteredFiveDayForeCastList.add(filteredFiveDayResponseModel)
    }

    // adapter for Today forecast
    private fun setAdapterForTodayForeCast(list: List<FiveDayForeCastResponseModel.Slots?>?) {
        llTodayTempForecast.visibility = View.VISIBLE

        rvTodaysForeCast.layoutManager = LinearLayoutManager(
            activity,
            RecyclerView.HORIZONTAL,
            false
        )
        val todayForeCastAdapter =
            TodayForeCastAdapter(object :
                AdapterViewClickListener<FiveDayForeCastResponseModel.Slots> {

                override fun onClickAdapterView(
                    objectAtPosition: FiveDayForeCastResponseModel.Slots?,
                    viewType: Int,
                    position: Int
                ) {

                }
            })

        rvTodaysForeCast.adapter = todayForeCastAdapter
        todayForeCastAdapter.submitList(list)
    }

    // adapter for FiveDay forecast
    private fun setAdapterForDayFiveForeCast(list: List<FilteredFiveDayResponseModel?>?) {
        llFiveDayTempForecast.visibility = View.VISIBLE

        rvFiveDayForeCast.layoutManager = LinearLayoutManager(
            activity,
            RecyclerView.HORIZONTAL,
            false
        )
        val fiveDayForeCastAdapter =
            FiveDayForeCastAdapter(object : AdapterViewClickListener<FilteredFiveDayResponseModel> {

                override fun onClickAdapterView(
                    objectAtPosition: FilteredFiveDayResponseModel?,
                    viewType: Int,
                    position: Int
                ) {

                }
            })

        rvFiveDayForeCast.adapter = fiveDayForeCastAdapter
        fiveDayForeCastAdapter.submitList(list)
    }
}