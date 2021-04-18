package com.weatherdemo.ui.adapter

import androidx.recyclerview.widget.ListAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.weatherdemo.R
import com.weatherdemo.callbacks.AdapterViewClickListener
import com.weatherdemo.callbacks.BaseListAdapterDiffCallBack
import com.weatherdemo.constants.MyConfig
import com.weatherdemo.custompreference.CustomPreference
import com.weatherdemo.model.response.FilteredFiveDayResponseModel
import kotlinx.android.synthetic.main.row_forecast.view.*

class FiveDayForeCastAdapter(
    private val adapterViewClickListener: AdapterViewClickListener<FilteredFiveDayResponseModel>?
) : ListAdapter<FilteredFiveDayResponseModel, FiveDayForeCastAdapter.ViewHolder>(
    BaseListAdapterDiffCallBack<FilteredFiveDayResponseModel>()
) {

    inner class ViewHolder(itemView: View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        fun bind(
            result: FilteredFiveDayResponseModel?,
            itemViewType: Int,
            adapterViewClickListener: AdapterViewClickListener<FilteredFiveDayResponseModel>?
        ) {
            setDataOnAdapterView(itemView, result, adapterViewClickListener, adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val layoutId = R.layout.row_forecast
        val itemView = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), getItemViewType(position), adapterViewClickListener)
    }

    fun setDataOnAdapterView(
        itemView: View,
        result: FilteredFiveDayResponseModel?,
        adapterViewClickListener: AdapterViewClickListener<FilteredFiveDayResponseModel>?,
        adapterPosition: Int
    ) {

        itemView.tvDay.text = result?.day
        itemView.tvHumidity.text = result?.humidity.toString()
        itemView.tvRain.text = result?.rain.toString() + itemView.context.getString(R.string.percent)
        itemView.tvWind.text = result?.wind!!.toInt().toString() + " " + itemView.context.getString(R.string.km_h)
        itemView.tvTempMin.text = result.minTemp.toInt().toString()
        itemView.tvTempMax.text = result.maxTemp.toInt().toString()

        val diff = (result.maxTemp.toInt()).minus((result.minTemp.toInt()))

        val height = if (CustomPreference.getPreference(itemView.context, MyConfig.PreferenceKeys.unit)
                .equals(itemView.context.getString(R.string.metric))) {
            (diff.times(250)).div(15)
        } else {
            (diff.times(125)).div(15)
        }

        val params: ViewGroup.LayoutParams = itemView.llBar.layoutParams
        params.width = 25
        params.height = height
        itemView.llBar.layoutParams = params

        itemView.setOnClickListener {
            adapterViewClickListener?.onClickAdapterView(result, 0, adapterPosition)
        }
    }
}