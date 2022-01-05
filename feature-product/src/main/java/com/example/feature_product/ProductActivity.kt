package com.example.feature_product

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.core_navigation.ModuleNavigator
import com.example.feature_product.databinding.ActivityProductBinding

class ProductActivity : AppCompatActivity(), ModuleNavigator.ProductNav{

    val jualId by jualIdParam()
    val status by statusParam()

    private val binding by lazy {
        ActivityProductBinding.inflate(layoutInflater)
    }

    private val navController by lazy { findNavController(R.id.product_navigation) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()
    }

    override fun onSupportNavigateUp() = navController.navigateUp()
}