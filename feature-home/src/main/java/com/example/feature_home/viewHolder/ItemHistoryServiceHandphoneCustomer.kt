package com.example.feature_home.viewHolder

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.afollestad.recyclical.ViewHolder
import com.example.feature_home.R

class ItemHistoryServiceHandphoneCustomer(view: View) : ViewHolder(view) {
    val tvServiceHpStoreName: TextView = view.findViewById(R.id.tv_service_hp_customer_name)
    val tvServiceHpTechnianName: TextView = view.findViewById(R.id.tv_service_hp_technician_name)
    val tvServiceHpStatus: TextView = view.findViewById(R.id.tv_service_hp_status)
    val tvServiceHpDate: TextView = view.findViewById(R.id.tv_service_hp_date)
    val tvServiceHpType: TextView = view.findViewById(R.id.tv_service_hp_type)
    val tvServiceHpDamageType: TextView = view.findViewById(R.id.tv_service_hp_damage_type)
    val ivServiceHpUserPhoto: ImageView = view.findViewById(R.id.iv_service_hp_user_photo)

    val btnToRiwayatService: Button = view.findViewById(R.id.btn_to_riwayat_service_detail)
}