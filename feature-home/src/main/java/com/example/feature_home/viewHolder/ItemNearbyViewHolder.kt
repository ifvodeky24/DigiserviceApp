package com.example.feature_home.viewHolder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.afollestad.recyclical.ViewHolder
import com.example.feature_home.R

class ItemNearbyViewHolder (view: View) : ViewHolder(view) {
    val ivTeknisi: ImageView = view.findViewById(R.id.iv_teknisi)
    val tvTeknisiName: TextView = view.findViewById(R.id.tv_teknisi_name)
}