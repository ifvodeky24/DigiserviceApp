package com.example.feature_home.viewHolder

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.afollestad.recyclical.ViewHolder
import com.example.feature_home.R

class ItemHistoryBuyProduct(view: View) : ViewHolder(view) {
    val tvProductName: TextView = view.findViewById(R.id.tv_product_name)
    val tvSellerName: TextView = view.findViewById(R.id.tv_seller_name)
    val tvProductPrice: TextView = view.findViewById(R.id.tv_product_price)
    val tvProductBuyDate: TextView = view.findViewById(R.id.tv_product_buy_date)
    val tvProductBuyStatus: TextView = view.findViewById(R.id.tv_product_buy_status)
    val ivProductPhoto: ImageView = view.findViewById(R.id.iv_product_photo)

    val btnGiveReview: Button = view.findViewById(R.id.btn_review)
    val btnProductCancel: Button = view.findViewById(R.id.btn_product_cancel)
    val btnProductFinish: Button = view.findViewById(R.id.btn_product_finish)

    val buttonActionContainer: LinearLayout = view.findViewById(R.id.button_action_container)
}