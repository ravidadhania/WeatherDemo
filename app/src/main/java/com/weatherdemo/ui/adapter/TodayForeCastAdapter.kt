package com.weatherdemo.ui.adapter

import androidx.recyclerview.widget.ListAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.weatherdemo.R
import com.weatherdemo.callbacks.AdapterViewClickListener
import com.weatherdemo.callbacks.BaseListAdapterDiffCallBack
import com.weatherdemo.constants.MyConfig

import com.weatherdemo.model.response.FiveDayForeCastResponseModel
import com.weatherdemo.utils.AndroidUtils
import kotlinx.android.synthetic.main.row_todays_forecast.view.*

class TodayForeCastAdapter(
    private val adapterViewClickListener: AdapterViewClickListener<FiveDayForeCastResponseModel.Slots>?
) : ListAdapter<FiveDayForeCastResponseModel.Slots, TodayForeCastAdapter.ViewHolder>(BaseListAdapterDiffCallBack<FiveDayForeCastResponseModel.Slots>()) {

    inner class ViewHolder(itemView: View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        fun bind(
            result: FiveDayForeCastResponseModel.Slots?,
            itemViewType: Int,
            adapterViewClickListener: AdapterViewClickListener<FiveDayForeCastResponseModel.Slots>?
        ) {
            setDataOnAdapterView(itemView, result, adapterViewClickListener, adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val layoutId = R.layout.row_todays_forecast
        val itemView = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), getItemViewType(position), adapterViewClickListener)
    }

    fun setDataOnAdapterView(
        itemView: View,
        result: FiveDayForeCastResponseModel.Slots?,
        adapterViewClickListener: AdapterViewClickListener<FiveDayForeCastResponseModel.Slots>?,
        adapterPosition: Int
    ) {

        itemView.tvTime.text = AndroidUtils.convertToTimeFormat(MyConfig.DateFormat.yyyy_MM_dd_HH_mm_ss,
                MyConfig.DateFormat.hh_mm_aa, result?.dt_txt)

        itemView.tvTemp.text = result?.main!!.temp.toInt().toString()
    }
}