package com.weatherdemo.callbacks

import androidx.recyclerview.widget.DiffUtil

class BaseListAdapterDiffCallBack<E> : DiffUtil.ItemCallback<E>() {
    override fun areItemsTheSame(p0: E, p1: E): Boolean {
        return p0 == p1
    }


    /* This method is called only if {@link #areItemsTheSame(T, T)} returns {@code true} for these items */
    override fun areContentsTheSame(oldItem: E, newItem: E): Boolean {
        return oldItem == newItem
    }
}