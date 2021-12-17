package com.example.feature_chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.core_navigation.ModuleNavigator
import com.example.feature_chat.databinding.ActivityChatBinding
import timber.log.Timber

class ChatActivity : AppCompatActivity(), ModuleNavigator.ChatNav {

    val status by statusParam()

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