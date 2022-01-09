package com.example.feature_chat

import android.os.Bundle
import androidx.navigation.findNavController
import com.example.core_navigation.ModuleNavigator
import com.example.core_util.BaseActivity
import com.example.feature_chat.databinding.ActivityChatBinding

class ChatActivity : BaseActivity(), ModuleNavigator.ChatNav {

    val status by statusParam()
    val productName by productNameParam()
    val productImage by productImageParam()

    private val binding by lazy {
        ActivityChatBinding.inflate(layoutInflater)
    }

    private val navController by lazy {
        findNavController(R.id.chat_navigation)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()
    }

    override fun onSupportNavigateUp() = navController.navigateUp()
}