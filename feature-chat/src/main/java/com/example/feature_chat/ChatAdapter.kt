package com.example.feature_chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.core_data.APP_PELANGGAN_IMAGES_URL
import com.example.core_data.APP_PRODUCT_IMAGES_URL
import com.example.core_data.APP_TEKNISI_IMAGES_URL
import com.example.core_util.Constants
import com.example.feature_chat.databinding.ItemContainerReceivedMessageBinding
import com.example.feature_chat.databinding.ItemContainerSentMessageBinding
import com.example.feature_chat.models.ChatMessage
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import timber.log.Timber

class ChatAdapter(
    private val receiverPhoto: String,
    private val chatMessages: ArrayList<ChatMessage>,
    private val senderId: String,
    private val receiverName: String
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
                receiverPhoto,
                receiverName
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

        fun setData(chatMessage: ChatMessage, receiverPhoto: String, receiverName: String) {
            binding.textMessage.text = chatMessage.message
            binding.textDateTime.text = chatMessage.dateTime


            val database = FirebaseFirestore.getInstance()
            database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo("name", receiverName)
                .get()
                .addOnCompleteListener { task: Task<QuerySnapshot?> ->
                    if (task.isSuccessful && task.result != null && task.result!!.documents.size > 0) {
                        val teknisiId = task.result!!.documents[0].get("teknisiId")
                        Timber.d("sdsdsd $teknisiId")

                        val path = if (teknisiId.toString() != "0") {
                            APP_TEKNISI_IMAGES_URL +receiverPhoto
                        } else {
                            APP_PELANGGAN_IMAGES_URL +receiverPhoto
                        }

                        Timber.d("sdsdsdsssssss $path)")
                        Glide.with(binding.root)
                            .load(path)
                            .fitCenter()
                            .into(binding.imageProfile)
                    } else {
                        Timber.d("Errorsdsds")
                    }
                }
        }

    }

    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
    }

}