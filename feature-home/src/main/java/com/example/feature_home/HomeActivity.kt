package com.example.feature_home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.core_util.BaseActivity
import com.example.feature_home.databinding.ActivityHomeBinding

class HomeActivity : BaseActivity() {

    private val binding by lazy {
        ActivityHomeBinding.inflate(layoutInflater)
    }

    private val navController by lazy { findNavController(R.id.home_navigation) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()
        binding.bottomNav.setupWithNavController(navController)
    }
}