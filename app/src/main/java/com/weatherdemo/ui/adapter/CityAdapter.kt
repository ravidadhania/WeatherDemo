package com.weatherdemo.ui.adapter

import androidx.recyclerview.widget.ListAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.weatherdemo.R
import com.weatherdemo.callbacks.AdapterViewClickListener
import com.weatherdemo.callbacks.BaseListAdapterDiffCallBack
import com.weatherdemo.entity.CityMaster
import kotlinx.android.synthetic.main.row_city.view.*

class CityAdapter(
    private val adapterViewClickListener: AdapterViewClickListener<CityMaster>?
) : ListAdapter<CityMaster, CityAdapter.ViewHolder>(BaseListAdapterDiffCallBack<CityMaster>()) {

    inner class ViewHolder(itemView: View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        fun bind(
            result: CityMaster?,
            itemViewType: Int,
            adapterViewClickListener: AdapterViewClickListener<CityMaster>?
        ) {
            setDataOnAdapterView(itemView, result, adapterViewClickListener, adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val layoutId = R.layout.row_city
        val itemView = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), getItemViewType(position), adapterViewClickListener)
    }

    fun setDataOnAdapterView(
        itemView: View,
        result: CityMaster?,
        adapterViewClickListener: AdapterViewClickListener<CityMaster>?,
        adapterPosition: Int
    ) {

        itemView.tvCity.text = result?.City
        itemView.ivDelete.setOnClickListener {
            adapterViewClickListener?.onClickAdapterView(result, 1, adapterPosition)
        }

        itemView.setOnClickListener {
            adapterViewClickListener?.onClickAdapterView(result, 0, adapterPosition)
        }
    }
}