package com.example.feature_service.viewHolder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.afollestad.recyclical.ViewHolder
import com.example.feature_service.R

class ItemServiceViewHolder(view: View) : ViewHolder(view) {
    val ivService = view.findViewById<ImageView>(R.id.iv_service)
    val tvServiceName = view.findViewById<TextView>(R.id.tv_service_name)
}