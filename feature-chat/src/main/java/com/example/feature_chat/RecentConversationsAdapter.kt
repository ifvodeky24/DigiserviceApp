package com.example.feature_chat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.core_data.APP_PELANGGAN_IMAGES_URL
import com.example.core_data.APP_TEKNISI_IMAGES_URL
import com.example.core_util.Constants
import com.example.feature_chat.databinding.ItemContainerRecentChatBinding
import com.example.feature_chat.models.ChatMessage
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import timber.log.Timber

class RecentConversationsAdapter(
    private val chatMessages: List<ChatMessage>,
    private val listener: RecentChatListener
) :
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

            Timber.d("Sssssssssss ${chatMessage.conversionName}")
            val database = FirebaseFirestore.getInstance()
            database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo("name", chatMessage.conversionName)
                .get()
                .addOnCompleteListener { task: Task<QuerySnapshot?> ->
                    if (task.isSuccessful && task.result != null && task.result!!.documents.size > 0) {
                        val teknisiId = task.result!!.documents[0].get("teknisiId")
                        Timber.d("sdsdsd $teknisiId")

                        val path = if (teknisiId.toString() != "0") {
                            APP_TEKNISI_IMAGES_URL+chatMessage.conversionImage
                        } else {
                            APP_PELANGGAN_IMAGES_URL+chatMessage.conversionImage
                        }

                        Timber.d("sdsdsdsssssss $path)")
                        Glide
                            .with(binding.root)
                            .load(path)
                            .centerCrop()
                            .into(binding.imageProfile)
                    } else {
                        Timber.d("Errorsdsds")
                    }
                }

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