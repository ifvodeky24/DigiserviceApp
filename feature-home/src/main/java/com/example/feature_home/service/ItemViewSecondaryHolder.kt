package com.example.feature_home.service

import android.view.View
import android.widget.CheckBox
import com.afollestad.recyclical.ViewHolder
import com.example.feature_home.R

class ItemViewSecondaryHolder (view: View) : ViewHolder(view) {
    val titleCheckBox: CheckBox = view.findViewById(R.id.checkbox_item)
}