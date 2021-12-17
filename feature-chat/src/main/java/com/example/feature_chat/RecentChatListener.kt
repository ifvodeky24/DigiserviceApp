package com.example.feature_chat

import com.example.feature_chat.models.ChatMessage

interface RecentChatListener {
    fun OnClick(chatMessage: ChatMessage)
}