package com.example.feature_chat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.feature_chat.databinding.ItemContainerRecentChatBinding
import com.example.feature_chat.models.ChatMessage
import timber.log.Timber

class RecentConversationsAdapter(private val chatMessages: List<ChatMessage>, private val listener: RecentChatListener) :
    RecyclerView.Adapter<RecentConversationsAdapter.ConversionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversionViewHolder {
        return ConversionViewHolder(
            ItemContainerRecentChatBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ConversionViewHolder, position: Int) {
        holder.setData(chatMessages[position])
    }

    override fun getItemCount(): Int {
        return chatMessages.size
    }

    inner class ConversionViewHolder(var binding: ItemContainerRecentChatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setData(chatMessage: ChatMessage) {
//            binding.imageProfile.setImageBitmap(getConversionImage(chatMessage.conversionImage))

            binding.textName.text = chatMessage.conversionName
            binding.textRecentMessage.text = chatMessage.message
            binding.textDateTime.text = chatMessage.dateTime
            binding.root.setOnClickListener {
                Timber.d("mwewewewe $chatMessage")
                listener.OnClick(chatMessage)
            }
        }
    }

    private fun getConversionImage(encodedImage: String?): Bitmap {
        val bytes = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}