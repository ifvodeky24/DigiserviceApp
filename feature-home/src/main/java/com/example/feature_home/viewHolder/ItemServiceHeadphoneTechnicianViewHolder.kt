package com.example.feature_home.viewHolder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.afollestad.recyclical.ViewHolder
import com.example.feature_home.R
import com.example.feature_home.databinding.ItemServiceTechnicianBinding

class ItemServiceHeadphoneTechnicianViewHolder(val view: View) : ViewHolder(view) {
    val tvServiceHpName: TextView = view.findViewById(R.id.tv_service_hp_name)
    val tvServiceHpType: TextView = view.findViewById(R.id.tv_service_hp_type)
    val tvServiceHpDamageType: TextView = view.findViewById(R.id.tv_service_hp_damage_type)
    val ivServiceCustomerPhoto: ImageView = view.findViewById(R.id.iv_service_costumer_photo)
    val layoutCard: CardView = view.findViewById(R.id.layoutCard)
}