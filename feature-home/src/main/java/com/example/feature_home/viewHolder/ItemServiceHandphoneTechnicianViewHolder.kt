package com.example.feature_home.viewHolder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.afollestad.recyclical.ViewHolder
import com.example.feature_home.R

class ItemServiceHandphoneTechnicianViewHolder(val view: View) : ViewHolder(view) {
    val tvServiceHpCustomerName: TextView = view.findViewById(R.id.tv_service_hp_customer_name)
    val tvServiceHpStatus: TextView = view.findViewById(R.id.tv_service_hp_status)
    val tvServiceHpDate: TextView = view.findViewById(R.id.tv_service_hp_date)
    val tvServiceHpType: TextView = view.findViewById(R.id.tv_service_hp_type)
    val tvServiceHpDamageType: TextView = view.findViewById(R.id.tv_service_hp_damage_type)
    val ivServiceHpUserPhoto: ImageView = view.findViewById(R.id.iv_service_hp_user_photo)

    val layoutCard: CardView = view.findViewById(R.id.card_service_technician)
}