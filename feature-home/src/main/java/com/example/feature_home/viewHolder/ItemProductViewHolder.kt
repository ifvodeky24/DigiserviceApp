package com.example.feature_home.viewHolder

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.afollestad.recyclical.ViewHolder
import com.example.feature_home.R

class ItemProductViewHolder(view: View) : ViewHolder(view) {
    val ivProductPhoto: ImageView = view.findViewById(R.id.iv_product_photo)
    val tvProductName: TextView = view.findViewById(R.id.tv_product_name)
    val tvProductDesciption: TextView = view.findViewById(R.id.tv_product_description)
    val tvPrice: TextView = view.findViewById(R.id.tv_Price)
    //val ibMore: ImageButton = view.findViewById(R.id.ib_more)
    val layoutCard: LinearLayout = view.findViewById(R.id.layoutCard)

}