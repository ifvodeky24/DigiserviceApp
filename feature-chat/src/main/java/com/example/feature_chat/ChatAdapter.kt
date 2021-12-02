package com.example.feature_chat

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.core_data.APP_PRODUCT_IMAGES_URL
import com.example.feature_chat.databinding.ItemContainerReceivedMessageBinding
import com.example.feature_chat.databinding.ItemContainerSentMessageBinding
import com.example.feature_chat.models.ChatMessage

class ChatAdapter(
    private val receiverPhoto: String,
    private val chatMessages: ArrayList<ChatMessage>,
    private val senderId: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            SentMessageViewHolder(
                ItemContainerSentMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            ReceivedMessageViewHolder(
                ItemContainerReceivedMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            (holder as SentMessageViewHolder).setData(chatMessages[position])
        } else {
            (holder as ReceivedMessageViewHolder).setData(
                chatMessages[position],
                receiverPhoto
            )
        }
    }

    override fun getItemCount(): Int {
        return chatMessages.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatMessages[position].senderId.equals(senderId)) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }

    internal class SentMessageViewHolder(itemContainerSentMessageBinding: ItemContainerSentMessageBinding) :
        RecyclerView.ViewHolder(itemContainerSentMessageBinding.root) {

        private val binding: ItemContainerSentMessageBinding = itemContainerSentMessageBinding

        fun setData(chatMessage: ChatMessage) {
            binding.textMessage.text = chatMessage.message
            binding.textDateTime.text = chatMessage.dateTime
        }

    }

    internal class ReceivedMessageViewHolder(itemContainerReceivedMessageBinding: ItemContainerReceivedMessageBinding) :
        RecyclerView.ViewHolder(itemContainerReceivedMessageBinding.getRoot()) {
        private val binding: ItemContainerReceivedMessageBinding =
            itemContainerReceivedMessageBinding

        fun setData(chatMessage: ChatMessage, receiverPhoto: String) {
            binding.textMessage.text = chatMessage.message
            binding.textDateTime.text = chatMessage.dateTime

            Glide.with(binding.root)
                .load(APP_PRODUCT_IMAGES_URL +receiverPhoto)
                .fitCenter()
                .into(binding.imageProfile)
        }

    }

    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
    }

}