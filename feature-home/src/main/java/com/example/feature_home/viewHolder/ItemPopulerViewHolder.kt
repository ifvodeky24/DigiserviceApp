package com.example.feature_home.viewHolder

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.afollestad.recyclical.ViewHolder
import com.example.feature_home.R

class ItemPopulerViewHolder (view: View) : ViewHolder(view) {
    val ivTeknisi: ImageView = view.findViewById(R.id.iv_teknisi)
    val tvTeknisiName: TextView = view.findViewById(R.id.tv_teknisi_name)
    val tvRating: TextView = view.findViewById(R.id.tv_description)
    val layoutCard: LinearLayout = view.findViewById(R.id.layoutCard)
}